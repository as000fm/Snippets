package schedules;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import schedules.data.DailyScheduleData;
import schedules.data.LocationData;

/**
 * Helper class of public static methods for scheduling the daily maintenance of locations and its equipments by technicians
 */
public class SchedulingHelper {

	/**
	 * Extract the list scheduled daily maintenances based on a fix number of days
	 * @param numberOfDays Number of days
	 * @param startDate Start date for scheduling
	 * @param locationsList List of locations and its equipments to be scheduled
	 * @param techniciansList List of technicians to assign
	 * @param openBusinessDays List of open business days based on the constant field values for java.util.Calendar.DAY_OF_WEEK
	 * @return the list scheduled daily maintenances of locations and its equipments by technicians
	 */
	public static List<DailyScheduleData> extractScheduleFixNumberOfDays(int numberOfDays, Date startDate, List<LocationData> locationsList, List<String> techniciansList, int[] openBusinessDays) {
		List<DailyScheduleData> scheduleList = new ArrayList<>();

		// If openBusinessDays is empty, use Monday to Friday as default
		if (openBusinessDays == null || openBusinessDays.length == 0) {
			openBusinessDays = getDefaultOpenBusinessDays();
		}

		// Create calendar for date manipulation
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.setTime(startDate);

		// Adjust start date if not on an open business day
		while (!isBusinessDay(calendar.get(java.util.Calendar.DAY_OF_WEEK), openBusinessDays)) {
			calendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
		}

		// Calculate total equipment counts
		int totalOnSiteEquipment = 0;
		int totalOffSiteEquipment = 0;

		for (LocationData location : locationsList) {
			totalOnSiteEquipment += location.getOnSiteEquipmentsCount();
			totalOffSiteEquipment += location.getTotalOffSiteEquipmentsSublocationsCount();
		}

		// Calculate target equipment per day
		int targetOnSitePerDay = (int) Math.ceil((double) totalOnSiteEquipment / numberOfDays);
		int targetOffSitePerDay = (int) Math.ceil((double) totalOffSiteEquipment / numberOfDays);

		// Create schedule for each day
		for (int day = 0; day < numberOfDays; day++) {
			// Create schedule for current date
			DailyScheduleData dailySchedule = new DailyScheduleData(calendar.getTime());

			// Initialize technicians maps
			for (String technician : techniciansList) {
				dailySchedule.getTechniciansLocationsMap().put(technician, new ArrayList<>());
			}

			scheduleList.add(dailySchedule);

			// Move to next business day
			do {
				calendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
			} while (!isBusinessDay(calendar.get(java.util.Calendar.DAY_OF_WEEK), openBusinessDays));
		}

		// Sort locations by equipment count
		List<LocationData> sortedLocations = new ArrayList<>(locationsList);
		sortedLocations.sort((a, b) -> {
			int aTotal = a.getOnSiteEquipmentsCount() + a.getTotalOffSiteEquipmentsSublocationsCount();
			int bTotal = b.getOnSiteEquipmentsCount() + b.getTotalOffSiteEquipmentsSublocationsCount();
			return Integer.compare(bTotal, aTotal);
		});

		// Calculate base and extra equipment per day for global distribution
		int baseEquipmentPerDay = (int) Math.floor((double) (totalOnSiteEquipment + totalOffSiteEquipment) / numberOfDays);
		int extraEquipment = (totalOnSiteEquipment + totalOffSiteEquipment) - (baseEquipmentPerDay * numberOfDays);

		// Create array to track equipment count per day
		int[] equipmentPerDay = new int[numberOfDays];

		for (int i = 0; i < numberOfDays; i++) {
			equipmentPerDay[i] = baseEquipmentPerDay + (extraEquipment > i ? 1 : 0);
		}

		// Schedule equipment for each location
		for (LocationData location : sortedLocations) {
			// Handle on-site equipment
			scheduleOnSiteEquipment(location, scheduleList, targetOnSitePerDay, equipmentPerDay);

			// Handle off-site equipment by sublocation
			scheduleOffSiteEquipment(location, scheduleList, targetOffSitePerDay, equipmentPerDay);
		}

		// Distribute locations evenly among technicians for each day
		for (DailyScheduleData dailySchedule : scheduleList) {
			distributeEquipmentsToTechnicians(dailySchedule, techniciansList);
		}

		return scheduleList;
	}

