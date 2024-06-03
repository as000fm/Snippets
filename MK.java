package outils.abstractions;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import outils.base.OutilsBase;
import outils.types.FilesCharsetsTypes;

/**
 * Convertisseur d'extensions Markdown à la Pandoc en code html pour MkDocs
 * @author Claude Toupin - 2 juin 2024
 */
public class MarkdownExtensionsTemplateProducer extends TemplateProducer {
	/** Nom du groupe du texte usager pour le patron **/
	private static final String USER_TEXT_GROUP_NAME = "userTest";

	/** Nom du groupe de la balise html pour le patron **/
	private static final String HTML_TAG_GROUP_NAME = "htmlTag";

	/** Nom du groupe du texte de l'attribut class de la balise html pour le patron **/
	private static final String CLASS_ATTRIBUTE_GROUP_NAME = "classAttribute";

	/** Nom du groupe du texte de l'attribut style de la balise html pour le patron **/
	private static final String STYLE_ATTRIBUTE_GROUP_NAME = "styleAttribute";

	/** Indicateur de début du contenu de l'attribut class de la balise par défaut **/
	private static final String CLASS_ATTRIBUTE_MARKER = "#";

	/** Dictionnaire des balises html à substituer **/
	private static final Map<String, String> HTML_TAGS_DICT = new HashMap<>(64);

	static {
		HTML_TAGS_DICT.put("abbr", "abbr"); // Defines an abbreviated form of a longer word or phrase.
		HTML_TAGS_DICT.put("acronym", "abbr"); // Obsolete Defines an acronym. Will use <abbr> tag instead.
		HTML_TAGS_DICT.put("address", "address"); // Specifies the author's contact information.
		HTML_TAGS_DICT.put("b", "b"); // Displays text in a bold style.
		HTML_TAGS_DICT.put("bold", "b"); // Displays text in a bold style. Will use <b> tag instead.
		HTML_TAGS_DICT.put("bdi ", "bdi "); // Represents text that is isolated from its surrounding for the purposes of bidirectional text formatting.
		HTML_TAGS_DICT.put("bdo", "bdo"); // Overrides the current text direction.
		HTML_TAGS_DICT.put("big", CLASS_ATTRIBUTE_MARKER); // Obsolete Displays text in a large size. Will use <span class="big"> instead.
		HTML_TAGS_DICT.put("blockquote", "blockquote"); // Represents a section that is quoted from another source.
		HTML_TAGS_DICT.put("center", CLASS_ATTRIBUTE_MARKER); // Obsolete Align contents in the center. Will use <span class="center"> instead.
		HTML_TAGS_DICT.put("cite", "cite"); // Indicates a citation or reference to another source.
		HTML_TAGS_DICT.put("code", "code"); // Specifies text as computer code.
		HTML_TAGS_DICT.put("data ", "data "); // Links a piece of content with a machine-readable translation.
		HTML_TAGS_DICT.put("del", "del"); // Represents text that has been deleted from the document.
		HTML_TAGS_DICT.put("dfn", "dfn"); // Specifies a definition.
		HTML_TAGS_DICT.put("em", "em"); // Defines emphasized text.
		HTML_TAGS_DICT.put("font", CLASS_ATTRIBUTE_MARKER); // Obsolete Defines font, color, and size for text. Will use <span class="font"> instead.
		HTML_TAGS_DICT.put("i", "i"); // Displays text in an italic style.
		HTML_TAGS_DICT.put("italic", "i"); // Displays text in an italic style. Will use <i> tag instead.
		HTML_TAGS_DICT.put("ins", "ins"); // Defines a block of text that has been inserted into a document.
		HTML_TAGS_DICT.put("kbd", "kbd"); // Specifies text as keyboard input.
		HTML_TAGS_DICT.put("mark ", "mark "); // Represents text highlighted for reference purposes.
		HTML_TAGS_DICT.put("output ", "output "); // Represents the result of a calculation.
		HTML_TAGS_DICT.put("pre", "pre"); // Defines a block of preformatted text.
		HTML_TAGS_DICT.put("progress ", "progress "); // Represents the completion progress of a task.
		HTML_TAGS_DICT.put("q", "q"); // Defines a short inline quotation.
		HTML_TAGS_DICT.put("rp ", "rp "); // Provides fall-back parenthesis for browsers that that don't support ruby annotations.
		HTML_TAGS_DICT.put("rt ", "rt "); // Defines the pronunciation of character presented in a ruby annotations.
		HTML_TAGS_DICT.put("ruby ", "ruby "); // Represents a ruby annotation.
		HTML_TAGS_DICT.put("s", "s"); // Represents contents that are no longer accurate or no longer relevant.
		HTML_TAGS_DICT.put("samp", "samp"); // Specifies text as sample output from a computer program.
		HTML_TAGS_DICT.put("small", "small"); // Displays text in a smaller size.
		HTML_TAGS_DICT.put("smallcaps", "$font-variant: small-caps"); // Displays text in small caps (from Pandoc). Will use <span style="font-variant: small-caps;"> tag instead.
		HTML_TAGS_DICT.put("span", "span"); // Defines an inline styleless section in a document.
		HTML_TAGS_DICT.put("strike", "s"); // Obsolete Displays text in strikethrough style. Will use <s> tag instead.
		HTML_TAGS_DICT.put("strong", "strong"); // Indicate strongly emphasized text.
		HTML_TAGS_DICT.put("sub", "sub"); // Defines subscripted text.
		HTML_TAGS_DICT.put("sup", "sup"); // Defines superscripted text.
		HTML_TAGS_DICT.put("tt", "code"); // Obsolete Displays text in a teletype style. Will use <code> tag instead.
		HTML_TAGS_DICT.put("u", "u"); // Displays text with an underline.
		HTML_TAGS_DICT.put("underline", "u"); // Displays text with an underline (from Pandoc). Will use <u> tag instead.
		HTML_TAGS_DICT.put("var", "var"); // Defines a variable.
		HTML_TAGS_DICT.put("wbr ", "wbr "); // Represents a line break opportunity.
	}

