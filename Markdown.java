package outils.abstractions;

import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import outils.base.OutilsBase;
import outils.commun.FetchURL;
import outils.commun.OutilsCommun;
import outils.listes.NameValue;
import outils.types.FilesCharsetsTypes;

/**
 * Extensions markdown converties en code html pour MkDocs
 * @author Claude Toupin - 2 juin 2024
 */
public class MarkdownExtensionsTemplateProducer extends HtmlTemplateProducer {
	/** Nom du fichier d'index markdown par défaut **/
	public static final String MARKDOWN_INDEX_FILENAME_DEF = "index" + OutilsCommun.MARKDOWN_EXTENSION;

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

	/** Nom du groupe du mot clé ranges de l'inclusion d'un fichier **/
	private static final String INCLUDE_RANGES_KEYWORD_GROUP_NAME = "ranges";

	/** Nom du groupe de la valeur du mot clé ranges de l'inclusion d'un fichier **/
	private static final String INCLUDE_RANGES_VALUE_GROUP_NAME = "rangesValue";

	/** Nom du groupe du mot clé dedent de l'inclusion d'un fichier **/
	private static final String INCLUDE_DEDENT_KEYWORD_GROUP_NAME = "dedent";

	/** Nom du groupe de la valeur du mot clé dedent de l'inclusion d'un fichier **/
	private static final String INCLUDE_DEDENT_VALUE_GROUP_NAME = "dedentValue";

	/** Nom du groupe du mot clé indent de l'inclusion d'un fichier **/
	private static final String INCLUDE_INDENT_KEYWORD_GROUP_NAME = "indent";

	/** Nom du groupe de la valeur du mot clé indent de l'inclusion d'un fichier **/
	private static final String INCLUDE_INDENT_VALUE_GROUP_NAME = "indentValue";

	// Légende d'une figure (image)

	/** Nom du groupe du texte alternatif (i.e. légende) de la légende d'une figure **/
	private static final String FIGURE_CAPTION_ALT_TEXT_GROUP_NAME = "altText";

	/** Nom du groupe de l'image de la légende d'une figure **/
	private static final String FIGURE_CAPTION_IMAGE_GROUP_NAME = "image";

	/** Nom du groupe de des options de la légende d'une figure **/
	private static final String FIGURE_CAPTION_OPTIONS_GROUP_NAME = "options";

	/** Nom du groupe de l'url de l'image de la légende d'une figure **/
	private static final String FIGURE_CAPTION_IMAGE_URL_GROUP_NAME = "imageURL";

	/** Nom du groupe du texte du titre de l'image de la légende d'une figure **/
	private static final String FIGURE_CAPTION_TITLE_GROUP_NAME = "title";

	/** Nom du groupe de la position des attributs de la légende d'une figure **/
	private static final String FIGURE_CAPTION_POSITION_GROUP_NAME = "position";

	/** Nom du groupe de l'alignement des attributs de la légende d'une figure **/
	private static final String FIGURE_CAPTION_ALIGNMENT_GROUP_NAME = "alignment";

	/** Nom du groupe du texte de l'attribut class de la balise html des attributs de la légende d'une figure **/
	private static final String FIGURE_CAPTION_CLASS_ATTRIBUTE_GROUP_NAME = "classAttribute";

	/** Nom du groupe du texte de l'attribut style de la balise html des attributs de la légende d'une figure **/
	private static final String FIGURE_CAPTION_STYLE_ATTRIBUTE_GROUP_NAME = "styleAttribute";

	// Extension Markdown

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
	private static final Map<String, NameValue> MARKDOWN_HTML_TAGS_DICT = new LinkedHashMap<>();

	// Lien markdown incluant les images markdown

	/** Nom du groupe du texte d'un lien **/
	private static final String LINK_TEXT_GROUP_NAME = "text";

	/** Nom du groupe du chemin d'un lien **/
	private static final String LINK_PATH_GROUP_NAME = "path";

	/** Nom du groupe du chemin d'un lien **/
	private static final String LINK_TITLE_GROUP_NAME = "title";

