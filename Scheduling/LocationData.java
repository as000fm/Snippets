package schedules.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Available equipments for a given location
 */
public class LocationData {
	/** Location name **/
	private final String locationName;

	/** On-site equipments name list **/
	private final List<String> onSiteEquipmentsList;

	/** Dictionnary of sublocation names and associated off-site equipments name list **/
	private final Map<String, List<String>> offSiteEquipmentsSublocationsMap;

	/** Split indicator required for on-site equipments **/
	private boolean splitRequiredOnSiteEquipments;

	/** Dictionnary of sublocation split indicator required for off-site equipments **/
	private final Map<String, Boolean> splitRequiredOffSiteEquipmentsSublocationsMap;

	public LocationData(String locationName) {
		this.locationName = locationName;
		this.onSiteEquipmentsList = new ArrayList<>();
		this.offSiteEquipmentsSublocationsMap = new LinkedHashMap<>();
		this.splitRequiredOnSiteEquipments = true;
		this.splitRequiredOffSiteEquipmentsSublocationsMap = new LinkedHashMap<>();
	}

	public int getTotalEquipmentsCount() {
		return getOnSiteEquipmentsCount() + getTotalOffSiteEquipmentsSublocationsCount();
	}

	public int getOnSiteEquipmentsCount() {
		return onSiteEquipmentsList.size();
	}

	public int getSublocationsCount() {
		return offSiteEquipmentsSublocationsMap.size();
	}

	public int getTotalOffSiteEquipmentsSublocationsCount() {
		int total = 0;

		for (List<String> offSiteEquipmentsNamesList : offSiteEquipmentsSublocationsMap.values()) {
			total += offSiteEquipmentsNamesList.size();
		}

		return total;
	}

	public int getOffSiteEquipmentsSublocationCount(String sublocation) {
		return offSiteEquipmentsSublocationsMap.get(sublocation).size();
	}

	public boolean addOnSiteEquipment(String equipmentName) {
		return onSiteEquipmentsList.add(equipmentName);
	}

	public boolean addOffSiteEquipmentToSublocation(String sublocatioName, String equipmentName) {
		List<String> offSiteEquipmentsList;

		if (offSiteEquipmentsSublocationsMap.containsKey(sublocatioName)) {
			offSiteEquipmentsList = offSiteEquipmentsSublocationsMap.get(sublocatioName);
		} else {
			offSiteEquipmentsList = new ArrayList<>();
			offSiteEquipmentsSublocationsMap.put(sublocatioName, offSiteEquipmentsList);

			splitRequiredOffSiteEquipmentsSublocationsMap.put(sublocatioName, Boolean.TRUE);
		}

		return offSiteEquipmentsList.add(equipmentName);
	}

	public String getLocationName() {
		return locationName;
	}

	public List<String> getOnSiteEquipmentsList() {
		return onSiteEquipmentsList;
	}

	public Map<String, List<String>> getOffSiteEquipmentsSublocationsMap() {
		return offSiteEquipmentsSublocationsMap;
	}

	public boolean isSplitRequiredOnSiteEquipments() {
		return splitRequiredOnSiteEquipments;
	}

	public void setSplitRequiredOnSiteEquipments(boolean splitRequiredOnSiteEquipments) {
		this.splitRequiredOnSiteEquipments = splitRequiredOnSiteEquipments;
	}

	public Map<String, Boolean> getSplitRequiredOffSiteEquipmentsSublocationsMap() {
		return splitRequiredOffSiteEquipmentsSublocationsMap;
	}
}
