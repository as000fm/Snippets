package main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import schedules.SchedulingHelper;
import schedules.data.DailyScheduleData;

/**
 * Test the extract the list scheduled daily maintenances based on a fix number of days
 */
public class TestSchedulingFixNumberOfDays {

	public static void main(String[] args) {
		List<String> techniciansList = new ArrayList<>();
		techniciansList.add("T1");
		// techniciansList.add("T2");
		// techniciansList.add("T3");

		int[] openBusinessDays = new int[] { //
				Calendar.MONDAY, // Monday
				Calendar.TUESDAY, // Tuesday
				Calendar.WEDNESDAY, // Wednesday
				Calendar.THURSDAY, // Thursday
				Calendar.FRIDAY // Friday
		};

		List<DailyScheduleData> scheduleList = SchedulingHelper.extractScheduleFixNumberOfDays(2, new Date(), TestsHelper.LOCATIONS_LIST, techniciansList, openBusinessDays);

		TestsHelper.displayScheduleList("fix number of days", scheduleList, true);
	}

}