	static {
		MARKDOWN_HTML_TAGS_DICT.put("abbr", new NameValue("abbr", "Définit une forme abrégée d'un mot ou d'une phrase plus longue.")); // Defines an abbreviated form of a longer word or phrase.
		MARKDOWN_HTML_TAGS_DICT.put("acronym", new NameValue("abbr", "Obsolète. Définit un acronyme. Utilisera plutôt la balise <abbr>.")); // Obsolete Defines an acronym. Will use <abbr> tag instead.
		MARKDOWN_HTML_TAGS_DICT.put("address", new NameValue("address", "Spécifie les informations de contact de l'auteur.")); // Specifies the author's contact information.
		MARKDOWN_HTML_TAGS_DICT.put("b", new NameValue("b", "Affiche le texte en gras.")); // Displays text in a bold style.
		MARKDOWN_HTML_TAGS_DICT.put("bold", new NameValue("b", "Affiche le texte en gras. Utilisera plutôt la balise <b>.")); // Displays text in a bold style. Will use <b> tag instead.
		MARKDOWN_HTML_TAGS_DICT.put("bdi", new NameValue("bdi", "Représente un texte isolé de son environnement pour le formatage bidirectionnel du texte.")); // Represents text that is isolated from its surrounding for the purposes of bidirectional text formatting.
		MARKDOWN_HTML_TAGS_DICT.put("bdo", new NameValue("bdo", "Remplace la direction actuelle du texte.")); // Overrides the current text direction.
		MARKDOWN_HTML_TAGS_DICT.put("big", new NameValue(MARKDOWN_CLASS_ATTRIBUTE_MARKER, "Obsolète. Affiche le texte en grande taille. Utilisera plutôt <span class=\"big\">.")); // Obsolete Displays text in a large size. Will use <span class="big"> instead.
		MARKDOWN_HTML_TAGS_DICT.put("blockquote", new NameValue("blockquote", "Représente une section citée d'une autre source.")); // Represents a section that is quoted from another source.
		MARKDOWN_HTML_TAGS_DICT.put("center", new NameValue(MARKDOWN_CLASS_ATTRIBUTE_MARKER, "Obsolète. Aligne le contenu au centre. Utilisera plutôt <span class=\"center\">.")); // Obsolete Align contents in the center. Will use <span class="center"> instead.
		MARKDOWN_HTML_TAGS_DICT.put("cite", new NameValue("cite", "Indique une citation ou une référence à une autre source.")); // Indicates a citation or reference to another source.
		MARKDOWN_HTML_TAGS_DICT.put("code", new NameValue("code", "Spécifie le texte comme code informatique.")); // Specifies text as computer code.
		MARKDOWN_HTML_TAGS_DICT.put("data", new NameValue("data", "Liaison d'un contenu avec une traduction lisible par machine.")); // Links a piece of content with a machine-readable translation.
		MARKDOWN_HTML_TAGS_DICT.put("del", new NameValue("del", "Représente un texte supprimé du document.")); // Represents text that has been deleted from the document.
		MARKDOWN_HTML_TAGS_DICT.put("dfn", new NameValue("dfn", "Spécifie une définition.")); // Specifies a definition.
		MARKDOWN_HTML_TAGS_DICT.put("em", new NameValue("em", "Définit un texte en italique.")); // Defines emphasized text.
		MARKDOWN_HTML_TAGS_DICT.put("font", new NameValue(MARKDOWN_CLASS_ATTRIBUTE_MARKER, "Obsolète. Définit la police, la couleur et la taille du texte. Utilisera plutôt <span class=\"font\">.")); // Obsolete Defines font, color, and size for text. Will use <span class="font"> instead.
		MARKDOWN_HTML_TAGS_DICT.put("i", new NameValue("i", "Affiche le texte en italique.")); // Displays text in an italic style.
		MARKDOWN_HTML_TAGS_DICT.put("italic", new NameValue("i", "Affiche le texte en italique. Utilisera plutôt la balise <i>.")); // Displays text in an italic style. Will use <i> tag instead.
		MARKDOWN_HTML_TAGS_DICT.put("ins", new NameValue("ins", "Définit un bloc de texte inséré dans un document.")); // Defines a block of text that has been inserted into a document.
		MARKDOWN_HTML_TAGS_DICT.put("kbd", new NameValue("kbd", "Spécifie le texte comme une entrée clavier.")); // Specifies text as keyboard input.
		MARKDOWN_HTML_TAGS_DICT.put("mark", new NameValue("mark", "Représente un texte surligné à des fins de référence.")); // Represents text highlighted for reference purposes.
		MARKDOWN_HTML_TAGS_DICT.put("output", new NameValue("output", "Représente le résultat d'un calcul.")); // Represents the result of a calculation.
		MARKDOWN_HTML_TAGS_DICT.put("pre", new NameValue("pre", "Définit un bloc de texte préformaté.")); // Defines a block of preformatted text.
		MARKDOWN_HTML_TAGS_DICT.put("progress", new NameValue("progress", "Représente l'avancement de l'exécution d'une tâche.")); // Represents the completion progress of a task.
		MARKDOWN_HTML_TAGS_DICT.put("q", new NameValue("q", "Définit une courte citation en ligne.")); // Defines a short inline quotation.
		MARKDOWN_HTML_TAGS_DICT.put("rp", new NameValue("rp", "Fournit des parenthèses de remplacement pour les navigateurs qui ne prennent pas en charge les annotations ruby.")); // Provides fall-back parenthesis for browsers that don't support ruby annotations.
		MARKDOWN_HTML_TAGS_DICT.put("rt", new NameValue("rt", "Définit la prononciation des caractères présentés dans les annotations ruby.")); // Defines the pronunciation of character presented in a ruby annotations.
		MARKDOWN_HTML_TAGS_DICT.put("ruby", new NameValue("ruby", "Représente une annotation ruby.")); // Represents a ruby annotation.
		MARKDOWN_HTML_TAGS_DICT.put("s", new NameValue("s", "Représente des contenus qui ne sont plus exacts ou pertinents.")); // Represents contents that are no longer accurate or no longer relevant.
		MARKDOWN_HTML_TAGS_DICT.put("samp", new NameValue("samp", "Spécifie le texte comme exemple de sortie d'un programme informatique.")); // Specifies text as sample output from a computer program.
		MARKDOWN_HTML_TAGS_DICT.put("small", new NameValue("small", "Affiche le texte en petite taille.")); // Displays text in a smaller size.
		MARKDOWN_HTML_TAGS_DICT.put("smallcaps", new NameValue("$font-variant: small-caps", "Affiche le texte en petites majuscules (de Pandoc). Utilisera plutôt <span style=\"font-variant: small-caps;\">.")); // Displays text in small caps (from Pandoc). Will use <span style="font-variant: small-caps;"> tag instead.
		MARKDOWN_HTML_TAGS_DICT.put("span", new NameValue("span", "Définit une section en ligne sans style.")); // Defines an inline styleless section in a document.
		MARKDOWN_HTML_TAGS_DICT.put("strike", new NameValue("s", "Obsolète. Affiche le texte en barré. Utilisera plutôt la balise <s>.")); // Obsolete Displays text in strikethrough style. Will use <s> tag instead.
		MARKDOWN_HTML_TAGS_DICT.put("strong", new NameValue("strong", "Indique un texte fortement accentué.")); // Indicate strongly emphasized text.
		MARKDOWN_HTML_TAGS_DICT.put("sub", new NameValue("sub", "Définit un texte en indice.")); // Defines subscripted text.
		MARKDOWN_HTML_TAGS_DICT.put("sup", new NameValue("sup", "Définit un texte en exposant.")); // Defines superscripted text.
		MARKDOWN_HTML_TAGS_DICT.put("tt", new NameValue("code", "Obsolète. Affiche le texte en style télétype. Utilisera plutôt <code>.")); // Obsolete Displays text in a teletype style. Will use <code> tag instead.
		MARKDOWN_HTML_TAGS_DICT.put("u", new NameValue("u", "Affiche le texte avec un soulignement.")); // Displays text with an underline.
		MARKDOWN_HTML_TAGS_DICT.put("underline", new NameValue("u", "Affiche le texte avec un soulignement (de Pandoc). Utilisera plutôt la balise <u>.")); // Displays text with an underline (from Pandoc). Will use <u> tag instead.
		MARKDOWN_HTML_TAGS_DICT.put("var", new NameValue("var", "Définit une variable.")); // Defines a variable.
		MARKDOWN_HTML_TAGS_DICT.put("wbr", new NameValue("wbr", "Représente une opportunité de saut de ligne.")); // Represents a line break opportunity.
	}