	/**
	 * Extract the list scheduled daily maintenances based on a maximum number of equipments per day
	 * @param maxNumberEquipmentsPerDay Maximum number of equipments per day
	 * @param maxNumberEquipmentsForGivenDaysMap Dictionnary of maximum number of equipments for each given day
	 * @param startDate Start date for scheduling
	 * @param locationsList List of locations and its equipments to be scheduled
	 * @param techniciansList List of technicians to assign
	 * @param openBusinessDays List of open business days based on the constant field values for java.util.Calendar.DAY_OF_WEEK
	 * @return the list scheduled daily maintenances of locations and its equipments by technicians
	 */
	public static List<DailyScheduleData> extractScheduleMaxNumberOfEquipmentsPerDay(int maxNumberEquipmentsPerDay, Map<Date, Integer> maxNumberEquipmentsForGivenDaysMap, Date startDate, List<LocationData> locationsList, List<String> techniciansList, int[] openBusinessDays) {
		List<DailyScheduleData> scheduleList = new ArrayList<>();

		// If openBusinessDays is empty, use Monday to Friday as default
		if (openBusinessDays == null || openBusinessDays.length == 0) {
			openBusinessDays = getDefaultOpenBusinessDays();
		}

		if (maxNumberEquipmentsForGivenDaysMap == null) {
			maxNumberEquipmentsForGivenDaysMap = new HashMap<>();
		}

		// Create calendar for date manipulation
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.setTime(startDate);

		// Adjust start date if not on an open business day
		while (!isBusinessDay(calendar.get(java.util.Calendar.DAY_OF_WEEK), openBusinessDays)) {
			calendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
		}

		// Calculate total number of equipment to schedule
		int totalOnSiteEquipment = 0;
		int totalOffSiteEquipment = 0;

		for (LocationData location : locationsList) {
			totalOnSiteEquipment += location.getOnSiteEquipmentsCount();
			totalOffSiteEquipment += location.getTotalOffSiteEquipmentsSublocationsCount();
		}

		// Create schedules until all equipment is allocated
		int remainingOnSite = totalOnSiteEquipment;
		int remainingOffSite = totalOffSiteEquipment;

		while ((remainingOnSite > 0) || (remainingOffSite > 0)) {
			Date currentDate = calendar.getTime();

			// Get max equipment limit for current day
			int currentDayMaxEquipment = maxNumberEquipmentsForGivenDaysMap.containsKey(currentDate) ? maxNumberEquipmentsForGivenDaysMap.get(currentDate) : maxNumberEquipmentsPerDay;

			// Create schedule for current date
			DailyScheduleData dailySchedule = new DailyScheduleData(currentDate);

			// Initialize technicians maps
			for (String technician : techniciansList) {
				dailySchedule.getTechniciansLocationsMap().put(technician, new ArrayList<>());
			}

			scheduleList.add(dailySchedule);

			// Calculate target equipment counts for even distribution
			int totalRemaining = remainingOnSite + remainingOffSite;
			int maxToSchedule = Math.min(currentDayMaxEquipment, totalRemaining);

			// Calculate target numbers for on-site and off-site
			int targetOnSite = 0;
			int targetOffSite = 0;

			if ((remainingOnSite > 0) && (remainingOffSite > 0)) {
				// Both types remaining - try to schedule evenly
				double ratioOnSite = ((double) remainingOnSite) / ((double) totalRemaining);
				targetOnSite = (int) Math.ceil(maxToSchedule * ratioOnSite);
				targetOffSite = maxToSchedule - targetOnSite;

				// Adjust if we don't have enough of either type
				if (targetOnSite > remainingOnSite) {
					targetOnSite = remainingOnSite;
					targetOffSite = Math.min(remainingOffSite, maxToSchedule - targetOnSite);
				} else if (targetOffSite > remainingOffSite) {
					targetOffSite = remainingOffSite;
					targetOnSite = Math.min(remainingOnSite, maxToSchedule - targetOffSite);
				}
			} else {
				// Only one type remaining - allocate all available capacity to it
				if (remainingOnSite > 0) {
					targetOnSite = Math.min(maxToSchedule, remainingOnSite);
				} else {
					targetOffSite = Math.min(maxToSchedule, remainingOffSite);
				}
			}

			// Schedule on-site equipment
			int onSiteScheduled = scheduleOnSiteEquipmentByPriority(locationsList, dailySchedule, targetOnSite);

			// If the on-site target has not been reached, then the excess is given to the off-site target.
			if (onSiteScheduled < targetOnSite) {
				targetOffSite += (targetOnSite - onSiteScheduled);
			}

			// Schedule off-site equipment
			int offSiteScheduled = scheduleOffSiteEquipmentByPriority(locationsList, dailySchedule, targetOffSite);

			remainingOnSite -= onSiteScheduled;
			remainingOffSite -= offSiteScheduled;

			// Move to next business day
			do {
				calendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
			} while (!isBusinessDay(calendar.get(java.util.Calendar.DAY_OF_WEEK), openBusinessDays));

			// Distribute locations evenly among technicians for the day
			distributeEquipmentsToTechnicians(dailySchedule, techniciansList);
		}

		return scheduleList;
	}

