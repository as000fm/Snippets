package outils.abstractions;

import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import outils.base.OutilsBase;
import outils.commun.OutilsCommun;
import outils.types.FilesCharsetsTypes;

/**
 * Extensions markdown converties en code html pour MkDocs
 * @author Claude Toupin - 2 juin 2024
 */
public class MarkdownExtensionsTemplateProducer extends TemplateProducer {
	/** Séparateur d'une paire nom=valeur d'un paramètre **/
	private static final String PARAMETER_SEPARATOR = "=";

	/** Séparateur de texte d'une valeur **/
	private static final String TEXT_SEPARATOR = "\\\"";

	/** Séparateur d'espaces blancs **/
	private static final String SPACES_SEPARATOR = "\\s*";

	/** Séparateur de groupes **/
	private static final String GROUPS_SEPARATOR = ")|(";

	/** Patron de valeur numérique **/
	private static final String NUMBER_VALUE_PATTERN = "\\d+";

	/** Délimiteur accolade de début d'une séquence **/
	private static final String OPEN_BRACE_DELIMITER = "{";

	/** Délimiteur accolade de fin d'une séquence **/
	private static final String CLOSE_BRACE_DELIMITER = "}";

	/** Délimiteur parenthèse de début d'une séquence **/
	private static final String OPEN_PARENTHESE_DELIMITER = "(";

	/** Délimiteur parenthèse de fin d'une séquence **/
	private static final String CLOSE_PARENTHESE_DELIMITER = ")";

	/** Délimiteur crochet de début d'une séquence **/
	private static final String OPEN_BRACKET_DELIMITER = "[";

	/** Délimiteur crochet de fin d'une séquence **/
	private static final String CLOSE_BRACKET_DELIMITER = "]";

	// Inclusion d'un fichier

	/** Nom du groupe du mot clé include de l'inclusion d'un fichier **/
	private static final String INCLUDE_INCLUDE_KEYWORD_GROUP_NAME = "include";

	/** Nom du groupe de la valeur du mot clé include de l'inclusion d'un fichier **/
	private static final String INCLUDE_INCLUDE_VALUE_GROUP_NAME = "includeValue";

	/** Nom du groupe du mot clé startLine/start-line de l'inclusion d'un fichier **/
	private static final String INCLUDE_START_LINE_KEYWORD_GROUP_NAME = "startLine";

	/** Nom du groupe de la valeur du mot clé startLine/start-line de l'inclusion d'un fichier **/
	private static final String INCLUDE_START_LINE_VALUE_GROUP_NAME = "startLineValue";

	/** Nom du groupe du mot clé endLine/end-line de l'inclusion d'un fichier **/
	private static final String INCLUDE_END_LINE_KEYWORD_GROUP_NAME = "endLine";

	/** Nom du groupe de la valeur du mot clé endLine/end-line de l'inclusion d'un fichier **/
	private static final String INCLUDE_END_LINE_VALUE_GROUP_NAME = "endLineValue";

	/** Nom du groupe du mot clé dedent de l'inclusion d'un fichier **/
	private static final String INCLUDE_DEDENT_KEYWORD_GROUP_NAME = "dedent";

	/** Nom du groupe de la valeur du mot clé dedent de l'inclusion d'un fichier **/
	private static final String INCLUDE_DEDENT_VALUE_GROUP_NAME = "dedentValue";

	/** Nom du groupe du mot clé indent de l'inclusion d'un fichier **/
	private static final String INCLUDE_INDENT_KEYWORD_GROUP_NAME = "indent";

	/** Nom du groupe de la valeur du mot clé indent de l'inclusion d'un fichier **/
	private static final String INCLUDE_INDENT_VALUE_GROUP_NAME = "indentValue";

	// Légende d'une figure (image)

	/** Délimiteur de début de la légende d'une figure **/
	private static final String START_FIGURE_CAPTION_DELIMITER = "!!" + OPEN_BRACKET_DELIMITER;

	/** Délimiteur de fin de la légende d'une figure **/
	private static final String END_FIGURE_CAPTION_DELIMITER = CLOSE_PARENTHESE_DELIMITER;

	/** Indicateur de début du contenu de la position de la légende d'une figure **/
	private static final String FIGURE_CAPTION_POSITION_ATTRIBUTE_MARKER = ".";

	/** Indicateur de début du contenu de l'attribut class de la légende d'une figure **/
	private static final String FIGURE_CAPTION_CLASS_ATTRIBUTE_MARKER = "#";

	/** Indicateur de début du contenu de l'attribut style de la légende d'une figure **/
	private static final String FIGURE_CAPTION_STYLE_ATTRIBUTE_MARKER = "$";

