package schedules.data;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Daily schedule of a list of locations and its equipments maintenance by technicians for a given date
 */
public class DailyScheduleData {
	/** Scheduled date of maintenance **/
	private final Date scheduledDate;
	
	/** Dictionnary of a list of locations and its equipments to maintain by technicians for the scheduled date **/
	private final Map<String, List<LocationData>> techniciansLocationsMap;
	
	public DailyScheduleData(Date scheduledDate) {
		this.scheduledDate = scheduledDate;
		this.techniciansLocationsMap = new LinkedHashMap<>();
	}

	public Date getScheduledDate() {
		return scheduledDate;
	}

	public Map<String, List<LocationData>> getTechniciansLocationsMap() {
		return techniciansLocationsMap;
	}
}