	private static boolean isBusinessDay(int dayOfWeek, int[] openBusinessDays) {
		for (int openDay : openBusinessDays) {
			if (dayOfWeek == openDay) {
				return true;
			}
		}

		return false;
	}

	private static int[] getDefaultOpenBusinessDays() {
		return new int[] { java.util.Calendar.MONDAY, java.util.Calendar.TUESDAY, java.util.Calendar.WEDNESDAY, java.util.Calendar.THURSDAY, java.util.Calendar.FRIDAY };
	}

	private static void scheduleOnSiteEquipment(LocationData location, List<DailyScheduleData> scheduleList, int targetPerDay, int[] equipmentPerDay) {
		int onSiteCount = location.getOnSiteEquipmentsCount();

		if (onSiteCount == 0) {
			return;
		}

		// Create a copy of the location with only on-site equipment
		LocationData onSiteLocation = new LocationData(location.getLocationName());
		onSiteLocation.getOnSiteEquipmentsList().addAll(location.getOnSiteEquipmentsList());

		if (onSiteCount == 1) {
			// Find the day with the least equipment
			int minDay = 0;
			for (int i = 1; i < equipmentPerDay.length; i++) {
				if (equipmentPerDay[i] < equipmentPerDay[minDay]) {
					minDay = i;
				}
			}

			// Schedule single equipment on the day with least equipment
			scheduleList.get(minDay).getTechniciansLocationsMap().values().iterator().next().add(onSiteLocation);
			location.getOnSiteEquipmentsList().clear();
			equipmentPerDay[minDay]++;
		} else {
			// Calculate daily maximum based on 50% rule
			int maxLocationLimit = (int) Math.ceil(onSiteCount * 0.5);
			int remainingEquipment = onSiteCount;

			// Try to schedule equipment on days with less equipment first
			while (remainingEquipment > 0) {
				// Find the day with the least equipment
				int minDay = 0;
				for (int i = 1; i < equipmentPerDay.length; i++) {
					if (equipmentPerDay[i] < equipmentPerDay[minDay]) {
						minDay = i;
					}
				}

				// Calculate how many to schedule on this day
				int toSchedule = Math.min(Math.min(maxLocationLimit, remainingEquipment), targetPerDay);
				if (toSchedule > 0) {
					LocationData dayLocation = new LocationData(location.getLocationName());
					for (int i = 0; i < toSchedule; i++) {
						String equipment = onSiteLocation.getOnSiteEquipmentsList().get(i);
						dayLocation.addOnSiteEquipment(equipment);
					}

					// Remove scheduled equipment
					onSiteLocation.getOnSiteEquipmentsList().subList(0, toSchedule).clear();
					location.getOnSiteEquipmentsList().removeAll(dayLocation.getOnSiteEquipmentsList());

					scheduleList.get(minDay).getTechniciansLocationsMap().values().iterator().next().add(dayLocation);
					equipmentPerDay[minDay] += toSchedule;
					remainingEquipment -= toSchedule;
				} else {
					break;
				}
			}
		}
	}

