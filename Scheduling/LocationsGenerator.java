package generators;

import java.security.SecureRandom;

/**
 * Test locations generator
 */
public class LocationsGenerator {
	private static final SecureRandom RANDOM = new SecureRandom();

	private static int random(int max) {
		return random(0, max);
	}

	private static int random(int min, int max) {
		return min + RANDOM.nextInt(1 + (max - min));
	}

	public static void main(String[] args) {
		System.out.println("LocationData location;");
		System.out.println();

		for (int locationId = 1; locationId <= 25; locationId++) {
			int onSitesCount;
			int subLocationsCount;

			do {
				onSitesCount = random(10);
				subLocationsCount = random(5);
			} while (onSitesCount == 0 && subLocationsCount == 0);

			System.out.println(String.format("location = new LocationData(\"Loc-%d\");", locationId));

			for (int onSiteId = 1; onSiteId <= onSitesCount; onSiteId++) {
				System.out.println(String.format("location.getOnSiteEquipmentsList().add(\"E%02d\");", onSiteId));
			}

			for (int subLocationId = 1; subLocationId <= subLocationsCount; subLocationId++) {
				String subLocationName = String.format("Sub%d", subLocationId);

				int offSitesCount = random(1, 5);

				for (int offSiteId = 1; offSiteId <= offSitesCount; offSiteId++) {
					System.out.println(String.format("location.addOffSiteEquipmentToSublocation(\"%s\", \"E%02d-%d\");", subLocationName, offSiteId, subLocationId));
				}
			}

			System.out.println("LOCATIONS_LIST.add(location);");
			System.out.println();
		}
	}

}