	/** Nom du groupe du texte alternatif (i.e. légende) de la légende d'une figure **/
	private static final String FIGURE_CAPTION_ALT_TEXT_GROUP_NAME = "altText";

	/** Nom du groupe de la position des attributs de la légende d'une figure **/
	private static final String FIGURE_CAPTION_POSITION_GROUP_NAME = "position";

	/** Nom du groupe du texte de l'attribut class de la balise html des attributs de la légende d'une figure **/
	private static final String FIGURE_CAPTION_CLASS_ATTRIBUTE_GROUP_NAME = "classAttribute";

	/** Nom du groupe du texte de l'attribut style de la balise html des attributs de la légende d'une figure **/
	private static final String FIGURE_CAPTION_STYLE_ATTRIBUTE_GROUP_NAME = "styleAttribute";

	/** Nom du groupe de l'url de l'image de la légende d'une figure **/
	private static final String FIGURE_CAPTION_IMAGE_URL_GROUP_NAME = "imageURL";

	/** Nom du groupe du texte du titre de l'image de la légende d'une figure **/
	private static final String FIGURE_CAPTION_TITLE_GROUP_NAME = "title";

	// Extension Markdown

	/** Délimiteur de début de l'extension Markdown **/
	private static final String START_MARKDOWN_DELIMITER = OPEN_BRACKET_DELIMITER;

	/** Délimiteur de séparation de l'extension Markdown **/
	private static final String SPLIT_MARKDOWN_DELIMITER = CLOSE_BRACKET_DELIMITER + OPEN_BRACE_DELIMITER;

	/** Délimiteur de fin de l'extension Markdown **/
	private static final String END_MARKDOWN_DELIMITER = CLOSE_BRACE_DELIMITER;

	/** Indicateur de début du contenu de la balise html pour le patron MarkDown **/
	private static final String MARKDOWN_HTML_ATTRIBUTE_MARKER = ".";

	/** Indicateur de début du contenu de l'attribut class de la balise html pour le patron MarkDown **/
	private static final String MARKDOWN_CLASS_ATTRIBUTE_MARKER = "#";

	/** Indicateur de début du contenu de l'attribut style de la balise html pour le patron MarkDown **/
	private static final String MARKDOWN_STYLE_ATTRIBUTE_MARKER = "$";

	/** Nom du groupe du texte usager pour le patron MarkDown **/
	private static final String MARKDOWN_USER_TEXT_GROUP_NAME = "userTest";

	/** Nom du groupe de la balise html pour le patron MarkDown **/
	private static final String MARKDOWN_HTML_TAG_GROUP_NAME = "htmlTag";

	/** Nom du groupe du texte de l'attribut class de la balise html pour le patron MarkDown **/
	private static final String MARKDOWN_CLASS_ATTRIBUTE_GROUP_NAME = "classAttribute";

	/** Nom du groupe du texte de l'attribut style de la balise html pour le patron MarkDown **/
	private static final String MARKDOWN_STYLE_ATTRIBUTE_GROUP_NAME = "styleAttribute";

	/** Dictionnaire des balises html des extensions Markdown à substituer **/
	private static final Map<String, String> MARKDOWN_HTML_TAGS_DICT = new HashMap<>(64);