	private static void scheduleOffSiteEquipment(LocationData location, List<DailyScheduleData> scheduleList, int targetPerDay, int[] equipmentPerDay) {
		if (location.getTotalOffSiteEquipmentsSublocationsCount() == 0) {
			return;
		}

		Map<String, List<String>> sublocationMap = location.getOffSiteEquipmentsSublocationsMap();

		// Sort sublocations by equipment count
		List<Map.Entry<String, List<String>>> sortedSublocations = new ArrayList<>(sublocationMap.entrySet());
		sortedSublocations.sort((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()));

		// Handle each sublocation separately
		for (Map.Entry<String, List<String>> sublocationEntry : sortedSublocations) {
			String sublocationName = sublocationEntry.getKey();
			List<String> equipmentList = sublocationEntry.getValue();

			if (equipmentList.size() == 1) {
				// Find the day with the least equipment
				int minDay = 0;
				for (int i = 1; i < equipmentPerDay.length; i++) {
					if (equipmentPerDay[i] < equipmentPerDay[minDay]) {
						minDay = i;
					}
				}

				// Schedule single equipment on the day with least equipment
				LocationData dayLocation = new LocationData(location.getLocationName());
				dayLocation.addOffSiteEquipmentToSublocation(sublocationName, equipmentList.get(0));
				scheduleList.get(minDay).getTechniciansLocationsMap().values().iterator().next().add(dayLocation);
				equipmentList.clear();
				equipmentPerDay[minDay]++;
			} else if (!equipmentList.isEmpty()) {
				// Calculate daily maximum based on 50% rule
				int maxSublocationLimit = (int) Math.ceil(equipmentList.size() * 0.5);
				List<String> remainingEquipment = new ArrayList<>(equipmentList);

				// Try to schedule equipment on days with less equipment first
				while (!remainingEquipment.isEmpty()) {
					// Find the day with the least equipment
					int minDay = 0;
					for (int i = 1; i < equipmentPerDay.length; i++) {
						if (equipmentPerDay[i] < equipmentPerDay[minDay]) {
							minDay = i;
						}
					}

					// Calculate how many to schedule on this day
					int toSchedule = Math.min(Math.min(maxSublocationLimit, remainingEquipment.size()), targetPerDay);
					if (toSchedule > 0) {
						LocationData dayLocation = new LocationData(location.getLocationName());
						for (int i = 0; i < toSchedule; i++) {
							dayLocation.addOffSiteEquipmentToSublocation(sublocationName, remainingEquipment.get(i));
						}

						// Remove scheduled equipment
						remainingEquipment.subList(0, toSchedule).clear();
						equipmentList.removeAll(dayLocation.getOffSiteEquipmentsSublocationsMap().get(sublocationName));

						scheduleList.get(minDay).getTechniciansLocationsMap().values().iterator().next().add(dayLocation);
						equipmentPerDay[minDay] += toSchedule;
					} else {
						break;
					}
				}
			}
		}
	}