	/** Délimiteur de début de l'extension **/
	private String startDelimiter;

	/** Délimiteur de séparation de l'extension **/
	private String splitDelimiter;

	/** Délimiteur de fin de l'extension **/
	private String endDelimiter;

	/** Indicateur de début de la balise html **/
	private String htmlTagMarker;

	/** Indicateur de début du contenu de l'attribut class de la balise **/
	private String classAttributeMarker;

	/** Indicateur de début du contenu de l'attribut style de la balise **/
	private String styleAttributeMarker;

	/** Longueur du délimiteur de début de l'extension **/
	private int startDelimiterLenght;

	/** Longueur du délimiteur de séparation de l'extension **/
	private int splitDelimiterLenght;

	/** Longueur du délimiteur de fin de l'extension **/
	private int endDelimiterLenght;

	/** Patron pour extraire les données **/
	private Pattern pattern;

	/**
	 * Constructeur de base
	 */
	public MarkdownExtensionsTemplateProducer() {
		super();
		init();
	}

	/**
	 * Constructeur de base
	 * @param template Contenu du modèle
	 */
	public MarkdownExtensionsTemplateProducer(List<String> template) {
		super(template);
		init();
	}

	/**
	 * Constructeur de base
	 * @param baseDir Répertoire de base
	 * @param templateFiles Liste de noms complets de fichiers modèles
	 * @throws Exception en cas d'erreur...
	 */
	public MarkdownExtensionsTemplateProducer(String baseDir, String... templateFiles) throws Exception {
		super(baseDir, templateFiles);
		init();
	}

	/**
	 * Constructeur de base
	 * @param baseDir Répertoire de base
	 * @param templateFile Nom complet du fichier modèle
	 * @param charsetType Type de jeu de caractères
	 * @throws Exception en cas d'erreur...
	 */
	public MarkdownExtensionsTemplateProducer(String baseDir, String templateFile, FilesCharsetsTypes charsetType) throws Exception {
		super(baseDir, templateFile, charsetType);
		init();
	}

