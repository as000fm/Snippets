	/**
	 * Traitement des méta-caractères d'une expression regulière
	 * @param value Le texte de l'expression regulière à traiter
	 * @return le texte de l'expression regulière traité
	 */
	final public static String escapeRegExpMetaChars(String value) {
		if (isEmpty(value)) {
			return value;
		}

		StringBuilder sb = new StringBuilder();

		for (char c : value.toCharArray()) {
			switch (c) {
				case '\\':
				case '^':
				case '$':
				case '.':
				case '|':
				case '?':
				case '*':
				case '+':
				case '(':
				case ')':
				case '[':
				case ']':
				case '{':
				case '}':
					sb.append('\\');
					sb.append(c);
					break;
				default:
					sb.append(c);
					break;
			}
		}

		return sb.toString();
	}