	static {
		MARKDOWN_HTML_TAGS_DICT.put("abbr", "abbr"); // Defines an abbreviated form of a longer word or phrase.
		MARKDOWN_HTML_TAGS_DICT.put("acronym", "abbr"); // Obsolete Defines an acronym. Will use <abbr> tag instead.
		MARKDOWN_HTML_TAGS_DICT.put("address", "address"); // Specifies the author's contact information.
		MARKDOWN_HTML_TAGS_DICT.put("b", "b"); // Displays text in a bold style.
		MARKDOWN_HTML_TAGS_DICT.put("bold", "b"); // Displays text in a bold style. Will use <b> tag instead.
		MARKDOWN_HTML_TAGS_DICT.put("bdi", "bdi"); // Represents text that is isolated from its surrounding for the purposes of bidirectional text formatting.
		MARKDOWN_HTML_TAGS_DICT.put("bdo", "bdo"); // Overrides the current text direction.
		MARKDOWN_HTML_TAGS_DICT.put("big", MARKDOWN_CLASS_ATTRIBUTE_MARKER); // Obsolete Displays text in a large size. Will use <span class="big"> instead.
		MARKDOWN_HTML_TAGS_DICT.put("blockquote", "blockquote"); // Represents a section that is quoted from another source.
		MARKDOWN_HTML_TAGS_DICT.put("center", MARKDOWN_CLASS_ATTRIBUTE_MARKER); // Obsolete Align contents in the center. Will use <span class="center"> instead.
		MARKDOWN_HTML_TAGS_DICT.put("cite", "cite"); // Indicates a citation or reference to another source.
		MARKDOWN_HTML_TAGS_DICT.put("code", "code"); // Specifies text as computer code.
		MARKDOWN_HTML_TAGS_DICT.put("data", "data"); // Links a piece of content with a machine-readable translation.
		MARKDOWN_HTML_TAGS_DICT.put("del", "del"); // Represents text that has been deleted from the document.
		MARKDOWN_HTML_TAGS_DICT.put("dfn", "dfn"); // Specifies a definition.
		MARKDOWN_HTML_TAGS_DICT.put("em", "em"); // Defines emphasized text.
		MARKDOWN_HTML_TAGS_DICT.put("font", MARKDOWN_CLASS_ATTRIBUTE_MARKER); // Obsolete Defines font, color, and size for text. Will use <span class="font"> instead.
		MARKDOWN_HTML_TAGS_DICT.put("i", "i"); // Displays text in an italic style.
		MARKDOWN_HTML_TAGS_DICT.put("italic", "i"); // Displays text in an italic style. Will use <i> tag instead.
		MARKDOWN_HTML_TAGS_DICT.put("ins", "ins"); // Defines a block of text that has been inserted into a document.
		MARKDOWN_HTML_TAGS_DICT.put("kbd", "kbd"); // Specifies text as keyboard input.
		MARKDOWN_HTML_TAGS_DICT.put("mark", "mark"); // Represents text highlighted for reference purposes.
		MARKDOWN_HTML_TAGS_DICT.put("output", "output"); // Represents the result of a calculation.
		MARKDOWN_HTML_TAGS_DICT.put("pre", "pre"); // Defines a block of preformatted text.
		MARKDOWN_HTML_TAGS_DICT.put("progress", "progress"); // Represents the completion progress of a task.
		MARKDOWN_HTML_TAGS_DICT.put("q", "q"); // Defines a short inline quotation.
		MARKDOWN_HTML_TAGS_DICT.put("rp", "rp"); // Provides fall-back parenthesis for browsers that that don't support ruby annotations.
		MARKDOWN_HTML_TAGS_DICT.put("rt", "rt"); // Defines the pronunciation of character presented in a ruby annotations.
		MARKDOWN_HTML_TAGS_DICT.put("ruby", "ruby"); // Represents a ruby annotation.
		MARKDOWN_HTML_TAGS_DICT.put("s", "s"); // Represents contents that are no longer accurate or no longer relevant.
		MARKDOWN_HTML_TAGS_DICT.put("samp", "samp"); // Specifies text as sample output from a computer program.
		MARKDOWN_HTML_TAGS_DICT.put("small", "small"); // Displays text in a smaller size.
		MARKDOWN_HTML_TAGS_DICT.put("smallcaps", "$font-variant: small-caps"); // Displays text in small caps (from Pandoc). Will use <span style="font-variant: small-caps;"> tag instead.
		MARKDOWN_HTML_TAGS_DICT.put("span", "span"); // Defines an inline styleless section in a document.
		MARKDOWN_HTML_TAGS_DICT.put("strike", "s"); // Obsolete Displays text in strikethrough style. Will use <s> tag instead.
		MARKDOWN_HTML_TAGS_DICT.put("strong", "strong"); // Indicate strongly emphasized text.
		MARKDOWN_HTML_TAGS_DICT.put("sub", "sub"); // Defines subscripted text.
		MARKDOWN_HTML_TAGS_DICT.put("sup", "sup"); // Defines superscripted text.
		MARKDOWN_HTML_TAGS_DICT.put("tt", "code"); // Obsolete Displays text in a teletype style. Will use <code> tag instead.
		MARKDOWN_HTML_TAGS_DICT.put("u", "u"); // Displays text with an underline.
		MARKDOWN_HTML_TAGS_DICT.put("underline", "u"); // Displays text with an underline (from Pandoc). Will use <u> tag instead.
		MARKDOWN_HTML_TAGS_DICT.put("var", "var"); // Defines a variable.
		MARKDOWN_HTML_TAGS_DICT.put("wbr", "wbr"); // Represents a line break opportunity.
	}

	/**
	 * Classe des données d'une substitution effectuée
	 */
	protected class PartData {
		/** Début du texte dans la source **/
		private final int start;

		/** Fin du texte dans la source **/
		private final int end;

		/** Texte en entrée à substituer **/
		private final String input;

		/** Texte en sortie substitué **/
		private String output;

		/**
		 * Constructeur de base
		 * @param matcher Donnée d'une recherche par expression régulière
		 */
		public PartData(Matcher matcher) {
			this(matcher.start(), matcher.end(), matcher.group());
		}