	/**
	 * Constructeur de base
	 * @param baseDir Répertoire de base
	 * @param charsetType Type de jeu de caractères
	 * @param templateFiles Liste de noms complets de fichiers modèles
	 * @throws Exception en cas d'erreur...
	 */
	public MarkdownExtensionsTemplateProducer(String baseDir, FilesCharsetsTypes charsetType, String... templateFiles) throws Exception {
		super(baseDir, charsetType, templateFiles);
		init();
	}

	/**
	 * Constructeur de base
	 * @param inputStream Liste de flux de données de fichiers modèles
	 * @throws Exception en cas d'erreur...
	 */
	public MarkdownExtensionsTemplateProducer(InputStream... inputStreams) throws Exception {
		super(inputStreams);
		init();
	}

	/**
	 * Constructeur de base
	 * @param inputStream Flux de données du fichier modèle
	 * @param charsetType Type de jeu de caractères
	 * @throws Exception en cas d'erreur...
	 */
	public MarkdownExtensionsTemplateProducer(InputStream inputStream, FilesCharsetsTypes charsetType) throws Exception {
		super(inputStream, charsetType);
		init();
	}

	/**
	 * Constructeur de base
	 * @param charsetType Type de jeu de caractères
	 * @param inputStreams Liste de flux de données de fichiers modèles
	 * @throws Exception en cas d'erreur...
	 */
	public MarkdownExtensionsTemplateProducer(FilesCharsetsTypes charsetType, InputStream... inputStreams) throws Exception {
		super(charsetType, inputStreams);
		init();
	}

	/**
	 * Initialisation
	 */
	protected void init() {
		this.startDelimiter = "[";
		this.splitDelimiter = "]{";
		this.endDelimiter = "}";
		this.htmlTagMarker = ".";
		this.classAttributeMarker = "#";
		this.styleAttributeMarker = "$";

		initPattern();
	}

	/**
	 * Initialisation du patron
	 */
	protected void initPattern() {
		this.startDelimiterLenght = this.startDelimiter.length();
		this.splitDelimiterLenght = this.splitDelimiter.length();
		this.endDelimiterLenght = this.endDelimiter.length();

		// String regex = "\\[(?<userText>.*)\\]\\{(\\.(?<htmlTag>[a-zA-Z]*))?(#(?<classAttribute>[^$]*))?(\\$(?<styleAttribute>[^}]*))?\\}";

		StringBuilder sb = new StringBuilder();
		sb.append(OutilsBase.escapeRegExpMetaChars(startDelimiter));
		sb.append(getGroup(USER_TEXT_GROUP_NAME, ".", false));
		sb.append(OutilsBase.escapeRegExpMetaChars(splitDelimiter));
		sb.append(getMakerGroup(htmlTagMarker, HTML_TAG_GROUP_NAME, "[a-zA-Z]", true));
		sb.append(getMakerGroup(classAttributeMarker, CLASS_ATTRIBUTE_GROUP_NAME, "[^" + styleAttributeMarker + "]", true));
		sb.append(getMakerGroup(styleAttributeMarker, STYLE_ATTRIBUTE_GROUP_NAME, "[^" + endDelimiter + "]", true));
		sb.append(OutilsBase.escapeRegExpMetaChars(endDelimiter));

		this.pattern = Pattern.compile(sb.toString());
	}

	/**
	 * Extrait le texte d'un groupe
	 * @param groupName Le nom du groupe
	 * @param text Le text à recherche
	 * @param optional Indicateur de groupe optionel
	 * @return le texte du groupe
	 */
	protected String getGroup(String groupName, String text, boolean optional) {
		StringBuilder sb = new StringBuilder();
		sb.append("(?<");
		sb.append(groupName);
		sb.append(">");
		sb.append(text);
		sb.append(optional ? "*))?" : "*)");

		return sb.toString();
	}

