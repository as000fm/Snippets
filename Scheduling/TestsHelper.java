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
	public static final List<LocationData> LOCATIONS_LIST = new ArrayList<>();;

	static {
		LocationData location;

		location = new LocationData("Loc-1");
		location.getOnSiteEquipmentsList().add("E01");
		location.getOnSiteEquipmentsList().add("E02");
		location.getOnSiteEquipmentsList().add("E03");
		location.getOnSiteEquipmentsList().add("E04");
		location.getOnSiteEquipmentsList().add("E05");
		location.getOnSiteEquipmentsList().add("E06");
		location.getOnSiteEquipmentsList().add("E07");
		location.getOnSiteEquipmentsList().add("E08");
		location.getOnSiteEquipmentsList().add("E09");
		location.addOffSiteEquipmentToSublocation("Sub1", "E01-1");
		location.addOffSiteEquipmentToSublocation("Sub2", "E01-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E02-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E03-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E04-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E05-2");
		location.addOffSiteEquipmentToSublocation("Sub3", "E01-3");
		LOCATIONS_LIST.add(location);

		location = new LocationData("Loc-2");
		location.getOnSiteEquipmentsList().add("E01");
		location.getOnSiteEquipmentsList().add("E02");
		location.getOnSiteEquipmentsList().add("E03");
		location.getOnSiteEquipmentsList().add("E04");
		location.getOnSiteEquipmentsList().add("E05");
		location.addOffSiteEquipmentToSublocation("Sub1", "E01-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E02-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E03-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E04-1");
		location.addOffSiteEquipmentToSublocation("Sub2", "E01-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E02-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E03-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E04-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E05-2");
		location.addOffSiteEquipmentToSublocation("Sub3", "E01-3");
		location.addOffSiteEquipmentToSublocation("Sub3", "E02-3");
		location.addOffSiteEquipmentToSublocation("Sub3", "E03-3");
		location.addOffSiteEquipmentToSublocation("Sub4", "E01-4");
		location.addOffSiteEquipmentToSublocation("Sub4", "E02-4");
		location.addOffSiteEquipmentToSublocation("Sub4", "E03-4");
		location.addOffSiteEquipmentToSublocation("Sub4", "E04-4");
		location.addOffSiteEquipmentToSublocation("Sub4", "E05-4");
		location.addOffSiteEquipmentToSublocation("Sub5", "E01-5");
		location.addOffSiteEquipmentToSublocation("Sub5", "E02-5");
		LOCATIONS_LIST.add(location);

		location = new LocationData("Loc-3");
		location.getOnSiteEquipmentsList().add("E01");
		location.getOnSiteEquipmentsList().add("E02");
		location.addOffSiteEquipmentToSublocation("Sub1", "E01-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E02-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E03-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E04-1");
		location.addOffSiteEquipmentToSublocation("Sub2", "E01-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E02-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E03-2");
		location.addOffSiteEquipmentToSublocation("Sub3", "E01-3");
		location.addOffSiteEquipmentToSublocation("Sub3", "E02-3");
		location.addOffSiteEquipmentToSublocation("Sub3", "E03-3");
		location.addOffSiteEquipmentToSublocation("Sub3", "E04-3");
		location.addOffSiteEquipmentToSublocation("Sub4", "E01-4");
		location.addOffSiteEquipmentToSublocation("Sub4", "E02-4");
		location.addOffSiteEquipmentToSublocation("Sub4", "E03-4");
		location.addOffSiteEquipmentToSublocation("Sub4", "E04-4");
		LOCATIONS_LIST.add(location);

		location = new LocationData("Loc-4");
		location.getOnSiteEquipmentsList().add("E01");
		location.getOnSiteEquipmentsList().add("E02");
		location.getOnSiteEquipmentsList().add("E03");
		location.getOnSiteEquipmentsList().add("E04");
		location.getOnSiteEquipmentsList().add("E05");
		location.addOffSiteEquipmentToSublocation("Sub1", "E01-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E02-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E03-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E04-1");
		location.addOffSiteEquipmentToSublocation("Sub2", "E01-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E02-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E03-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E04-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E05-2");
		location.addOffSiteEquipmentToSublocation("Sub3", "E01-3");
		location.addOffSiteEquipmentToSublocation("Sub3", "E02-3");
		location.addOffSiteEquipmentToSublocation("Sub3", "E03-3");
		LOCATIONS_LIST.add(location);

		location = new LocationData("Loc-5");
		location.getOnSiteEquipmentsList().add("E01");
		location.getOnSiteEquipmentsList().add("E02");
		location.getOnSiteEquipmentsList().add("E03");
		location.getOnSiteEquipmentsList().add("E04");
		location.getOnSiteEquipmentsList().add("E05");
		location.getOnSiteEquipmentsList().add("E06");
		location.getOnSiteEquipmentsList().add("E07");
		location.getOnSiteEquipmentsList().add("E08");
		location.getOnSiteEquipmentsList().add("E09");
		location.getOnSiteEquipmentsList().add("E10");
		location.addOffSiteEquipmentToSublocation("Sub1", "E01-1");
		location.addOffSiteEquipmentToSublocation("Sub2", "E01-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E02-2");
		location.addOffSiteEquipmentToSublocation("Sub3", "E01-3");
		location.addOffSiteEquipmentToSublocation("Sub3", "E02-3");
		location.addOffSiteEquipmentToSublocation("Sub3", "E03-3");
		location.addOffSiteEquipmentToSublocation("Sub3", "E04-3");
		location.addOffSiteEquipmentToSublocation("Sub4", "E01-4");
		location.addOffSiteEquipmentToSublocation("Sub4", "E02-4");
		location.addOffSiteEquipmentToSublocation("Sub4", "E03-4");
		LOCATIONS_LIST.add(location);

		location = new LocationData("Loc-6");
		location.getOnSiteEquipmentsList().add("E01");
		location.getOnSiteEquipmentsList().add("E02");
		location.getOnSiteEquipmentsList().add("E03");
		location.getOnSiteEquipmentsList().add("E04");
		location.getOnSiteEquipmentsList().add("E05");
		location.getOnSiteEquipmentsList().add("E06");
		location.addOffSiteEquipmentToSublocation("Sub1", "E01-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E02-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E03-1");
		location.addOffSiteEquipmentToSublocation("Sub2", "E01-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E02-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E03-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E04-2");
		location.addOffSiteEquipmentToSublocation("Sub3", "E01-3");
		location.addOffSiteEquipmentToSublocation("Sub3", "E02-3");
		location.addOffSiteEquipmentToSublocation("Sub4", "E01-4");
		location.addOffSiteEquipmentToSublocation("Sub4", "E02-4");
		location.addOffSiteEquipmentToSublocation("Sub4", "E03-4");
		location.addOffSiteEquipmentToSublocation("Sub4", "E04-4");
		location.addOffSiteEquipmentToSublocation("Sub5", "E01-5");
		LOCATIONS_LIST.add(location);

		location = new LocationData("Loc-7");
		location.getOnSiteEquipmentsList().add("E01");
		location.getOnSiteEquipmentsList().add("E02");
		location.getOnSiteEquipmentsList().add("E03");
		location.getOnSiteEquipmentsList().add("E04");
		location.getOnSiteEquipmentsList().add("E05");
		location.getOnSiteEquipmentsList().add("E06");
		location.getOnSiteEquipmentsList().add("E07");
		location.getOnSiteEquipmentsList().add("E08");
		LOCATIONS_LIST.add(location);

		location = new LocationData("Loc-8");
		location.getOnSiteEquipmentsList().add("E01");
		location.getOnSiteEquipmentsList().add("E02");
		location.getOnSiteEquipmentsList().add("E03");
		location.getOnSiteEquipmentsList().add("E04");
		location.getOnSiteEquipmentsList().add("E05");
		location.getOnSiteEquipmentsList().add("E06");
		location.getOnSiteEquipmentsList().add("E07");
		location.getOnSiteEquipmentsList().add("E08");
		location.addOffSiteEquipmentToSublocation("Sub1", "E01-1");
		location.addOffSiteEquipmentToSublocation("Sub2", "E01-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E02-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E03-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E04-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E05-2");
		LOCATIONS_LIST.add(location);

		location = new LocationData("Loc-9");
		location.getOnSiteEquipmentsList().add("E01");
		location.getOnSiteEquipmentsList().add("E02");
		location.getOnSiteEquipmentsList().add("E03");
		location.addOffSiteEquipmentToSublocation("Sub1", "E01-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E02-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E03-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E04-1");
		location.addOffSiteEquipmentToSublocation("Sub2", "E01-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E02-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E03-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E04-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E05-2");
		location.addOffSiteEquipmentToSublocation("Sub3", "E01-3");
		location.addOffSiteEquipmentToSublocation("Sub4", "E01-4");
		location.addOffSiteEquipmentToSublocation("Sub4", "E02-4");
		location.addOffSiteEquipmentToSublocation("Sub4", "E03-4");
		location.addOffSiteEquipmentToSublocation("Sub5", "E01-5");
		LOCATIONS_LIST.add(location);

		location = new LocationData("Loc-10");
		location.getOnSiteEquipmentsList().add("E01");
		location.addOffSiteEquipmentToSublocation("Sub1", "E01-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E02-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E03-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E04-1");
		LOCATIONS_LIST.add(location);

		location = new LocationData("Loc-11");
		location.getOnSiteEquipmentsList().add("E01");
		location.getOnSiteEquipmentsList().add("E02");
		location.getOnSiteEquipmentsList().add("E03");
		location.getOnSiteEquipmentsList().add("E04");
		location.getOnSiteEquipmentsList().add("E05");
		location.getOnSiteEquipmentsList().add("E06");
		location.addOffSiteEquipmentToSublocation("Sub1", "E01-1");
		location.addOffSiteEquipmentToSublocation("Sub2", "E01-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E02-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E03-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E04-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E05-2");
		location.addOffSiteEquipmentToSublocation("Sub3", "E01-3");
		location.addOffSiteEquipmentToSublocation("Sub3", "E02-3");
		location.addOffSiteEquipmentToSublocation("Sub3", "E03-3");
		location.addOffSiteEquipmentToSublocation("Sub3", "E04-3");
		location.addOffSiteEquipmentToSublocation("Sub3", "E05-3");
		LOCATIONS_LIST.add(location);

		location = new LocationData("Loc-12");
		location.getOnSiteEquipmentsList().add("E01");
		location.getOnSiteEquipmentsList().add("E02");
		location.getOnSiteEquipmentsList().add("E03");
		location.addOffSiteEquipmentToSublocation("Sub1", "E01-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E02-1");
		location.addOffSiteEquipmentToSublocation("Sub2", "E01-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E02-2");
		location.addOffSiteEquipmentToSublocation("Sub3", "E01-3");
		location.addOffSiteEquipmentToSublocation("Sub3", "E02-3");
		location.addOffSiteEquipmentToSublocation("Sub3", "E03-3");
		location.addOffSiteEquipmentToSublocation("Sub3", "E04-3");
		LOCATIONS_LIST.add(location);

		location = new LocationData("Loc-13");
		location.getOnSiteEquipmentsList().add("E01");
		location.getOnSiteEquipmentsList().add("E02");
		location.getOnSiteEquipmentsList().add("E03");
		location.getOnSiteEquipmentsList().add("E04");
		location.getOnSiteEquipmentsList().add("E05");
		location.getOnSiteEquipmentsList().add("E06");
		location.getOnSiteEquipmentsList().add("E07");
		location.getOnSiteEquipmentsList().add("E08");
		location.getOnSiteEquipmentsList().add("E09");
		location.addOffSiteEquipmentToSublocation("Sub1", "E01-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E02-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E03-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E04-1");
		location.addOffSiteEquipmentToSublocation("Sub2", "E01-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E02-2");
		location.addOffSiteEquipmentToSublocation("Sub3", "E01-3");
		location.addOffSiteEquipmentToSublocation("Sub4", "E01-4");
		location.addOffSiteEquipmentToSublocation("Sub4", "E02-4");
		location.addOffSiteEquipmentToSublocation("Sub4", "E03-4");
		location.addOffSiteEquipmentToSublocation("Sub4", "E04-4");
		location.addOffSiteEquipmentToSublocation("Sub5", "E01-5");
		location.addOffSiteEquipmentToSublocation("Sub5", "E02-5");
		location.addOffSiteEquipmentToSublocation("Sub5", "E03-5");
		location.addOffSiteEquipmentToSublocation("Sub5", "E04-5");
		LOCATIONS_LIST.add(location);

		location = new LocationData("Loc-14");
		location.getOnSiteEquipmentsList().add("E01");
		location.getOnSiteEquipmentsList().add("E02");
		location.getOnSiteEquipmentsList().add("E03");
		location.getOnSiteEquipmentsList().add("E04");
		location.getOnSiteEquipmentsList().add("E05");
		location.getOnSiteEquipmentsList().add("E06");
		location.getOnSiteEquipmentsList().add("E07");
		location.addOffSiteEquipmentToSublocation("Sub1", "E01-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E02-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E03-1");
		location.addOffSiteEquipmentToSublocation("Sub2", "E01-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E02-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E03-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E04-2");
		location.addOffSiteEquipmentToSublocation("Sub3", "E01-3");
		location.addOffSiteEquipmentToSublocation("Sub4", "E01-4");
		location.addOffSiteEquipmentToSublocation("Sub4", "E02-4");
		location.addOffSiteEquipmentToSublocation("Sub4", "E03-4");
		location.addOffSiteEquipmentToSublocation("Sub4", "E04-4");
		location.addOffSiteEquipmentToSublocation("Sub5", "E01-5");
		LOCATIONS_LIST.add(location);

		location = new LocationData("Loc-15");
		location.getOnSiteEquipmentsList().add("E01");
		location.getOnSiteEquipmentsList().add("E02");
		location.addOffSiteEquipmentToSublocation("Sub1", "E01-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E02-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E03-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E04-1");
		location.addOffSiteEquipmentToSublocation("Sub2", "E01-2");
		location.addOffSiteEquipmentToSublocation("Sub3", "E01-3");
		location.addOffSiteEquipmentToSublocation("Sub3", "E02-3");
		location.addOffSiteEquipmentToSublocation("Sub4", "E01-4");
		location.addOffSiteEquipmentToSublocation("Sub4", "E02-4");
		location.addOffSiteEquipmentToSublocation("Sub5", "E01-5");
		location.addOffSiteEquipmentToSublocation("Sub5", "E02-5");
		location.addOffSiteEquipmentToSublocation("Sub5", "E03-5");
		location.addOffSiteEquipmentToSublocation("Sub5", "E04-5");
		LOCATIONS_LIST.add(location);

		location = new LocationData("Loc-16");
		location.getOnSiteEquipmentsList().add("E01");
		location.getOnSiteEquipmentsList().add("E02");
		location.getOnSiteEquipmentsList().add("E03");
		location.getOnSiteEquipmentsList().add("E04");
		location.getOnSiteEquipmentsList().add("E05");
		location.getOnSiteEquipmentsList().add("E06");
		location.getOnSiteEquipmentsList().add("E07");
		location.getOnSiteEquipmentsList().add("E08");
		location.getOnSiteEquipmentsList().add("E09");
		LOCATIONS_LIST.add(location);

		location = new LocationData("Loc-17");
		location.addOffSiteEquipmentToSublocation("Sub1", "E01-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E02-1");
		LOCATIONS_LIST.add(location);

		location = new LocationData("Loc-18");
		location.getOnSiteEquipmentsList().add("E01");
		location.getOnSiteEquipmentsList().add("E02");
		location.getOnSiteEquipmentsList().add("E03");
		location.getOnSiteEquipmentsList().add("E04");
		location.getOnSiteEquipmentsList().add("E05");
		location.getOnSiteEquipmentsList().add("E06");
		location.getOnSiteEquipmentsList().add("E07");
		location.getOnSiteEquipmentsList().add("E08");
		location.addOffSiteEquipmentToSublocation("Sub1", "E01-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E02-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E03-1");
		LOCATIONS_LIST.add(location);

		location = new LocationData("Loc-19");
		location.getOnSiteEquipmentsList().add("E01");
		location.getOnSiteEquipmentsList().add("E02");
		location.addOffSiteEquipmentToSublocation("Sub1", "E01-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E02-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E03-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E04-1");
		LOCATIONS_LIST.add(location);

		location = new LocationData("Loc-20");
		location.getOnSiteEquipmentsList().add("E01");
		location.getOnSiteEquipmentsList().add("E02");
		location.getOnSiteEquipmentsList().add("E03");
		location.getOnSiteEquipmentsList().add("E04");
		location.getOnSiteEquipmentsList().add("E05");
		location.getOnSiteEquipmentsList().add("E06");
		location.getOnSiteEquipmentsList().add("E07");
		location.getOnSiteEquipmentsList().add("E08");
		location.getOnSiteEquipmentsList().add("E09");
		location.addOffSiteEquipmentToSublocation("Sub1", "E01-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E02-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E03-1");
		location.addOffSiteEquipmentToSublocation("Sub2", "E01-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E02-2");
		location.addOffSiteEquipmentToSublocation("Sub2", "E03-2");
		location.addOffSiteEquipmentToSublocation("Sub3", "E01-3");
		location.addOffSiteEquipmentToSublocation("Sub3", "E02-3");
		LOCATIONS_LIST.add(location);

		location = new LocationData("Loc-21");
		location.getOnSiteEquipmentsList().add("E01");
		location.getOnSiteEquipmentsList().add("E02");
		location.getOnSiteEquipmentsList().add("E03");
		location.getOnSiteEquipmentsList().add("E04");
		location.getOnSiteEquipmentsList().add("E05");
		location.getOnSiteEquipmentsList().add("E06");
		location.getOnSiteEquipmentsList().add("E07");
		location.addOffSiteEquipmentToSublocation("Sub1", "E01-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E02-1");
		LOCATIONS_LIST.add(location);

		location = new LocationData("Loc-22");
		location.addOffSiteEquipmentToSublocation("Sub1", "E01-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E02-1");
		location.addOffSiteEquipmentToSublocation("Sub2", "E01-2");
		location.addOffSiteEquipmentToSublocation("Sub3", "E01-3");
		location.addOffSiteEquipmentToSublocation("Sub3", "E02-3");
		location.addOffSiteEquipmentToSublocation("Sub3", "E03-3");
		location.addOffSiteEquipmentToSublocation("Sub3", "E04-3");
		location.addOffSiteEquipmentToSublocation("Sub3", "E05-3");
		LOCATIONS_LIST.add(location);

		location = new LocationData("Loc-23");
		location.getOnSiteEquipmentsList().add("E01");
		location.getOnSiteEquipmentsList().add("E02");
		location.getOnSiteEquipmentsList().add("E03");
		LOCATIONS_LIST.add(location);

		location = new LocationData("Loc-24");
		location.getOnSiteEquipmentsList().add("E01");
		location.getOnSiteEquipmentsList().add("E02");
		location.getOnSiteEquipmentsList().add("E03");
		location.getOnSiteEquipmentsList().add("E04");
		location.getOnSiteEquipmentsList().add("E05");
		location.getOnSiteEquipmentsList().add("E06");
		location.addOffSiteEquipmentToSublocation("Sub1", "E01-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E02-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E03-1");
		location.addOffSiteEquipmentToSublocation("Sub1", "E04-1");
		LOCATIONS_LIST.add(location);

		location = new LocationData("Loc-25");
		location.getOnSiteEquipmentsList().add("E01");
		location.getOnSiteEquipmentsList().add("E02");
		location.getOnSiteEquipmentsList().add("E03");
		location.getOnSiteEquipmentsList().add("E04");
		location.getOnSiteEquipmentsList().add("E05");
		location.getOnSiteEquipmentsList().add("E06");
		location.getOnSiteEquipmentsList().add("E07");
		location.getOnSiteEquipmentsList().add("E08");
		LOCATIONS_LIST.add(location);
	}

	/**
	 * Displays the list of scheduled daily maintenances
	 * @param title Title to display
	 * @param scheduleList List of scheduled daily maintenances to display
	 * @param verbose Verbose mode indicator
	 */
	public static void displayScheduleList(String title, List<DailyScheduleData> scheduleList, boolean verbose) {
		System.out.println("List of scheduled daily maintenances for " + title);
		
		int dayNo = 1;

		int totalEquipments = 0;

		for (DailyScheduleData schedule : scheduleList) {
			System.out.println("Scheduled #" + dayNo + " date: " + schedule.getScheduledDate());

			int totalEquipmentsSchedules = 0;

			for (Map.Entry<String, List<LocationData>> technicianLocationsEntry : schedule.getTechniciansLocationsMap().entrySet()) {
				System.out.println("Technician: " + technicianLocationsEntry.getKey());

				int totalEquipmentsPerTechnician = 0;
				int totalEquipmentsPerTechnicianOnSite = 0;
				int totalEquipmentsPerTechnicianOffSite = 0;

				for (LocationData location : technicianLocationsEntry.getValue()) {
					if (verbose) {
						System.out.println("  Location: " + location.getLocationName());

						for (String equipment : location.getOnSiteEquipmentsList()) {
							System.out.println("    On-site: " + equipment);
						}

						System.out.println("  Total number of on-site equipment scheduled for location: " + location.getOnSiteEquipmentsCount());
					}

					for (Map.Entry<String, List<String>> sublocationEntry : location.getOffSiteEquipmentsSublocationsMap().entrySet()) {
						if (verbose) {
							System.out.println("    Sublocation: " + sublocationEntry.getKey());

							for (String equipmentName : sublocationEntry.getValue()) {
								System.out.println("      Off-site: " + equipmentName);
							}
						}
					}

					if (verbose) {
						System.out.println("  Total number of off-site equipment scheduled for sublocations " + location.getTotalOffSiteEquipmentsSublocationsCount());
					}

					totalEquipmentsPerTechnicianOnSite += location.getOnSiteEquipmentsCount();
					totalEquipmentsPerTechnicianOffSite += location.getTotalOffSiteEquipmentsSublocationsCount();

					totalEquipmentsPerTechnician += location.getOnSiteEquipmentsCount() + location.getTotalOffSiteEquipmentsSublocationsCount();
				}

				System.out.println("Total number of equipment scheduled for technician " + technicianLocationsEntry.getKey() + ": " + totalEquipmentsPerTechnician);
				System.out.println("Total number of on-site equipment scheduled for technician " + technicianLocationsEntry.getKey() + ": " + totalEquipmentsPerTechnicianOnSite);
				System.out.println("Total number of off-site equipment scheduled for technician " + technicianLocationsEntry.getKey() + ": " + totalEquipmentsPerTechnicianOffSite);

				totalEquipmentsSchedules += totalEquipmentsPerTechnician;
			}
			
			dayNo++;

			System.out.println("Total number of equipment scheduled for daily maintenance: " + totalEquipmentsSchedules);

			totalEquipments += totalEquipmentsSchedules;

			System.out.println("-------------------------------------");
		}

		System.out.println("Total number of equipment scheduled for maintenance: " + totalEquipments);

	}

}