		/**
		 * Constructeur de base
		 * @param start Début du texte dans la source
		 * @param end Fin du texte dans la source
		 * @param input Texte en entrée à substituer
		 */
		public PartData(int start, int end, String input) {
			this.start = start;
			this.end = end;
			this.input = input;
			this.output = null;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "PartData [start=" + start + ", end=" + end + ", input=" + input + ", output=" + output + "]";
		}

		/**
		 * Extrait le champ start
		 * @return un int
		 */
		public int getStart() {
			return start;
		}

		/**
		 * Extrait le champ end
		 * @return un int
		 */
		public int getEnd() {
			return end;
		}

		/**
		 * Extrait le champ input
		 * @return un String
		 */
		public String getInput() {
			return input;
		}

		/**
		 * Extrait le champ output
		 * @return un String
		 */
		public String getOutput() {
			return output;
		}

		/**
		 * Modifie le champ output
		 * @param output La valeur du champ output
		 */
		public void setOutput(String output) {
			this.output = output;
		}

	}

	/** Patron pour la recherche d'une inclusion d'un fichier **/
	private Pattern includeSearchPattern;

	/** Patron pour extraire les données de l'inclusion d'un fichier **/
	private Pattern includePattern;

	/** Patron pour la recherche d'une légende d'une figure **/
	private Pattern figureCaptionSearchPattern;

	/** Patron pour extraire les données de la légende d'une figure **/
	private Pattern figureCaptionPattern;

	/** Patron pour la recherche d'une extension Markdown **/
	private Pattern markdownSearchPattern;

	/** Patron pour extraire les données de l'extension Markdown **/
	private Pattern markdownPattern;

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
		// Recherche de l'inclusion d'un fichier: \{[^\}]*\}

		this.includeSearchPattern = Pattern.compile("\\{[^\\}]*\\}");

		// Inclusion d'un fichier sans les accolades: ((?<include>include)=\"(?<includeValue>.*)\")|((?<startLine>start[-]?[Ll]ine)=(?<startLineValue>\d+))|((?<endLine>end[-]?[Ll]ine)=(?<endLineValue>\d+))|((?<dedent>dedent)=(?<dedentValue>\d+))|((?<indent>indent)=(?<indentValue>\d+))

		StringBuilder sb = new StringBuilder();
		sb.append(OPEN_PARENTHESE_DELIMITER);
		sb.append(getGroup(INCLUDE_INCLUDE_KEYWORD_GROUP_NAME, INCLUDE_INCLUDE_KEYWORD_GROUP_NAME));
		sb.append(OutilsBase.escapeRegExpMetaChars(PARAMETER_SEPARATOR));
		sb.append(TEXT_SEPARATOR);
		sb.append(getGroupOption(INCLUDE_INCLUDE_VALUE_GROUP_NAME, ".", false));
		sb.append(TEXT_SEPARATOR);
		sb.append(GROUPS_SEPARATOR);
		sb.append(getGroup(INCLUDE_START_LINE_KEYWORD_GROUP_NAME, "start[-]?[Ll]ine"));
		sb.append(OutilsBase.escapeRegExpMetaChars(PARAMETER_SEPARATOR));
		sb.append(getGroup(INCLUDE_START_LINE_VALUE_GROUP_NAME, NUMBER_VALUE_PATTERN));
		sb.append(GROUPS_SEPARATOR);
		sb.append(getGroup(INCLUDE_END_LINE_KEYWORD_GROUP_NAME, "end[-]?[Ll]ine"));
		sb.append(OutilsBase.escapeRegExpMetaChars(PARAMETER_SEPARATOR));
		sb.append(getGroup(INCLUDE_END_LINE_VALUE_GROUP_NAME, NUMBER_VALUE_PATTERN));
		sb.append(GROUPS_SEPARATOR);
		sb.append(getGroup(INCLUDE_DEDENT_KEYWORD_GROUP_NAME, INCLUDE_DEDENT_KEYWORD_GROUP_NAME));
		sb.append(OutilsBase.escapeRegExpMetaChars(PARAMETER_SEPARATOR));
		sb.append(getGroup(INCLUDE_DEDENT_VALUE_GROUP_NAME, NUMBER_VALUE_PATTERN));
		sb.append(GROUPS_SEPARATOR);
		sb.append(getGroup(INCLUDE_INDENT_KEYWORD_GROUP_NAME, INCLUDE_INDENT_KEYWORD_GROUP_NAME));
		sb.append(OutilsBase.escapeRegExpMetaChars(PARAMETER_SEPARATOR));
		sb.append(getGroup(INCLUDE_INDENT_VALUE_GROUP_NAME, NUMBER_VALUE_PATTERN));
		sb.append(CLOSE_PARENTHESE_DELIMITER);

		this.includePattern = Pattern.compile(sb.toString());

		// Recherche d'une légende d'une figure : !!\[[^\]]*\](\{[^\}]*\})?\([^\)]*\)