	/**
	 * Extrait le texte d'un groupe avec marqueur
	 * @param marker Le marqueur du groupe
	 * @param groupName Le nom du groupe
	 * @param text Le text à recherche
	 * @param optional Indicateur de groupe optionel
	 * @return le texte du groupe
	 */
	protected String getMakerGroup(String marker, String groupName, String text, boolean optional) {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		sb.append(OutilsBase.escapeRegExpMetaChars(marker));
		sb.append(getGroup(groupName, text, optional));

		return sb.toString();
	}
	
	// ``` {include="../src/doc/TestExecutionOrderFirst.java" startLine=11 endLine=25 dedent=4 .java}

	/*
	 * (non-Javadoc)
	 * @see outils.abstractions.TemplateProducer#afterProduceLine(java.lang.String)
	 */
	@Override
	protected String afterProduceLine(String line) throws Exception {
		int startPos = line.indexOf(startDelimiterLenght);

		while (startPos != -1) {
			int splitPos = line.indexOf(splitDelimiter, startPos + startDelimiterLenght);

			if (splitPos == -1) {
				break;
			}

			int endPos = line.indexOf(endDelimiterLenght, splitPos + splitDelimiterLenght);

			if (endPos == -1) {
				break;
			}

			int endAt = endPos + endDelimiterLenght;

			String text = line.substring(startPos, endAt);

			Matcher matcher = pattern.matcher(text);

			if (matcher.matches()) {
				StringBuilder sb = new StringBuilder(line.substring(0, startPos));

				String userText = OutilsBase.asString(matcher.group(USER_TEXT_GROUP_NAME));
				String htmlTag = OutilsBase.asString(matcher.group(HTML_TAG_GROUP_NAME)).trim();
				String classAttribute = OutilsBase.asString(matcher.group(CLASS_ATTRIBUTE_GROUP_NAME)).trim();
				String styleAttribute = OutilsBase.asString(matcher.group(STYLE_ATTRIBUTE_GROUP_NAME)).trim();

				if (OutilsBase.isEmpty(userText)) {
					endAt = sb.length();
				} else if (OutilsBase.isEmpty(htmlTag) && OutilsBase.isEmpty(classAttribute) && OutilsBase.isEmpty(styleAttribute)) {
					sb.append(userText);
					endAt = sb.length();
				} else {
					String tag = "span";

					if (!OutilsBase.isEmpty(htmlTag) && HTML_TAGS_DICT.containsKey(htmlTag)) {
						String value = HTML_TAGS_DICT.get(htmlTag);

						if (OutilsBase.areEquals(value, CLASS_ATTRIBUTE_MARKER)) {
							classAttribute = (htmlTag + " " + classAttribute).trim();
						} else {
							tag = value;
						}

						sb.append('<');
						sb.append(tag);

						if (!OutilsBase.isEmpty(classAttribute)) {
							sb.append(" class=\"");
							sb.append(classAttribute);
							sb.append('"');
						}

						if (!OutilsBase.isEmpty(styleAttribute)) {
							sb.append(" style=\"");
							sb.append(styleAttribute);
							sb.append('"');
						}

						sb.append('>');
						sb.append(userText);
						sb.append("</");
						sb.append(tag);
						sb.append('>');
					} else {
						sb.append(text);
					}

					endAt = sb.length();
				}

				sb.append(line.substring(endAt));

				line = sb.toString();
			}

			startPos = line.indexOf(startDelimiterLenght, endAt);
		}

		return line;
	}

	/*
	 * (non-Javadoc)
	 * @see outils.abstractions.TemplateProducerBase#produce(java.util.List)
	 */
	@Override
	public List<String> produce(List<String> template) throws Exception {
		initPattern();

		return super.produce(template);
	}

	/**
	 * Extrait le champ startDelimiter
	 * @return un String
	 */
	public String getStartDelimiter() {
		return startDelimiter;
	}

