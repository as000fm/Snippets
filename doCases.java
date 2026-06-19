	/**
	 * Effectue la conversion en format "kebab-case" ex: AlloToto -> allo-toto
	 * @param value La valeur à traiter
	 * @return la valeur convertie
	 */
	public static String doKebabCase(String value) {
		if (!isEmpty(value)) {
			return value //
					.replaceAll("([a-z])([A-Z])", "$1 $2") //
					.replaceAll("[_\\s]+", "-") //
					.replaceAll("[^a-zA-Z0-9-]", "") //
					.replaceAll("-{2,}", "-") //
					.toLowerCase() //
					.replaceAll("^-|-$", "") //
			;
		}

		return value;
	}

	/**
	 * Effectue la conversion en format "snake_case" ex: AlloToto -> allo_toto
	 * @param value La valeur à traiter
	 * @return la valeur convertie
	 */
	public static String doSnakeCase(String value) {
		if (!isEmpty(value)) {
			return value //
					.replaceAll("([a-z])([A-Z])", "$1 $2") //
					.replaceAll("[-\\s]+", "_") //
					.replaceAll("[^a-zA-Z0-9_]", "") //
					.replaceAll("_{2,}", "_") //
					.toLowerCase() //
					.replaceAll("^_|_$", "") //
			;
		}

		return value;
	}
