package main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import schedules.SchedulingHelper;
import schedules.data.DailyScheduleData;

/**
 * Test the extract the list scheduled daily maintenances based on a maximum number of equipments per day
 */
public class TestSchedulingMaxNumberOfEquipementsPerDay {

	public static void main(String[] args) {
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
		maxNumberEquipmentsForGivenDaysMap.put(new Date(), 10);

		List<DailyScheduleData> scheduleList = SchedulingHelper.extractScheduleMaxNumberOfEquipmentsPerDay(10, maxNumberEquipmentsForGivenDaysMap, new Date(), TestsHelper.LOCATIONS_LIST, techniciansList, openBusinessDays);

		TestsHelper.displayScheduleList("max number of equipments per day", scheduleList);
	}
}
