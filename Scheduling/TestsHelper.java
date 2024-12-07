package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import schedules.data.DailyScheduleData;
import schedules.data.LocationData;

/**
 * Helper class of public static methods for the tests
 */
public class TestsHelper {
	
	/** List of locations and its equipments to be scheduled **/
	public static final List<LocationData> LOCATIONS_LIST;
	
	static {
		LocationData location1 = new LocationData("Loc-1");
		location1.getOnSiteEquipmentsList().add("E20-1");
		location1.getOnSiteEquipmentsList().add("E30-2");
		location1.addOffSiteEquipmentToSublocation("Sub2", "E22-1");
		location1.addOffSiteEquipmentToSublocation("Sub2", "E24-2");
		location1.addOffSiteEquipmentToSublocation("Sub3", "E36-1");
		location1.addOffSiteEquipmentToSublocation("Sub6", "E14-1");
		location1.addOffSiteEquipmentToSublocation("Sub6", "E16-2");

		LocationData location5 = new LocationData("Loc-5");
		location5.getOnSiteEquipmentsList().add("E50-1");
		location5.getOnSiteEquipmentsList().add("E56-2");

		LocationData location6 = new LocationData("Loc-6");
		location6.getOnSiteEquipmentsList().add("E28-1");
		location6.getOnSiteEquipmentsList().add("E40-2");
		location6.addOffSiteEquipmentToSublocation("Sub4", "E44-1");

		LocationData location8 = new LocationData("Loc-8");
		location8.getOnSiteEquipmentsList().add("E42-1");
		location8.getOnSiteEquipmentsList().add("E44-2");
		location8.getOnSiteEquipmentsList().add("E48-3");
		location8.addOffSiteEquipmentToSublocation("Sub5", "E51-1");
		location8.addOffSiteEquipmentToSublocation("Sub5", "E52-2");
		location8.addOffSiteEquipmentToSublocation("Sub5", "E55-3");

		LOCATIONS_LIST = new ArrayList<>();
		LOCATIONS_LIST.add(location1);
		LOCATIONS_LIST.add(location5);
		LOCATIONS_LIST.add(location6);
		LOCATIONS_LIST.add(location8);
	}

	/**
	 * Displays the list of scheduled daily maintenances
	 * @param title Title to display
	 * @param scheduleList List of scheduled daily maintenances to display
	 */
	public static void displayScheduleList(String title, List<DailyScheduleData> scheduleList) {
		System.out.println("List of scheduled daily maintenances for " + title);
		
		int totalEquipments = 0;

		for (DailyScheduleData schedule : scheduleList) {
			System.out.println("Scheduled date: " + schedule.getScheduledDate());
			
			int totalEquipmentsSchedules = 0;

			for (Map.Entry<String, List<LocationData>> technicianLocationsEntry : schedule.getTechniciansLocationsMap().entrySet()) {
				System.out.println("Technician: " + technicianLocationsEntry.getKey());
				
				int totalEquipmentsPerTechnician = 0;

				for (LocationData location : technicianLocationsEntry.getValue()) {
					System.out.println("  Location: " + location.getLocationName());
					
					totalEquipmentsPerTechnician += location.getOnsiteEquipmentsCount() + location.getTotalOffSiteEquipmentsSublocationsCount();

					for (String equipment : location.getOnSiteEquipmentsList()) {
						System.out.println("    On-site: " + equipment);
					}

					for (Map.Entry<String, List<String>> sublocationEntry : location.getOffSiteEquipmentsSublocationsMap().entrySet()) {
						System.out.println("    Sublocation: " + sublocationEntry.getKey());
						
						for (String equipmentName : sublocationEntry.getValue()) {
							System.out.println("      Off-site: " + equipmentName);
						}
					}
				}
				
				System.out.println("Total number of equipment scheduled for technician " + technicianLocationsEntry.getKey() + ": " + totalEquipmentsPerTechnician);
				
				totalEquipmentsSchedules += totalEquipmentsPerTechnician;
			}
			
			System.out.println("Total number of equipment scheduled for daily maintenance: " + totalEquipmentsSchedules);
			
			totalEquipments += totalEquipmentsSchedules;

			System.out.println("-------------------------------------");
		}

		System.out.println("Total number of equipment scheduled for maintenance: " + totalEquipments);
		
	}

}
