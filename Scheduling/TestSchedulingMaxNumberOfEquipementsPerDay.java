package main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import schedules.SchedulingHelper;
import schedules.data.DailyScheduleData;
import schedules.data.LocationData;

/**
 * Test the extract the list scheduled daily maintenances based on a maximum number of equipments per day
 */
public class TestSchedulingMaxNumberOfEquipementsPerDay {

	public static void main(String[] args) {
		LocationData location1 = new LocationData("Loc-1");
		location1.getOnSiteEquipmentsList().add("E20");
		location1.getOnSiteEquipmentsList().add("E30");
		location1.addOffSiteEquipmentToSublocation("Sub2", "E22");
		location1.addOffSiteEquipmentToSublocation("Sub2", "E24");
		location1.addOffSiteEquipmentToSublocation("Sub3", "E36");
		location1.addOffSiteEquipmentToSublocation("Sub6", "E14");
		location1.addOffSiteEquipmentToSublocation("Sub6", "E16");

		LocationData location5 = new LocationData("Loc-5");
		location5.getOnSiteEquipmentsList().add("E50");
		location5.getOnSiteEquipmentsList().add("E56");

		LocationData location6 = new LocationData("Loc-6");
		location6.getOnSiteEquipmentsList().add("E28");
		location6.getOnSiteEquipmentsList().add("E40");
		location6.addOffSiteEquipmentToSublocation("Sub4", "E44");

		LocationData location8 = new LocationData("Loc-8");
		location8.getOnSiteEquipmentsList().add("E42");
		location8.getOnSiteEquipmentsList().add("E44");
		location8.getOnSiteEquipmentsList().add("E48");
		location8.addOffSiteEquipmentToSublocation("Sub5", "E51");
		location8.addOffSiteEquipmentToSublocation("Sub5", "E52");
		location8.addOffSiteEquipmentToSublocation("Sub5", "E55");

		List<LocationData> locationsList = new ArrayList<>();
		locationsList.add(location1);
		locationsList.add(location5);
		locationsList.add(location6);
		locationsList.add(location8);

		List<String> techniciansList = new ArrayList<>();
		techniciansList.add("T1");
		techniciansList.add("T2");
		// techniciansList.add("T3");

		int[] openBusinessDays = new int[] { //
				Calendar.MONDAY, // Monday
				Calendar.TUESDAY, // Tuesday
				Calendar.WEDNESDAY, // Wednesday
				Calendar.THURSDAY, // Thursday
				Calendar.FRIDAY // Friday
		};
		
		Map<Date, Integer> maxNumberEquipmentsForGivenDaysMap = new HashMap<>();
		maxNumberEquipmentsForGivenDaysMap.put(new Date(), 6);

		List<DailyScheduleData> scheduleList = SchedulingHelper.extractScheduleMaxNumberOfEquipementsPerDay(5, maxNumberEquipmentsForGivenDaysMap, new Date(), locationsList, techniciansList, openBusinessDays);

		TestSchedulingFixNumberOfDays.displayScheduleList(scheduleList);
	}
}