	/**
	 * Modifie le champ startDelimiter
	 * @param startDelimiter La valeur du champ startDelimiter
	 */
	public void setStartDelimiter(String startDelimiter) {
		if (OutilsBase.isEmpty(startDelimiter)) {
			throw new RuntimeException("Pas de valeur pour startDelimiter");
		}

		this.startDelimiter = startDelimiter;

		initPattern();
	}

	/**
	 * Extrait le champ splitDelimiter
	 * @return un String
	 */
	public String getSplitDelimiter() {
		return splitDelimiter;
	}

	/**
	 * Modifie le champ splitDelimiter
	 * @param splitDelimiter La valeur du champ splitDelimiter
	 */
	public void setSplitDelimiter(String splitDelimiter) {
		if (OutilsBase.isEmpty(splitDelimiter)) {
			throw new RuntimeException("Pas de valeur pour splitDelimiter");
		}

		this.splitDelimiter = splitDelimiter;

		initPattern();
	}

	/**
	 * Extrait le champ endDelimiter
	 * @return un String
	 */
	public String getEndDelimiter() {
		return endDelimiter;
	}

	/**
	 * Modifie le champ endDelimiter
	 * @param endDelimiter La valeur du champ endDelimiter
	 */
	public void setEndDelimiter(String endDelimiter) {
		if (OutilsBase.isEmpty(endDelimiter)) {
			throw new RuntimeException("Pas de valeur pour endDelimiter");
		}

		this.endDelimiter = endDelimiter;

		initPattern();
	}

	/**
	 * Extrait le champ htmlTagMarker
	 * @return un String
	 */
	public String getHtmlTagMarker() {
		return htmlTagMarker;
	}

	/**
	 * Modifie le champ htmlTagMarker
	 * @param htmlTagMarker La valeur du champ htmlTagMarker
	 */
	public void setHtmlTagMarker(String htmlTagMarker) {
		if (OutilsBase.isEmpty(htmlTagMarker)) {
			throw new RuntimeException("Pas de valeur pour htmlTagMarker");
		}

		this.htmlTagMarker = htmlTagMarker;

		initPattern();
	}

	/**
	 * Extrait le champ classAttributeMarker
	 * @return un String
	 */
	public String getClassAttributeMarker() {
		return classAttributeMarker;
	}

	/**
	 * Modifie le champ classAttributeMarker
	 * @param classAttributeMarker La valeur du champ classAttributeMarker
	 */
	public void setClassAttributeMarker(String classAttributeMarker) {
		if (OutilsBase.isEmpty(classAttributeMarker)) {
			throw new RuntimeException("Pas de valeur pour classAttributeMarker");
		}

		this.classAttributeMarker = classAttributeMarker;

		initPattern();
	}

	/**
	 * Extrait le champ styleAttributeMarker
	 * @return un String
	 */
	public String getStyleAttributeMarker() {
		return styleAttributeMarker;
	}

	/**
	 * Modifie le champ styleAttributeMarker
	 * @param styleAttributeMarker La valeur du champ styleAttributeMarker
	 */
	public void setStyleAttributeMarker(String styleAttributeMarker) {
		if (OutilsBase.isEmpty(styleAttributeMarker)) {
			throw new RuntimeException("Pas de valeur pour styleAttributeMarker");
		}

		this.styleAttributeMarker = styleAttributeMarker;

		initPattern();
	}

	/**
	 * Extrait le champ startDelimiterLenght
	 * @return un int
	 */
	public int getStartDelimiterLenght() {
		return startDelimiterLenght;
	}

	/**
	 * Extrait le champ splitDelimiterLenght
	 * @return un int
	 */
	public int getSplitDelimiterLenght() {
		return splitDelimiterLenght;
	}

	/**
	 * Extrait le champ endDelimiterLenght
	 * @return un int
	 */
	public int getEndDelimiterLenght() {
		return endDelimiterLenght;
	}

}