	/**
	 * Extrait le champ markdownHtmlTagsDict
	 * @return un Map<String,NameValue>
	 */
	public static Map<String, NameValue> getMarkdownHtmlTagsDict() {
		return MARKDOWN_HTML_TAGS_DICT;
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

	/**
	 * Classe des données d'un intervalle de lignes
	 */
	protected class LinesRange {
		/** Début de l'intervalle **/
		private int startLine;

		/** Fin de l'intervalle **/
		private int endLine;

		/**
		 * Constructeur de base
		 * @param startLine Début de l'intervalle
		 * @param endLine Fin de l'intervalle
		 */
		public LinesRange(String startLine, String endLine) {
			this(Integer.valueOf(startLine).intValue(), Integer.valueOf(endLine).intValue());
		}

		/**
		 * Constructeur de base
		 * @param startLine Début de l'intervalle
		 * @param endLine Fin de l'intervalle
		 */
		public LinesRange(int startLine, int endLine) {
			this.startLine = startLine;
			this.endLine = endLine;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "LinesRange [startLine=" + startLine + ", endLine=" + endLine + "]";
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			LinesRange other = (LinesRange) obj;
			if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
				return false;
			return endLine == other.endLine && startLine == other.startLine;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getEnclosingInstance().hashCode();
			result = prime * result + Objects.hash(endLine, startLine);
			return result;
		}

		/**
		 * Détermine si une valeur de ligne donnée est dans l'intervalle de début et de fi de lignes
		 * @param lineNumber La valeur de ligne à vérifier
		 * @return vrai si dans l'intervalle
		 */
		public boolean isInRange(int lineNumber) {
			return (lineNumber >= startLine) && (lineNumber <= endLine);
		}

		/**
		 * Extrait le champ startLine
		 * @return un int
		 */
		public int getStartLine() {
			return startLine;
		}

		/**
		 * Modifie le champ startLine
		 * @param startLine La valeur du champ startLine
		 */
		public void setStartLine(int startLine) {
			this.startLine = startLine;
		}

		/**
		 * Extrait le champ endLine
		 * @return un int
		 */
		public int getEndLine() {
			return endLine;
		}

		/**
		 * Modifie le champ endLine
		 * @param endLine La valeur du champ endLine
		 */
		public void setEndLine(int endLine) {
			this.endLine = endLine;
		}

		private MarkdownExtensionsTemplateProducer getEnclosingInstance() {
			return MarkdownExtensionsTemplateProducer.this;
		}
	}

	/** Patron pour la recherche d'une inclusion d'un fichier **/
	private Pattern includeSearchPattern;

	/** Patron pour extraire les données de l'inclusion d'un fichier **/
	private Pattern includePattern;

	/** Patron pour extraire les données des intervalles de lignes de l'inclusion d'un fichier **/
	private Pattern includeNumberOrRangePattern;

	/** Patron pour extraire les données d'une ligne de l'inclusion d'un fichier **/
	private Pattern includeNumberPattern;

	/** Patron pour extraire les données d'un intervalle de lignes de l'inclusion d'un fichier **/
	private Pattern includeRangePattern;

	/** Patron pour la recherche d'une légende d'une figure **/
	private Pattern figureCaptionSearchPattern;

	/** Patron pour la recherche de l'image d'une légende d'une figure **/
	private Pattern figureCaptionSearchImagePattern;

	/** Patron pour la recherche de la position d'une légende d'une figure **/
	private Pattern figureCaptionSearchPositionPattern;

	/** Patron pour la recherche de l'alignement d'une légende d'une figure **/
	private Pattern figureCaptionSearchAlignmentPattern;

	/** Patron pour la recherche des attributs d'une légende d'une figure **/
	private Pattern figureCaptionSearchAttributesPattern;

	/** Patron pour la recherche d'une extension Markdown **/
	private Pattern markdownSearchPattern;

	/** Patron pour extraire les données de l'extension Markdown **/
	private Pattern markdownPattern;

	/** Patron pour extraire les données d'un lien markdown **/
	private Pattern linkPattern;

	/** Répertoire courant pour les liens avec chemins absolus **/
	private String currentDirectory;

	/** Nom du fichier Markdown **/
	private String markdownFilename;

	/** Indicateur de fichier Markdown d'index **/
	private boolean markdownIndexFilename;

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
		this.currentDirectory = null;
		this.markdownFilename = "";
		this.markdownIndexFilename = false;

		// Recherche de l'inclusion d'un fichier

		this.includeSearchPattern = Pattern.compile("\\{[^\\}]*\\}");

		// Inclusion d'un fichier sans les accolades

		this.includePattern = Pattern.compile("((?<include>include)=\\\"(?<includeValue>[^\\\"]*?)\\\")|((?<startLine>start[-]?[Ll]ine)=\\\"(?<startLineValue>[^\\\"]*?)\\\")|((?<endLine>end[-]?[Ll]ine)=\\\"(?<endLineValue>[^\\\"]*?)\\\")|((?<ranges>range[s]?)=\\\"(?<rangesValue>[^\\\"]*?)\\\")|((?<dedent>dedent)=\\\"(?<dedentValue>[^\\\"]*?)\\\")|((?<indent>indent)=\\\"(?<indentValue>[^\\\"]*?)\\\")");

		this.includeNumberOrRangePattern = Pattern.compile("(\\d+\\s*-\\s*\\d+|\\d+)");

		this.includeNumberPattern = Pattern.compile("(\\d+)");

		this.includeRangePattern = Pattern.compile("(\\d+)\\s*-\\s*(\\d+)");

		// Légende d'une figure

		this.figureCaptionSearchPattern = Pattern.compile("!!\\[(?<altText>[^\\]]*)\\]\\((?<image>[^\\)]+)\\)(\\{(?<options>[^\\}]*)\\})?");

		this.figureCaptionSearchImagePattern = Pattern.compile("(?<imageURL>[^\\s\"]+)(?:\\s\"(?<title>[^\"]*)\")?");

		this.figureCaptionSearchPositionPattern = Pattern.compile("position=(?<position>(top|bottom|none))");

		this.figureCaptionSearchAlignmentPattern = Pattern.compile("alignment=(?<alignment>(left|center|right|none))");

		this.figureCaptionSearchAttributesPattern = Pattern.compile("(#(?<classAttribute>[^$]*?))?(\\$(?<styleAttribute>.*))?$");

		// Recherche d'une extension Markdown

		this.markdownSearchPattern = Pattern.compile("\\[[^\\]]*\\]\\{[^\\}]*\\}");

		// Extension Markdown

		this.markdownPattern = Pattern.compile("\\[(?<userTest>[^\\]]*)\\]\\{(\\.(?<htmlTag>[a-zA-Z]*))?(#(?<classAttribute>[^$]*))?(\\$(?<styleAttribute>[^}]*))?\\}");

		// Lien markdown incluant les images markdown

		this.linkPattern = Pattern.compile("\\[(?<text>[^\\]]*)\\]\\((?<path>[^\\s\\)]*)(?:\\s\"(?<title>[^\"]*)\")?\\)");
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
			String inputSearch = part.getInput();

			if (!OutilsBase.isEmpty(inputSearch)) {
				String inputError = " -> " + inputSearch;

				String includeValue = null;
				Integer startLineValue = null;
				Integer endLineValue = null;
				List<Integer> linesValuesList = new ArrayList<>();
				List<LinesRange> rangesValuesList = new ArrayList<>();
				Integer dedentValue = null;
				String indentValue = null;

				String input = inputSearch.substring(1, inputSearch.length() - 1);

				Matcher matcher = includePattern.matcher(input);

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

					if (matcher.group(INCLUDE_START_LINE_KEYWORD_GROUP_NAME) != null) {
						String value = matcher.group(INCLUDE_START_LINE_VALUE_GROUP_NAME);

						if ((startLineValue != null) || OutilsBase.isEmpty(value)) {
							throw new InvalidParameterException(errorMessage);
						}

						Matcher numberMatcher = includeNumberPattern.matcher(value);

						while (numberMatcher.find()) {
							String number = numberMatcher.group(1);

							if (startLineValue != null) {
								throw new InvalidParameterException(errorMessage);
							}

							value = value.replace(number, "").trim();

							startLineValue = Integer.valueOf(number);
						}

						if (!OutilsBase.isEmpty(value)) {
							throw new InvalidParameterException(errorMessage);
						}

						input = input.replace(group, "").trim();
						continue;
					}

					if (matcher.group(INCLUDE_END_LINE_KEYWORD_GROUP_NAME) != null) {
						String value = matcher.group(INCLUDE_END_LINE_VALUE_GROUP_NAME);

						if ((endLineValue != null) || OutilsBase.isEmpty(value)) {
							throw new InvalidParameterException(errorMessage);
						}

						Matcher numberMatcher = includeNumberPattern.matcher(value);

						while (numberMatcher.find()) {
							String number = numberMatcher.group(1);

							if (endLineValue != null) {
								throw new InvalidParameterException(errorMessage);
							}

							value = value.replace(number, "").trim();

							endLineValue = Integer.valueOf(number);
						}

						if (!OutilsBase.isEmpty(value)) {
							throw new InvalidParameterException(errorMessage);
						}

						input = input.replace(group, "").trim();
						continue;
					}

					if (matcher.group(INCLUDE_RANGES_KEYWORD_GROUP_NAME) != null) {
						String value = matcher.group(INCLUDE_RANGES_VALUE_GROUP_NAME);

						if (OutilsBase.isEmpty(value)) {
							throw new InvalidParameterException(errorMessage);
						}

						Matcher numberOrRangeMatcher = includeNumberOrRangePattern.matcher(value);

						while (numberOrRangeMatcher.find()) {
							String numberOrRange = numberOrRangeMatcher.group(1);

							value = value.replace(numberOrRange, "").trim();

							Matcher rangeMatcher = includeRangePattern.matcher(numberOrRange);

							if (rangeMatcher.matches()) {
								LinesRange linesRange = new LinesRange(rangeMatcher.group(1), rangeMatcher.group(2));

								if (linesRange.getEndLine() < linesRange.getStartLine()) {
									throw new InvalidParameterException(errorMessage);
								}

								if (!rangesValuesList.contains(linesRange)) {
									rangesValuesList.add(linesRange);
								} else {
									throw new InvalidParameterException(errorMessage);
								}
							} else {
								Integer lineValue = Integer.valueOf(numberOrRange);

								if (!linesValuesList.contains(lineValue)) {
									linesValuesList.add(lineValue);
								} else {
									throw new InvalidParameterException(errorMessage);
								}
							}
						}

						if (!OutilsBase.isEmpty(value)) {
							throw new InvalidParameterException(errorMessage);
						}

						input = input.replace(group, "").trim();
						continue;
					}

					if (matcher.group(INCLUDE_DEDENT_KEYWORD_GROUP_NAME) != null) {
						String value = matcher.group(INCLUDE_DEDENT_VALUE_GROUP_NAME);

						if ((dedentValue != null) || OutilsBase.isEmpty(value)) {
							throw new InvalidParameterException(errorMessage);
						}

						Matcher numberMatcher = includeNumberPattern.matcher(value);

						while (numberMatcher.find()) {
							String number = numberMatcher.group(1);

							if (dedentValue != null) {
								throw new InvalidParameterException(errorMessage);
							}

							value = value.replace(number, "").trim();

							dedentValue = Integer.valueOf(number);
						}

						if (!OutilsBase.isEmpty(value)) {
							throw new InvalidParameterException(errorMessage);
						}

						input = input.replace(group, "").trim();
						continue;
					}

					if (matcher.group(INCLUDE_INDENT_KEYWORD_GROUP_NAME) != null) {
						String value = matcher.group(INCLUDE_INDENT_VALUE_GROUP_NAME);

						if ((indentValue != null) || OutilsBase.isEmpty(value)) {
							throw new InvalidParameterException(errorMessage);
						}

						Integer indentCount = null;

						Matcher numberMatcher = includeNumberPattern.matcher(value);

						while (numberMatcher.find()) {
							String number = numberMatcher.group(1);

							if (indentCount != null) {
								throw new InvalidParameterException(errorMessage);
							}

							value = value.replace(number, "").trim();

							indentCount = Integer.valueOf(number);
						}

						if (!OutilsBase.isEmpty(value) || OutilsBase.isEmpty(indentCount)) {
							throw new InvalidParameterException(errorMessage);
						}

						StringBuilder sb = new StringBuilder();

						for (int i = 0; i < indentCount.intValue(); i++) {
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
						sb.append(input);
					}

					List<String> includeLines;

					if (OutilsBase.startsWithIgnoreCase(includeValue, "http://") || OutilsBase.startsWithIgnoreCase(includeValue, "https://")) {
						FetchURL fetchURL = new FetchURL(600000);
						
						byte[] result = fetchURL.getContent(includeValue);

						String resultStr = (result != null) ? new String(result) : "null";
						
						includeLines = OutilsBase.asList(resultStr.split("\\r?\\n"));
						
						if (!fetchURL.isResponseCodeOK()) {
							OutilsCommun.consoleError(includeLines);

							throw new Exception(fetchURL.getLastUrl() + " -> " + fetchURL.getResponseCode());
						}
					} else {
						includeLines = OutilsCommun.loadListFromFile(includeValue);
					}

					List<String> outputLines = new ArrayList<>();

					if ((rangesValuesList.size() == 1) && linesValuesList.isEmpty() && (startLineValue == null) && (endLineValue == null)) {
						LinesRange linesRange = rangesValuesList.get(0);

						startLineValue = Integer.valueOf(linesRange.getStartLine());
						endLineValue = Integer.valueOf(linesRange.getEndLine());

						rangesValuesList.clear();
					}

					int startIndex = OutilsBase.min(includeLines.size(), OutilsBase.max(1, (startLineValue != null) ? startLineValue.intValue() : 1)) - 1;
					int endIndex = OutilsBase.min(includeLines.size(), OutilsBase.min(Integer.MAX_VALUE, (endLineValue != null) ? endLineValue.intValue() : Integer.MAX_VALUE)) - 1;

					boolean hasRange = !linesValuesList.isEmpty() || !rangesValuesList.isEmpty();

					for (int index = startIndex; index <= endIndex; index++) {
						if (hasRange) {
							int lineNumber = index + 1;

							boolean found = false;

							if (!linesValuesList.isEmpty() && linesValuesList.contains(lineNumber)) {
								found = true;
							} else if (!rangesValuesList.isEmpty()) {
								for (LinesRange linesRange : rangesValuesList) {
									if (linesRange.isInRange(lineNumber)) {
										found = true;
										break;
									}
								}
							}

							if (!found) {
								continue;
							}
						}

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

						sb.append(OutilsCommun.toEOL(outputLines, false));
					}

					part.setOutput(sb.toString());
				} else {
					part.setOutput(inputSearch);
				}
			}
		}