		this.figureCaptionSearchPattern = Pattern.compile("!!\\[[^\\]]*\\](\\{[^\\}]*\\})?\\([^\\)]*\\)");

		// Légende d'une figure: !!\[(?<altText>.*)\](\{(\.(?<position>(top|bottom)))?(#(?<classAttribute>[^$]*))?(\$(?<styleAttribute>[^}]*))?\})?\((?<imageURL>.*)\s*\"(?<title>.*)\"\)

		sb = new StringBuilder();
		sb.append(OutilsBase.escapeRegExpMetaChars(START_FIGURE_CAPTION_DELIMITER));
		sb.append(getGroupOption(FIGURE_CAPTION_ALT_TEXT_GROUP_NAME, ".", false));
		sb.append(OutilsBase.escapeRegExpMetaChars(CLOSE_BRACKET_DELIMITER));
		sb.append(OPEN_PARENTHESE_DELIMITER);
		sb.append(OutilsBase.escapeRegExpMetaChars(OPEN_BRACE_DELIMITER));
		sb.append(getMarkerGroup(FIGURE_CAPTION_POSITION_ATTRIBUTE_MARKER, FIGURE_CAPTION_POSITION_GROUP_NAME, "(top|bottom)", false, true));
		sb.append(getMarkerGroup(FIGURE_CAPTION_CLASS_ATTRIBUTE_MARKER, FIGURE_CAPTION_CLASS_ATTRIBUTE_GROUP_NAME, "[^" + FIGURE_CAPTION_STYLE_ATTRIBUTE_MARKER + "]", true));
		sb.append(getMarkerGroup(FIGURE_CAPTION_STYLE_ATTRIBUTE_MARKER, FIGURE_CAPTION_STYLE_ATTRIBUTE_GROUP_NAME, "[^}]", true));
		sb.append(OutilsBase.escapeRegExpMetaChars(CLOSE_BRACE_DELIMITER));
		sb.append(")?");
		sb.append(OutilsBase.escapeRegExpMetaChars(OPEN_PARENTHESE_DELIMITER));
		sb.append(getGroupOption(FIGURE_CAPTION_IMAGE_URL_GROUP_NAME, ".", false));
		sb.append(SPACES_SEPARATOR);
		sb.append(TEXT_SEPARATOR);
		sb.append(getGroupOption(FIGURE_CAPTION_TITLE_GROUP_NAME, ".", false));
		sb.append(TEXT_SEPARATOR);
		sb.append(OutilsBase.escapeRegExpMetaChars(END_FIGURE_CAPTION_DELIMITER));

		this.figureCaptionPattern = Pattern.compile(sb.toString());

		// Recherche d'une légende d'une figure : \[[^\]]*\]\{[^\}]*\}

		this.markdownSearchPattern = Pattern.compile("\\[[^\\]]*\\]\\{[^\\}]*\\}");

		// Extension Markdown: \[(?<userText>.*)\]\{(\.(?<htmlTag>[a-zA-Z]*))?(#(?<classAttribute>[^$]*))?(\$(?<styleAttribute>[^}]*))?\}

		sb = new StringBuilder();
		sb.append(OutilsBase.escapeRegExpMetaChars(START_MARKDOWN_DELIMITER));
		sb.append(getGroupOption(MARKDOWN_USER_TEXT_GROUP_NAME, ".", false));
		sb.append(OutilsBase.escapeRegExpMetaChars(SPLIT_MARKDOWN_DELIMITER));
		sb.append(getMarkerGroup(MARKDOWN_HTML_ATTRIBUTE_MARKER, MARKDOWN_HTML_TAG_GROUP_NAME, "[a-zA-Z]", true));
		sb.append(getMarkerGroup(MARKDOWN_CLASS_ATTRIBUTE_MARKER, MARKDOWN_CLASS_ATTRIBUTE_GROUP_NAME, "[^" + MARKDOWN_STYLE_ATTRIBUTE_MARKER + "]", true));
		sb.append(getMarkerGroup(MARKDOWN_STYLE_ATTRIBUTE_MARKER, MARKDOWN_STYLE_ATTRIBUTE_GROUP_NAME, "[^" + END_MARKDOWN_DELIMITER + "]", true));
		sb.append(OutilsBase.escapeRegExpMetaChars(END_MARKDOWN_DELIMITER));