	private static int scheduleOnSiteEquipmentForDay(LocationData location, DailyScheduleData dailySchedule, int maxEquipment) {
		int onSiteCount = location.getOnSiteEquipmentsCount();

		if (onSiteCount == 0) {
			return 0;
		}

		// Create a copy of the location with only on-site equipment
		LocationData onSiteLocation = new LocationData(location.getLocationName());
		List<String> equipmentToSchedule = new ArrayList<>(location.getOnSiteEquipmentsList());

		int maxLocationLimit;

		if (location.isSplitRequiredOnSiteEquipments()) {
			location.setSplitRequiredOnSiteEquipments(false);

			// Calculate 50% limit for this location
			maxLocationLimit = (int) Math.ceil(equipmentToSchedule.size() * 0.5);
		} else {
			// The 50% limit has already been applied for this location
			maxLocationLimit = equipmentToSchedule.size();
		}

		// Take minimum of global limit, location limit, and available equipment
		int toSchedule = Math.min(maxEquipment, Math.min(maxLocationLimit, equipmentToSchedule.size()));

		if (toSchedule > 0) {
			// Add equipment to the schedule
			for (int i = 0; i < toSchedule; i++) {
				onSiteLocation.addOnSiteEquipment(equipmentToSchedule.get(i));
			}

			// Remove scheduled equipment from the original location
			location.getOnSiteEquipmentsList().removeAll(onSiteLocation.getOnSiteEquipmentsList());

			// Add location to schedule
			dailySchedule.getTechniciansLocationsMap().values().iterator().next().add(onSiteLocation);
		}

		return toSchedule;
	}

	private static int scheduleOffSiteEquipmentForDay(LocationData location, DailyScheduleData dailySchedule, int maxEquipment) {
		if (location.getTotalOffSiteEquipmentsSublocationsCount() == 0) {
			return 0;
		}

		int totalScheduled = 0;

		Map<String, List<String>> sublocationMap = location.getOffSiteEquipmentsSublocationsMap();

		// Convert sublocation entries to a list and sort by equipment count (descending)
		List<Map.Entry<String, List<String>>> sortedSublocations = new ArrayList<>(sublocationMap.entrySet());
		sortedSublocations.sort((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()));

		// Handle each sublocation in order of most equipment
		for (Map.Entry<String, List<String>> sublocationEntry : sortedSublocations) {
			if (maxEquipment <= 0) {
				break;
			}

			String sublocationName = sublocationEntry.getKey();
			List<String> equipmentList = sublocationEntry.getValue();

			if (!equipmentList.isEmpty()) {
				LocationData dayLocation = new LocationData(location.getLocationName());

				boolean splitRequired = location.getSplitRequiredOffSiteEquipmentsSublocationsMap().get(sublocationName);

				int maxSublocationLimit;

				if (splitRequired) {
					location.getSplitRequiredOffSiteEquipmentsSublocationsMap().put(sublocationName, Boolean.FALSE);

					// Calculate 50% limit for this sublocation
					maxSublocationLimit = (int) Math.ceil(equipmentList.size() * 0.5);
				} else {
					// The 50% limit has already been applied for this location
					maxSublocationLimit = equipmentList.size();
				}

				// Take minimum of global limit, sublocation limit, and available equipment
				int toSchedule = Math.min(maxEquipment, Math.min(maxSublocationLimit, equipmentList.size()));

				// Schedule equipment for this sublocation
				for (int i = 0; i < toSchedule; i++) {
					String equipment = equipmentList.get(0);
					dayLocation.addOffSiteEquipmentToSublocation(sublocationName, equipment);
					equipmentList.remove(0);
				}

				if (dayLocation.getTotalEquipmentsCount() > 0) {
					dailySchedule.getTechniciansLocationsMap().values().iterator().next().add(dayLocation);
					totalScheduled += toSchedule;
					maxEquipment -= toSchedule;
				}
			}
		}

		return totalScheduled;
	}

	private static int scheduleOnSiteEquipmentByPriority(List<LocationData> locationsList, DailyScheduleData dailySchedule, int targetOnSite) {
		// Create a sorted list of locations by on-site equipment count (descending)
		List<LocationData> sortedLocations = new ArrayList<>(locationsList);
		sortedLocations.sort((a, b) -> Integer.compare(b.getOnSiteEquipmentsCount(), a.getOnSiteEquipmentsCount()));

		int onSiteScheduled = 0;

		for (LocationData location : sortedLocations) {
			if (location.getOnSiteEquipmentsCount() > 0) {
				int scheduledForDay = scheduleOnSiteEquipmentForDay(location, dailySchedule, targetOnSite - onSiteScheduled);
				onSiteScheduled += scheduledForDay;

				if (onSiteScheduled >= targetOnSite) {
					break;
				}
			}
		}

		return onSiteScheduled;
	}