		return getLine(line, partsList);
	}

	/**
	 * Effectue la convertion d'un lien markdown (incluant les images markdown) absolu en relatif lors de la substitution d'une ligne donnée
	 * @param line La ligne à substituer
	 * @return la ligne substituée
	 */
	protected String produceLink(String line) {
		if (OutilsBase.isEmpty(line) || OutilsBase.isEmpty(currentDirectory)) {
			return line;
		}

		List<PartData> partsList = new ArrayList<>();

		Matcher search = linkPattern.matcher(line);

		while (search.find()) {
			partsList.add(new PartData(search));
		}

		for (PartData part : partsList) {
			String input = part.getInput();

			Matcher matcher = linkPattern.matcher(input);

			if (matcher.matches()) {
				StringBuilder sb = new StringBuilder();

				String linkText = OutilsBase.asString(matcher.group(LINK_TEXT_GROUP_NAME)).trim();
				String linkPath = OutilsBase.asString(matcher.group(LINK_PATH_GROUP_NAME)).trim().replace(OutilsCommun.WINDOWS_FILE_SEPARATOR, OutilsCommun.UNIX_FILE_SEPARATOR);
				String linkTitle = OutilsBase.asString(matcher.group(LINK_TITLE_GROUP_NAME)).trim();

				if (!OutilsBase.isEmpty(linkText)) {
					if (linkPath.startsWith("@") && !OutilsBase.isEmpty(currentDirectory)) {
						String file = linkPath.substring(1);

						if (!file.startsWith(OutilsCommun.UNIX_FILE_SEPARATOR)) {
							file = OutilsCommun.UNIX_FILE_SEPARATOR + file;
						}

						try {
							linkPath = OutilsCommun.toUnixRelativePath(currentDirectory, file);
						} catch (IllegalArgumentException e) {
							throw new RuntimeException("Invalid linkPath: " + linkPath);
						}
					}

					sb = new StringBuilder("[");
					sb.append(linkText);
					sb.append("](");
					sb.append(linkPath);

					if (!OutilsBase.isEmpty(linkTitle)) {
						sb.append(" \"");
						sb.append(linkTitle);
						sb.append('"');
					}

					sb.append(")");
				}

				part.setOutput(sb.toString());
			} else {
				part.setOutput(input);
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

			Matcher matcher = figureCaptionSearchPattern.matcher(input);

			if (matcher.matches()) {
				String altText = OutilsBase.asString(matcher.group(FIGURE_CAPTION_ALT_TEXT_GROUP_NAME));
				String image = OutilsBase.asString(matcher.group(FIGURE_CAPTION_IMAGE_GROUP_NAME));
				String options = OutilsBase.asString(matcher.group(FIGURE_CAPTION_OPTIONS_GROUP_NAME));

				matcher = figureCaptionSearchImagePattern.matcher(image);

				if (matcher.matches()) {
					String imageURL = OutilsBase.asString(matcher.group(FIGURE_CAPTION_IMAGE_URL_GROUP_NAME)).trim();
					String title = OutilsBase.asString(matcher.group(FIGURE_CAPTION_TITLE_GROUP_NAME)).trim();

					String position = "";
					String alignment = "";
					String classAttribute = "";
					String styleAttribute = "";

					if (!OutilsBase.isEmpty(options)) {
						matcher = figureCaptionSearchPositionPattern.matcher(options);

						if (matcher.find()) {
							position = OutilsBase.asString(matcher.group(FIGURE_CAPTION_POSITION_GROUP_NAME)).trim();
						}

						matcher = figureCaptionSearchAlignmentPattern.matcher(options);

						if (matcher.find()) {
							alignment = OutilsBase.asString(matcher.group(FIGURE_CAPTION_ALIGNMENT_GROUP_NAME)).trim();
						}

						matcher = figureCaptionSearchAttributesPattern.matcher(options);

						if (matcher.find()) {
							classAttribute = OutilsBase.asString(matcher.group(FIGURE_CAPTION_CLASS_ATTRIBUTE_GROUP_NAME)).trim();
							styleAttribute = OutilsBase.asString(matcher.group(FIGURE_CAPTION_STYLE_ATTRIBUTE_GROUP_NAME)).trim();
						}
					}

					StringBuilder sb = new StringBuilder();

					if (OutilsBase.isEmpty(position)) {
						position = "bottom";
					}

					if (OutilsBase.isEmpty(alignment)) {
						alignment = "center";
					}

					boolean noPosition = OutilsBase.areEquals(position, "none") || OutilsBase.isEmpty(altText);
					boolean noAlignment = OutilsBase.areEquals(alignment, "none");

					if (!OutilsBase.isEmpty(imageURL)) {
						if (imageURL.startsWith("@") && !OutilsBase.isEmpty(currentDirectory)) {
							String file = imageURL.substring(1);

							String baseDir = currentDirectory;

							if (!OutilsBase.isEmpty(markdownFilename) && !markdownIndexFilename) {
								baseDir += OutilsCommun.UNIX_FILE_SEPARATOR + markdownFilename.substring(0, markdownFilename.length() - OutilsCommun.MARKDOWN_EXTENSION.length());
							}

							if (!file.startsWith(OutilsCommun.UNIX_FILE_SEPARATOR)) {
								file = OutilsCommun.UNIX_FILE_SEPARATOR + file;
							}

							try {
								imageURL = OutilsCommun.toUnixRelativePath(baseDir, file);
							} catch (IllegalArgumentException e) {
								throw new RuntimeException("Invalid imageURL: " + imageURL);
							}
						}

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

						if (!OutilsBase.isEmpty(classAttribute)) {
							img.append(" class=\"");
							img.append(classAttribute);
							img.append('"');
						}

						if (!OutilsBase.isEmpty(styleAttribute)) {
							img.append(" style=\"");
							img.append(styleAttribute);
							img.append('"');
						}

						img.append('>');

						if (!noAlignment) {
							sb.append("<figure class=\"");
							sb.append(alignment);
							sb.append("\">");

							if (!noPosition) {
								StringBuilder figcaption = new StringBuilder("<figcaption class=\"");
								figcaption.append(alignment);
								figcaption.append(" ");
								figcaption.append(position);
								figcaption.append("\">");
								figcaption.append(OutilsBase.markdownToHTML(altText));
								figcaption.append("</figcaption>");

								boolean top = OutilsBase.areEquals(position, "top");
								boolean bottom = OutilsBase.areEquals(position, "bottom");

								if (top) {
									sb.append(figcaption.toString());
								}

								sb.append(img.toString());

								if (bottom) {
									sb.append(figcaption.toString());
								}
							} else {
								sb.append(img.toString());
							}

							sb.append("</figure>");
						} else {
							sb.append(img.toString());
						}
					} else {
						sb.append(input);
					}

					part.setOutput(sb.toString());
				} else {
					part.setOutput(input);
				}
			} else {
				part.setOutput(input);
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
								String value = MARKDOWN_HTML_TAGS_DICT.get(htmlTag).getName();

								if (OutilsBase.areEquals(value, MARKDOWN_CLASS_ATTRIBUTE_MARKER)) {
									classAttribute = (htmlTag + " " + classAttribute).trim();
								} else if (OutilsBase.startsWith(value, MARKDOWN_STYLE_ATTRIBUTE_MARKER)) {
									styleAttribute = (value.substring(1).trim() + " " + styleAttribute).trim();
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
			} else {
				part.setOutput(input);
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
	 * @see outils.abstractions.TemplateProducer#onTag(java.lang.String)
	 */
	@Override
	protected String onTag(String tag) throws Exception {
		if (getTagsDictionary().containsKey(tag)) {
			return OutilsBase.asString(getTagsDictionary().get(tag));
		}

		return getStartTag() + tag + getEndTag();
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
		return produceMarkdownExtension(produceLink(produceFigureCaption(line)));
	}

	/**
	 * Traitement du modèle
	 * @param currentDirectory Répertoire courant pour les liens avec chemins absolus
	 * @param markdownFilename Nom du fichier Markdown
	 * @param templateFile Nom complet du fichier modèle
	 * @return un List<String> contenant le texte complet basé sur le modèle
	 * @throws Exception en cas d'erreur...
	 */
	public List<String> produce(String currentDirectory, String markdownFilename, String templateFile) throws Exception {
		setCurrentDirectory(currentDirectory);
		setMarkdownFilename(markdownFilename);
		return produce(templateFile);
	}

	/**
	 * Traitement du modèle
	 * @param currentDirectory Répertoire courant pour les liens avec chemins absolus
	 * @param markdownFilename Nom du fichier Markdown
	 * @param templateFile Nom complet du fichier modèle
	 * @param charsetType Type de jeu de caractères
	 * @return un List<String> contenant le texte complet basé sur le modèle
	 * @throws Exception en cas d'erreur...
	 */
	public List<String> produce(String currentDirectory, String markdownFilename, String templateFile, FilesCharsetsTypes charsetType) throws Exception {
		setCurrentDirectory(currentDirectory);
		setMarkdownFilename(markdownFilename);
		return produce(templateFile, charsetType);
	}

	/**
	 * Traitement du modèle
	 * @param currentDirectory Répertoire courant pour les liens avec chemins absolus
	 * @param markdownFilename Nom du fichier Markdown
	 * @param inputStream Flux du fichier modèle
	 * @return un List<String> contenant le texte complet basé sur le modèle
	 * @throws Exception en cas d'erreur...
	 */
	public List<String> produce(String currentDirectory, String markdownFilename, InputStream inputStream) throws Exception {
		setCurrentDirectory(currentDirectory);
		setMarkdownFilename(markdownFilename);
		return produce(inputStream);
	}

	/**
	 * Traitement du modèle
	 * @param currentDirectory Répertoire courant pour les liens avec chemins absolus
	 * @param markdownFilename Nom du fichier Markdown
	 * @param inputStream Flux du fichier modèle
	 * @param charsetType Type de jeu de caractères
	 * @return un List<String> contenant le texte complet basé sur le modèle
	 * @throws Exception en cas d'erreur...
	 */
	public List<String> produce(String currentDirectory, String markdownFilename, InputStream inputStream, FilesCharsetsTypes charsetType) throws Exception {
		setCurrentDirectory(currentDirectory);
		setMarkdownFilename(markdownFilename);
		return produce(inputStream, charsetType);
	}

	/**
	 * Traitement du modèle
	 * @param currentDirectory Répertoire courant pour les liens avec chemins absolus
	 * @param markdownFilename Nom du fichier Markdown
	 * @param template Un List<String> contenant un modèle
	 * @return un List<String> contenant le texte complet basé sur le modèle
	 * @throws Exception en cas d'erreur...
	 */
	public List<String> produce(String currentDirectory, String markdownFilename, List<String> template) throws Exception {
		setCurrentDirectory(currentDirectory);
		setMarkdownFilename(markdownFilename);
		return produce(template);
	}

	/**
	 * Traitement du modèle
	 * @param currentDirectory Répertoire courant pour les liens avec chemins absolus
	 * @param markdownFilename Nom du fichier Markdown
	 * @return un List<String> contenant le texte complet basé sur le modèle
	 * @throws Exception en cas d'erreur...
	 */
	public List<String> produceCurrentDirectoryMarkdownFile(String currentDirectory, String markdownFilename) throws Exception {
		setCurrentDirectory(currentDirectory);
		setMarkdownFilename(markdownFilename);
		return produce();
	}

	/**
	 * Traitement d'une simple ligne de texte donnée
	 * @param currentDirectory Répertoire courant pour les liens avec chemins absolus
	 * @param markdownFilename Nom du fichier Markdown
	 * @param line La ligne de texte à traiter
	 * @return la ligne de texte traitée
	 * @throws Exception en cas d'erreur...
	 */
	public String produceSingleLine(String currentDirectory, String markdownFilename, String line) throws Exception {
		setCurrentDirectory(currentDirectory);
		setMarkdownFilename(markdownFilename);
		return produceSingleLine(line);
	}

	/**
	 * Extrait le champ currentDirectory
	 * @return un String
	 */
	public String getCurrentDirectory() {
		return currentDirectory;
	}

	/**
	 * Modifie le champ currentDirectory
	 * @param currentDirectory La valeur du champ currentDirectory
	 */
	public void setCurrentDirectory(String currentDirectory) {
		this.currentDirectory = !OutilsBase.isEmpty(currentDirectory) ? currentDirectory.replace(OutilsCommun.WINDOWS_FILE_SEPARATOR, OutilsCommun.UNIX_FILE_SEPARATOR) : OutilsCommun.UNIX_FILE_SEPARATOR;
	}

	/**
	 * Extrait le champ markdownFilename
	 * @return un String
	 */
	public String getMarkdownFilename() {
		return markdownFilename;
	}

	/**
	 * Modifie le champ markdownFilename
	 * @param markdownFilename La valeur du champ markdownFilename
	 */
	public void setMarkdownFilename(String markdownFilename) {
		this.markdownFilename = OutilsBase.asString(markdownFilename);
		this.markdownIndexFilename = OutilsBase.areEquals(markdownFilename, MARKDOWN_INDEX_FILENAME_DEF);
	}

	/**
	 * Extrait le champ markdownIndexFilename
	 * @return un boolean
	 */
	public boolean isMarkdownIndexFilename() {
		return markdownIndexFilename;
	}

}
