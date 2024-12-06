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
	 * @param locationsList List of locations and its equipments to be schedule
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

		// Schedule locations
		for (LocationData location : locationsList) {
			// Handle on-site equipment
			scheduleOnSiteEquipment(location, scheduleList, numberOfDays);

			// Handle off-site equipment by sublocation
			scheduleOffSiteEquipment(location, scheduleList, numberOfDays);
		}

		// Distribute locations evenly among technicians for each day
		for (DailyScheduleData dailySchedule : scheduleList) {
			distributeLocationsToTechnicians(dailySchedule, techniciansList);
		}

		return scheduleList;
	}

	/**
	 * Extract the list scheduled daily maintenances based on a maximum number of equipments per day
	 * @param maxNumberEquipmentsPerDay Maximum number of equipments per day
	 * @param maxNumberEquipmentsForGivenDaysMap Dictionnary of maximum number of equipments for each given day
	 * @param startDate Start date for scheduling
	 * @param locationsList List of locations and its equipments to be schedule
	 * @param techniciansList List of technicians to assign
	 * @param openBusinessDays List of open business days based on the constant field values for java.util.Calendar.DAY_OF_WEEK
	 * @return the list scheduled daily maintenances of locations and its equipments by technicians
	 */
	public static List<DailyScheduleData> extractScheduleMaxNumberOfEquipementsPerDay(int maxNumberEquipmentsPerDay, Map<Date, Integer> maxNumberEquipmentsForGivenDaysMap, Date startDate, List<LocationData> locationsList, List<String> techniciansList, int[] openBusinessDays) {
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
			totalOnSiteEquipment += location.getOnsiteEquipmentsCount();
			totalOffSiteEquipment += location.getTotalOffSiteEquipmentsSublocationsCount();
		}

		// Create schedules until all equipment is allocated
		int remainingOnSite = totalOnSiteEquipment;
		int remainingOffSite = totalOffSiteEquipment;
		
		while (remainingOnSite > 0 || remainingOffSite > 0) {
			Date currentDate = calendar.getTime();

			// Get max equipment limit for current day
			int currentDayMaxEquipment = maxNumberEquipmentsForGivenDaysMap.containsKey(currentDate) 
				? maxNumberEquipmentsForGivenDaysMap.get(currentDate)
				: maxNumberEquipmentsPerDay;

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
			
			if (remainingOnSite > 0 && remainingOffSite > 0) {
				// Both types remaining - try to schedule evenly
				targetOnSite = (int) Math.ceil(maxToSchedule / 2.0);
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
			scheduleOnSiteEquipmentByPriority(locationsList, dailySchedule, targetOnSite);

			// Schedule off-site equipment
			scheduleOffSiteEquipmentByPriority(locationsList, dailySchedule, targetOffSite);

			remainingOnSite -= targetOnSite;
			remainingOffSite -= targetOffSite;

			// Move to next business day
			do {
				calendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
			} while (!isBusinessDay(calendar.get(java.util.Calendar.DAY_OF_WEEK), openBusinessDays));

			// Distribute locations evenly among technicians for the day
			distributeLocationsToTechnicians(dailySchedule, techniciansList);
		}

		return scheduleList;
	}

	private static int[] getDefaultOpenBusinessDays() {
		return new int[] { java.util.Calendar.MONDAY, java.util.Calendar.TUESDAY, java.util.Calendar.WEDNESDAY, java.util.Calendar.THURSDAY, java.util.Calendar.FRIDAY };
	}

	private static boolean isBusinessDay(int dayOfWeek, int[] openBusinessDays) {
		for (int openDay : openBusinessDays) {
			if (dayOfWeek == openDay) {
				return true;
			}
		}
		return false;
	}

	private static void scheduleOnSiteEquipment(LocationData location, List<DailyScheduleData> scheduleList, int numberOfDays) {
		int onSiteCount = location.getOnsiteEquipmentsCount();
		if (onSiteCount == 0)
			return;

		// Create a copy of the location with only on-site equipment
		LocationData onSiteLocation = new LocationData(location.getLocationName());
		onSiteLocation.getOnSiteEquipmentsList().addAll(location.getOnSiteEquipmentsList());

		if (onSiteCount == 1) {
			// Schedule single equipment on first day
			scheduleList.get(0).getTechniciansLocationsMap().values().iterator().next().add(onSiteLocation);
		} else {
			// Calculate daily maximum based on percentage
			double dailyPercentage = 100.0 / numberOfDays;
			int maxPerDay = (int) Math.ceil((dailyPercentage / 100.0) * onSiteCount);

			// Distribute equipment across days
			int remainingEquipment = onSiteCount;
			int currentDay = 0;

			while (remainingEquipment > 0) {
				int toSchedule = Math.min(maxPerDay, remainingEquipment);

				LocationData dayLocation = new LocationData(location.getLocationName());
				for (int i = 0; i < toSchedule; i++) {
					dayLocation.getOnSiteEquipmentsList().add(onSiteLocation.getOnSiteEquipmentsList().get(onSiteCount - remainingEquipment + i));
				}

				scheduleList.get(currentDay).getTechniciansLocationsMap().values().iterator().next().add(dayLocation);

				remainingEquipment -= toSchedule;
				currentDay++;
			}
		}
	}

	private static void scheduleOffSiteEquipment(LocationData location, List<DailyScheduleData> scheduleList, int numberOfDays) {
		if (location.getTotalOffSiteEquipmentsSublocationsCount() == 0)
			return;

		Map<String, List<String>> sublocationMap = location.getOffSiteEquipmentsSublocationsMap();

		// Handle each sublocation separately
		for (Map.Entry<String, List<String>> sublocationEntry : sublocationMap.entrySet()) {
			String sublocationName = sublocationEntry.getKey();
			List<String> equipmentList = sublocationEntry.getValue();

			if (equipmentList.size() == 1) {
				// Schedule single equipment on first day
				LocationData dayLocation = new LocationData(location.getLocationName());
				dayLocation.addOffSiteEquipmentToSublocation(sublocationName, equipmentList.get(0));
				scheduleList.get(0).getTechniciansLocationsMap().values().iterator().next().add(dayLocation);
			} else {
				// Calculate daily maximum based on percentage
				double dailyPercentage = 100.0 / numberOfDays;
				int maxPerDay = (int) Math.ceil((dailyPercentage / 100.0) * equipmentList.size());

				// Distribute equipment across days
				int remainingEquipment = equipmentList.size();
				int currentDay = 0;

				while (remainingEquipment > 0) {
					int toSchedule = Math.min(maxPerDay, remainingEquipment);

					LocationData dayLocation = new LocationData(location.getLocationName());
					for (int i = 0; i < toSchedule; i++) {
						dayLocation.addOffSiteEquipmentToSublocation(sublocationName, equipmentList.get(equipmentList.size() - remainingEquipment + i));
					}

					scheduleList.get(currentDay).getTechniciansLocationsMap().values().iterator().next().add(dayLocation);

					remainingEquipment -= toSchedule;
					currentDay++;
				}
			}
		}
	}

	private static int scheduleOnSiteEquipmentForDay(LocationData location, DailyScheduleData dailySchedule, int maxEquipment) {
		int onSiteCount = location.getOnsiteEquipmentsCount();
		if (onSiteCount == 0) return 0;

		// Create a copy of the location with only on-site equipment
		LocationData onSiteLocation = new LocationData(location.getLocationName());
		List<String> equipmentToSchedule = new ArrayList<>(location.getOnSiteEquipmentsList());
		
		// Calculate 50% limit for this location
		int maxLocationLimit = (int) Math.ceil(equipmentToSchedule.size() * 0.5);
		
		// Take minimum of global limit, location limit, and available equipment
		int toSchedule = Math.min(maxEquipment, Math.min(maxLocationLimit, equipmentToSchedule.size()));
		
		if (toSchedule > 0) {
			// Add equipment to the schedule
			for (int i = 0; i < toSchedule; i++) {
				onSiteLocation.getOnSiteEquipmentsList().add(equipmentToSchedule.get(i));
			}
			
			// Remove scheduled equipment from the original location
			location.getOnSiteEquipmentsList().removeAll(onSiteLocation.getOnSiteEquipmentsList());
			
			// Add location to schedule
			dailySchedule.getTechniciansLocationsMap().values().iterator().next().add(onSiteLocation);
		}

		return toSchedule;
	}

	private static int scheduleOffSiteEquipmentForDay(LocationData location, DailyScheduleData dailySchedule, int maxEquipment) {
		if (location.getTotalOffSiteEquipmentsSublocationsCount() == 0) return 0;

		int totalScheduled = 0;
		Map<String, List<String>> sublocationMap = location.getOffSiteEquipmentsSublocationsMap();

		// Convert sublocation entries to a list and sort by equipment count (descending)
		List<Map.Entry<String, List<String>>> sortedSublocations = new ArrayList<>(sublocationMap.entrySet());
		sortedSublocations.sort((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()));

		// Handle each sublocation in order of most equipment
		for (Map.Entry<String, List<String>> sublocationEntry : sortedSublocations) {
			if (maxEquipment <= 0) break;

			String sublocationName = sublocationEntry.getKey();
			List<String> equipmentList = sublocationEntry.getValue();
			
			if (!equipmentList.isEmpty()) {
				LocationData dayLocation = new LocationData(location.getLocationName());
				
				// Calculate 50% limit for this sublocation
				int maxSublocationLimit = (int) Math.ceil(equipmentList.size() * 0.5);
				
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

	private static void scheduleOnSiteEquipmentByPriority(List<LocationData> locationsList, DailyScheduleData dailySchedule, int targetOnSite) {
		if (targetOnSite <= 0) return;

		// Create a sorted list of locations by on-site equipment count (descending)
		List<LocationData> sortedLocations = new ArrayList<>(locationsList);
		sortedLocations.sort((a, b) -> Integer.compare(b.getOnsiteEquipmentsCount(), a.getOnsiteEquipmentsCount()));

		int onSiteScheduled = 0;
		for (LocationData location : sortedLocations) {
			if (location.getOnsiteEquipmentsCount() > 0) {
				int scheduledForDay = scheduleOnSiteEquipmentForDay(location, dailySchedule, targetOnSite - onSiteScheduled);
				onSiteScheduled += scheduledForDay;

				if (onSiteScheduled >= targetOnSite) break;
			}
		}
	}

	private static void scheduleOffSiteEquipmentByPriority(List<LocationData> locationsList, DailyScheduleData dailySchedule, int targetOffSite) {
		if (targetOffSite <= 0) return;

		// Create a sorted list of locations by off-site equipment count (descending)
		List<LocationData> sortedLocations = new ArrayList<>(locationsList);
		sortedLocations.sort((a, b) -> Integer.compare(
			b.getTotalOffSiteEquipmentsSublocationsCount(),
			a.getTotalOffSiteEquipmentsSublocationsCount()
		));

		int offSiteScheduled = 0;
		for (LocationData location : sortedLocations) {
			if (location.getTotalOffSiteEquipmentsSublocationsCount() > 0) {
				int scheduledForDay = scheduleOffSiteEquipmentForDay(location, dailySchedule, targetOffSite - offSiteScheduled);
				offSiteScheduled += scheduledForDay;

				if (offSiteScheduled >= targetOffSite) break;
			}
		}
	}

	private static void distributeLocationsToTechnicians(DailyScheduleData dailySchedule, List<String> techniciansList) {
		if (techniciansList.size() <= 1)
			return;
	
		// Get all locations for the day
		List<LocationData> allLocations = new ArrayList<>();
		for (List<LocationData> locations : dailySchedule.getTechniciansLocationsMap().values()) {
			allLocations.addAll(locations);
			locations.clear();
		}
	
		// Distribute locations evenly among technicians
		int locationsPerTechnician = (int) Math.ceil((double) allLocations.size() / techniciansList.size());
		int currentLocation = 0;
	
		for (String technician : techniciansList) {
			List<LocationData> technicianLocations = dailySchedule.getTechniciansLocationsMap().get(technician);
	
			for (int i = 0; i < locationsPerTechnician && currentLocation < allLocations.size(); i++) {
				technicianLocations.add(allLocations.get(currentLocation++));
			}
		}
	}
}
