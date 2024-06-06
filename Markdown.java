	/**
	 * Convertion d'un texte markdown ayant des marqueurs gras et italiques en text sans marqueurs
	 * @param markdown Le texte à convertir
	 * @return le texte convertit sans marqueurs
	 */
	public static final String markdownToPlainText(String markdown) {
		if (isEmpty(markdown)) {
			return markdown;
		}

		String plainText = markdown;

		 // Suppression des gras et italiques
		plainText = plainText.replaceAll("\\*{1,3}(.*?)\\*{1,3}", "$1");
		plainText = plainText.replaceAll("_{1,3}(.*?)_{1,3}", "$1");

		return plainText;
	}

	/**
	 * Convertion d'un texte markdown ayant des marqueurs gras et italiques en html
	 * @param markdown Le texte à convertir
	 * @return le texte convertit en html
	 */
	public static final String markdownToHTML(String markdown) {
		String htmlText = markdown;

		// Traiter en premioer de la combinaison gras italique (***texte***)
		htmlText = htmlText.replaceAll("\\*\\*\\*(.*?)\\*\\*\\*", "<strong><em>$1</em></strong>");
		htmlText = htmlText.replaceAll("___(.*?)___", "<strong><em>$1</em></strong>");

		// Traitement par la suite du gras (**texte**) et italique (*texte* ou _texte_) séparément
		htmlText = htmlText.replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>");
		htmlText = htmlText.replaceAll("__(.*?)__", "<strong>$1</strong>");

		htmlText = htmlText.replaceAll("\\*(.*?)\\*", "<em>$1</em>");
		htmlText = htmlText.replaceAll("_(.*?)_", "<em>$1</em>");

		return htmlText;
	}