	private static int scheduleOffSiteEquipmentByPriority(List<LocationData> locationsList, DailyScheduleData dailySchedule, int targetOffSite) {
		// Create a sorted list of locations by off-site equipment count (descending)
		List<LocationData> sortedLocations = new ArrayList<>(locationsList);
		sortedLocations.sort((a, b) -> Integer.compare(b.getTotalOffSiteEquipmentsSublocationsCount(), a.getTotalOffSiteEquipmentsSublocationsCount()));

		int offSiteScheduled = 0;

		for (LocationData location : sortedLocations) {
			if (location.getTotalOffSiteEquipmentsSublocationsCount() > 0) {
				int scheduledForDay = scheduleOffSiteEquipmentForDay(location, dailySchedule, targetOffSite - offSiteScheduled);
				offSiteScheduled += scheduledForDay;

				if (offSiteScheduled >= targetOffSite) {
					break;
				}
			}
		}

		return offSiteScheduled;
	}

	private static void distributeEquipmentsToTechnicians(DailyScheduleData dailySchedule, List<String> techniciansList) {
		if (techniciansList.size() <= 1) {
			return;
		}

		List<LocationData> onSiteLocations = new ArrayList<>();
		List<LocationData> offSiteLocations = new ArrayList<>();

		for (List<LocationData> locations : dailySchedule.getTechniciansLocationsMap().values()) {
			for (LocationData location : locations) {
				for (String onSiteEquipment : location.getOnSiteEquipmentsList()) {
					LocationData onSiteLocation = new LocationData(location.getLocationName());
					onSiteLocation.addOnSiteEquipment(onSiteEquipment);

					onSiteLocations.add(onSiteLocation);
				}

				for (Map.Entry<String, List<String>> sublocationEntry : location.getOffSiteEquipmentsSublocationsMap().entrySet()) {
					for (String offSiteEquipment : sublocationEntry.getValue()) {
						LocationData offSiteLocation = new LocationData(location.getLocationName());
						offSiteLocation.addOffSiteEquipmentToSublocation(sublocationEntry.getKey(), offSiteEquipment);

						offSiteLocations.add(offSiteLocation);
					}
				}
			}
		}

		int totalOnSite = onSiteLocations.size();
		int totalOffSite = offSiteLocations.size();
		int numTechnicians = techniciansList.size();

		int[][] distributionByTechnician = new int[2][numTechnicians];

		int baseOnSite = totalOnSite / numTechnicians;
		int extraOnSite = totalOnSite % numTechnicians;

		int baseOffSite = totalOffSite / numTechnicians;
		int extraOffSite = totalOffSite % numTechnicians;

		for (int i = 0; i < numTechnicians; i++) {
			boolean addOnSite = (extraOnSite != 0);
			boolean addOffSite = ((extraOnSite == 0) && (extraOffSite != 0));

			distributionByTechnician[0][i] = baseOnSite + (addOnSite ? 1 : 0);
			distributionByTechnician[1][i] = baseOffSite + (addOffSite ? 1 : 0);

			if (addOnSite) {
				extraOnSite--;
			}

			if (addOffSite) {
				extraOffSite--;
			}
		}

		int technicianIndex = 0;
		int onSiteIndex = 0;
		int offSiteIndex = 0;

		for (List<LocationData> locations : dailySchedule.getTechniciansLocationsMap().values()) {
			locations.clear();

			for (int i = 0; i < distributionByTechnician[0][technicianIndex]; i++) {
				locations.add(onSiteLocations.get(onSiteIndex++));
			}

			for (int i = 0; i < distributionByTechnician[1][technicianIndex]; i++) {
				locations.add(offSiteLocations.get(offSiteIndex++));
			}

			technicianIndex++;
		}
	}
}