		this.markdownPattern = Pattern.compile(sb.toString());
	}

	/**
	 * Extrait le texte d'un groupe
	 * @param groupName Le nom du groupe
	 * @param text Le text à recherche
	 * @return le texte du groupe
	 */
	protected String getGroup(String groupName, String text) {
		StringBuilder sb = new StringBuilder();
		sb.append("(?<");
		sb.append(groupName);
		sb.append(">");
		sb.append(text);
		sb.append(')');

		return sb.toString();
	}

	/**
	 * Extrait le texte d'un groupe avec option
	 * @param groupName Le nom du groupe
	 * @param text Le text à recherche
	 * @param optional Indicateur de groupe optionel
	 * @return le texte du groupe
	 */
	protected String getGroupOption(String groupName, String text, boolean optional) {
		return getGroupOption(groupName, text, true, optional);
	}

	/**
	 * Extrait le texte d'un groupe avec option
	 * @param groupName Le nom du groupe
	 * @param text Le text à recherche
	 * @param wildcard Indicateur de texte optionel
	 * @param optional Indicateur de groupe optionel
	 * @return le texte du groupe
	 */
	protected String getGroupOption(String groupName, String text, boolean wildcard, boolean optional) {
		StringBuilder sb = new StringBuilder();
		sb.append("(?<");
		sb.append(groupName);
		sb.append(">");
		sb.append(text);

		if (wildcard) {
			sb.append('*');
		}

		sb.append(optional ? "))?" : CLOSE_PARENTHESE_DELIMITER);

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
	protected String getMarkerGroup(String marker, String groupName, String text, boolean optional) {
		return getMarkerGroup(marker, groupName, text, true, optional);
	}

	/**
	 * Extrait le texte d'un groupe avec marqueur
	 * @param marker Le marqueur du groupe
	 * @param groupName Le nom du groupe
	 * @param text Le text à recherche
	 * @param wildcard Indicateur de texte optionel
	 * @param optional Indicateur de groupe optionel
	 * @return le texte du groupe
	 */
	protected String getMarkerGroup(String marker, String groupName, String text, boolean wildcard, boolean optional) {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		sb.append(OutilsBase.escapeRegExpMetaChars(marker));
		sb.append(getGroupOption(groupName, text, wildcard, optional));

		return sb.toString();
	}

	/**
	 * Effectue la désindentation d'un texte
	 * @param value Le texte à désindenter
	 * @param dedentValue Le nombre de caratères de désindentation
	 * @return le texte désindenté
	 */
	protected String doDedentValue(String value, int dedentValue) {
		if (OutilsBase.isEmpty(value)) {
			return value;
		}

		int end = OutilsBase.max(0, OutilsBase.min(dedentValue, value.length()));

		return value.substring(0, end).replace(" ", "") + value.substring(end);
	}

	/**
	 * Effectue l'importation d'un fichier lors de la substitution d'une ligne donnée
	 * @param line La ligne à substituer
	 * @return la ligne substituée
	 * @throws Exception en cas d'erreur...
	 */
	protected String produceIncludeFile(String line) throws Exception {
		if (OutilsBase.isEmpty(line)) {
			return line;
		}

		List<PartData> partsList = new ArrayList<>();

		Matcher search = includeSearchPattern.matcher(line);

		while (search.find()) {
			partsList.add(new PartData(search));
		}

		for (PartData part : partsList) {
			String input = part.getInput();

			if (!OutilsBase.isEmpty(input)) {
				String inputError = " -> " + input;

				String includeValue = null;
				Integer startLineValue = null;
				Integer endLineValue = null;
				Integer dedentValue = null;
				String indentValue = null;

				Matcher matcher = includePattern.matcher(input.substring(1, input.length() - 1));

				while (matcher.find()) {
					String group = matcher.group();

					String errorMessage = group + inputError;

					if (matcher.group(INCLUDE_INCLUDE_KEYWORD_GROUP_NAME) != null) {
						String value = matcher.group(INCLUDE_INCLUDE_VALUE_GROUP_NAME);

						if ((includeValue != null) || OutilsBase.isEmpty(value)) {
							throw new InvalidParameterException(errorMessage);
						}

						includeValue = value;

						input = input.replace(group, "").trim();
						continue;
					}

					if (matcher.group(INCLUDE_START_LINE_VALUE_GROUP_NAME) != null) {
						String value = matcher.group(INCLUDE_START_LINE_VALUE_GROUP_NAME);

						if ((startLineValue != null) || OutilsBase.isEmpty(value)) {
							throw new InvalidParameterException(errorMessage);
						}

						startLineValue = Integer.valueOf(value);

						input = input.replace(group, "").trim();
						continue;
					}

					if (matcher.group(INCLUDE_END_LINE_KEYWORD_GROUP_NAME) != null) {
						String value = matcher.group(INCLUDE_END_LINE_VALUE_GROUP_NAME);

						if ((endLineValue != null) || OutilsBase.isEmpty(value)) {
							throw new InvalidParameterException(errorMessage);
						}

						endLineValue = Integer.valueOf(value);

						input = input.replace(group, "").trim();
						continue;
					}

					if (matcher.group(INCLUDE_DEDENT_KEYWORD_GROUP_NAME) != null) {
						String value = matcher.group(INCLUDE_DEDENT_VALUE_GROUP_NAME);

						if ((dedentValue != null) || OutilsBase.isEmpty(value)) {
							throw new InvalidParameterException(errorMessage);
						}

						dedentValue = Integer.valueOf(value);

						input = input.replace(group, "").trim();
						continue;
					}

					if (matcher.group(INCLUDE_INDENT_KEYWORD_GROUP_NAME) != null) {
						String value = matcher.group(INCLUDE_INDENT_VALUE_GROUP_NAME);

						if ((indentValue != null) || OutilsBase.isEmpty(value)) {
							throw new InvalidParameterException(errorMessage);
						}

						StringBuilder sb = new StringBuilder();

						for (int i = 0; i < Integer.parseInt(value); i++) {
							sb.append(' ');
						}

						indentValue = sb.toString();

						input = input.replace(group, "").trim();
						continue;
					}
				}

				if (includeValue != null) {
					StringBuilder sb = new StringBuilder();

					if (!OutilsBase.isEmpty(input)) {
						sb.append(OPEN_BRACE_DELIMITER);
						sb.append(input);
						sb.append(CLOSE_BRACE_DELIMITER);
					}

					List<String> includeLines = OutilsCommun.loadListFromFile(includeValue);
					List<String> outputLines = new ArrayList<>();

					int startIndex = OutilsBase.min(includeLines.size(), OutilsBase.max(1, (startLineValue != null) ? startLineValue.intValue() : 1)) - 1;
					int endIndex = OutilsBase.min(includeLines.size(), OutilsBase.min(Integer.MAX_VALUE, (endLineValue != null) ? endLineValue.intValue() : Integer.MAX_VALUE)) - 1;

					for (int index = startIndex; index <= endIndex; index++) {
						String output = includeLines.get(index).replace("\t", "    ");

						if (dedentValue != null) {
							output = doDedentValue(output, dedentValue.intValue());
						}

						if (indentValue != null) {
							output = indentValue + output;
						}

						outputLines.add(output);
					}

					if (!outputLines.isEmpty()) {
						if (part.getStart() > 0) {
							sb.append(OutilsCommun.LINE_SEPARATOR);
						}

						sb.append(OutilsCommun.toCRLFList(outputLines, false));
					}

					part.setOutput(sb.toString());
				} else {
					part.setOutput(input);
				}
			}
		}

		return getLine(line, partsList);
	}

	/**
	 * Effectue la convertion de la légende d'une figure (image) en html lors de la substitution d'une ligne donnée
	 * @param line La ligne à substituer
	 * @return la ligne substituée
	 */
	protected String produceFigureCaption(String line) {
		if (OutilsBase.isEmpty(line)) {
			return line;
		}

		List<PartData> partsList = new ArrayList<>();

		Matcher search = figureCaptionSearchPattern.matcher(line);

		while (search.find()) {
			partsList.add(new PartData(search));
		}

		for (PartData part : partsList) {
			String input = part.getInput();

			Matcher matcher = figureCaptionPattern.matcher(input);

			if (matcher.matches()) {
				StringBuilder sb = new StringBuilder();

				String altText = OutilsBase.asString(matcher.group(FIGURE_CAPTION_ALT_TEXT_GROUP_NAME));
				String position = OutilsBase.asString(matcher.group(FIGURE_CAPTION_POSITION_GROUP_NAME)).trim();
				String classAttribute = OutilsBase.asString(matcher.group(FIGURE_CAPTION_CLASS_ATTRIBUTE_GROUP_NAME)).trim();
				String styleAttribute = OutilsBase.asString(matcher.group(FIGURE_CAPTION_STYLE_ATTRIBUTE_GROUP_NAME)).trim();
				String imageURL = OutilsBase.asString(matcher.group(FIGURE_CAPTION_IMAGE_URL_GROUP_NAME)).trim();
				String title = OutilsBase.asString(matcher.group(FIGURE_CAPTION_TITLE_GROUP_NAME)).trim();

				if (OutilsBase.isEmpty(position)) {
					position = "bottom";
				}

				if (!OutilsBase.isEmpty(imageURL)) {
					StringBuilder img = new StringBuilder("<img src=\"");
					img.append(imageURL);
					img.append('"');

					if (!OutilsBase.isEmpty(title)) {
						img.append(" title=\"");
						img.append(title);
						img.append('"');
					}

					if (!OutilsBase.isEmpty(altText)) {
						img.append(" alt=\"");
						img.append(OutilsBase.markdownToPlainText(altText));
						img.append('"');
					}

					img.append('>');

					StringBuilder figcaption = new StringBuilder("<figcaption class=\"");
					figcaption.append(position);

					if (!OutilsBase.isEmpty(classAttribute)) {
						figcaption.append(' ');
						figcaption.append(classAttribute);
					}

					figcaption.append('"');

					if (!OutilsBase.isEmpty(styleAttribute)) {
						figcaption.append(" style=\"");
						figcaption.append(styleAttribute);
						figcaption.append('"');
					}

					figcaption.append('>');
					figcaption.append(OutilsBase.markdownToHTML(altText));
					figcaption.append("</figcaption>");

					boolean top = OutilsBase.areEquals(position, "top");
					boolean bottom = OutilsBase.areEquals(position, "bottom");

					sb.append("<figure>");

					if (top) {
						sb.append(figcaption.toString());
					}

					sb.append(img.toString());

					if (bottom) {
						sb.append(figcaption.toString());
					}

					sb.append("</figure>");

				} else {
					sb.append(input);
				}

				part.setOutput(sb.toString());
			}
		}

		return getLine(line, partsList);
	}

	/**
	 * Effectue la convertion d'une extension markdown en html lors de la substitution d'une ligne donnée
	 * @param line La ligne à substituer
	 * @return la ligne substituée
	 */
	protected String produceMarkdownExtension(String line) {
		if (OutilsBase.isEmpty(line)) {
			return line;
		}

		List<PartData> partsList = new ArrayList<>();

		Matcher search = markdownSearchPattern.matcher(line);

		while (search.find()) {
			partsList.add(new PartData(search));
		}

		for (PartData part : partsList) {
			String input = part.getInput();

			Matcher matcher = markdownPattern.matcher(input);

			if (matcher.matches()) {
				StringBuilder sb = new StringBuilder();

				String userText = OutilsBase.asString(matcher.group(MARKDOWN_USER_TEXT_GROUP_NAME));
				String htmlTag = OutilsBase.asString(matcher.group(MARKDOWN_HTML_TAG_GROUP_NAME)).trim();
				String classAttribute = OutilsBase.asString(matcher.group(MARKDOWN_CLASS_ATTRIBUTE_GROUP_NAME)).trim();
				String styleAttribute = OutilsBase.asString(matcher.group(MARKDOWN_STYLE_ATTRIBUTE_GROUP_NAME)).trim();

				if (!OutilsBase.isEmpty(userText)) {
					if (OutilsBase.isEmpty(htmlTag) && OutilsBase.isEmpty(classAttribute) && OutilsBase.isEmpty(styleAttribute)) {
						sb.append(userText);
					} else {
						String tag = "span";

						if (!OutilsBase.isEmpty(htmlTag)) {
							if (MARKDOWN_HTML_TAGS_DICT.containsKey(htmlTag)) {
								String value = MARKDOWN_HTML_TAGS_DICT.get(htmlTag);

								if (OutilsBase.areEquals(value, MARKDOWN_CLASS_ATTRIBUTE_MARKER)) {
									classAttribute = (htmlTag + " " + classAttribute).trim();
								} else {
									tag = value;
								}
							} else {
								tag = null;
							}
						}

						if (!OutilsBase.isEmpty(tag)) {
							boolean paragraph = (input.length() == line.length());

							if (paragraph) {
								sb.append("<p>");
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
							sb.append(OutilsBase.markdownToHTML(userText));
							sb.append("</");
							sb.append(tag);
							sb.append('>');

							if (paragraph) {
								sb.append("</p>");
							}
						} else {
							sb.append(input);
						}
					}
				}

				part.setOutput(sb.toString());
			}
		}

		return getLine(line, partsList);
	}

	/**
	 * Extrait le contenu de la ligne depuis une liste de données substituées
	 * @param line La ligne originale
	 * @param partsList La liste des substitutions
	 * @return la nouvelle ligne substituée
	 */
	protected String getLine(String line, List<PartData> partsList) {
		if (partsList.isEmpty()) {
			return line;
		}

		int pos = 0;

		StringBuilder sb = new StringBuilder();

		for (PartData part : partsList) {
			sb.append(line.substring(pos, part.getStart()));
			sb.append(part.getOutput());

			pos = part.getEnd();
		}

		if (pos < line.length()) {
			sb.append(line.substring(pos));
		}

		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see outils.abstractions.TemplateProducer#beforeProduceLine(java.lang.String)
	 */
	@Override
	protected String beforeProduceLine(String line) throws Exception {
		return produceIncludeFile(line);
	}

	/*
	 * (non-Javadoc)
	 * @see outils.abstractions.TemplateProducer#afterProduceLine(java.lang.String)
	 */
	@Override
	protected String afterProduceLine(String line) throws Exception {
		return produceMarkdownExtension(produceFigureCaption(line));
	}

}
