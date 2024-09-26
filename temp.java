package automated.tests.helpers.abstractions;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import automated.tests.annotations.SkipTesting;
import automated.tests.helpers.base.BasicsHelper;
import automated.tests.helpers.common.CommonHelper;
import automated.tests.helpers.types.FilesCharsetsTypes;

/**
 * Produit un List<String> basé sur un fichier modèle
 */
public abstract class TemplateProducerBase {
	/** Début de balise à substituer **/
	private String startTag;

	/** Fin de balise à substituer **/
	private String endTag;

	/** Balise à substituer **/
	private String tag;

	/** Répertoire de base **/
	private String baseDir;

	/** Contenu du modèle **/
	private List<String> template;

	/**
	 * Constructeur de base
	 */
	protected TemplateProducerBase() {
		this(null, new ArrayList<>());
	}

	/**
	 * Constructeur de base
	 * @param baseDir Répertoire de base
	 */
	protected TemplateProducerBase(String baseDir) {
		this(baseDir, new ArrayList<>());
	}

	/**
	 * Constructeur de base
	 * @param template Contenu du modèle
	 */
	protected TemplateProducerBase(List<String> template) {
		this(null, template);
	}

	/**
	 * Constructeur de base
	 * @param baseDir Répertoire de base
	 * @param template Contenu du modèle
	 */
	protected TemplateProducerBase(String baseDir, List<String> template) {
		this.startTag = "<#";
		this.endTag = ">";
		this.tag = "$TAG_";
		this.baseDir = baseDir;
		this.template = template;
	}

	/**
	 * Constructeur de base
	 * @param baseDir Répertoire de base
	 * @param templateFiles Liste de noms complets de fichiers modèles
	 * @throws Exception en cas d'erreur...
	 */
	protected TemplateProducerBase(String baseDir, String... templateFiles) throws Exception {
		this(baseDir);

		if (templateFiles != null) {
			for (String templateFile : templateFiles) {
				this.template.addAll(CommonHelper.loadListFromFile(CommonHelper.getFullname(this.baseDir, templateFile)));
			}
		}
	}

	/**
	 * Constructeur de base
	 * @param baseDir Répertoire de base
	 * @param templateFile Nom complet du fichier modèle
	 * @param charsetType Type de jeu de caractères
	 * @throws Exception en cas d'erreur...
	 */
	protected TemplateProducerBase(String baseDir, String templateFile, FilesCharsetsTypes charsetType) throws Exception {
		this(baseDir, CommonHelper.loadListFromFile(CommonHelper.getFullname(baseDir, templateFile), charsetType));
	}

	/**
	 * Constructeur de base
	 * @param baseDir Répertoire de base
	 * @param charsetType Type de jeu de caractères
	 * @param templateFiles Liste de noms complets de fichiers modèles
	 * @throws Exception en cas d'erreur...
	 */
	protected TemplateProducerBase(String baseDir, FilesCharsetsTypes charsetType, String... templateFiles) throws Exception {
		this(baseDir);

		if (templateFiles != null) {
			for (String templateFile : templateFiles) {
				this.template.addAll(CommonHelper.loadListFromFile(CommonHelper.getFullname(this.baseDir, templateFile), charsetType));
			}
		}
	}

	/**
	 * Constructeur de base
	 * @param inputStream Liste de flux de données de fichiers modèles
	 * @throws Exception en cas d'erreur...
	 */
	protected TemplateProducerBase(InputStream... inputStreams) throws Exception {
		this();

		if (inputStreams != null) {
			for (InputStream inputStream : inputStreams) {
				this.template.addAll(CommonHelper.loadListFromFile(inputStream));
			}
		}
	}

	/**
	 * Constructeur de base
	 * @param inputStream Flux de données du fichier modèle
	 * @param charsetType Type de jeu de caractères
	 * @throws Exception en cas d'erreur...
	 */
	protected TemplateProducerBase(InputStream inputStream, FilesCharsetsTypes charsetType) throws Exception {
		this(null, CommonHelper.loadListFromFile(inputStream, charsetType));
	}

	/**
	 * Constructeur de base
	 * @param charsetType Type de jeu de caractères
	 * @param inputStreams Liste de flux de données de fichiers modèles
	 * @throws Exception en cas d'erreur...
	 */
	protected TemplateProducerBase(FilesCharsetsTypes charsetType, InputStream... inputStreams) throws Exception {
		this();

		if (inputStreams != null) {
			for (InputStream inputStream : inputStreams) {
				this.template.addAll(CommonHelper.loadListFromFile(inputStream, charsetType));
			}
		}
	}

	/**
	 * Pré-traitement avant le traitement de la ligne du modèle
	 * @param line Ligne du modèle
	 * @return un String
	 * @throws Exception en cas d'erreur...
	 */
	protected abstract String beforeProduceLine(String line) throws Exception;

	/**
	 * Post-traitement après le traitement de la ligne du modèle
	 * @param line Ligne du modèle
	 * @return un String
	 * @throws Exception en cas d'erreur...
	 */
	protected abstract String afterProduceLine(String line) throws Exception;

	/**
	 * Traitement d'un tag
	 * @param tag Le nom du tag
	 * @return un string avec la valeur de substitution
	 * @throws Exception en cas d'erreur...
	 */
	protected abstract String onTag(String tag) throws Exception;

	/**
	 * Pré-traitement du modèle
	 * @return un List<String> contenant le texte de pré-traitement
	 * @throws Exception en cas d'erreur...
	 */
	protected abstract List<String> beforeProduce() throws Exception;

	/**
	 * Post-traitement du modèle
	 * @return un List<String> contenant le texte de post-traitement
	 * @throws Exception en cas d'erreur...
	 */
	protected abstract List<String> afterProduce() throws Exception;

	/**
	 * Effectue la substitution d'une ligne donnée
	 * @param line La ligne à substituer
	 * @return la ligne substituée
	 * @throws Exception en cas d'erreur..
	 */
	protected String produceLine(String line) throws Exception {
		int pos = line.indexOf(startTag);

		while (pos != -1) {
			int end = line.indexOf(endTag, pos + startTag.length());

			if (end == -1) {
				break;
			}

			String section = line.substring(pos + startTag.length(), end);

			if (section.indexOf(startTag) != -1) {
				section = produceLine(section + endTag);
				line = line.substring(0, pos + startTag.length()) + section + line.substring(end + endTag.length());
			} else {
				String value = onTag(section);

				if (value == null) {
					value = startTag + section + endTag;
				}

				StringBuilder sb = new StringBuilder(line.substring(0, pos));

				sb.append(value);

				int length = sb.length();

				sb.append(line.substring(end + endTag.length()));

				line = sb.toString();

				pos = line.indexOf(startTag, length);
			}
		}

		pos = line.indexOf(tag);

		while (pos != -1) {
			int end;

			for (end = pos + 1; end < line.length(); end++) {
				if ((line.charAt(end) != '_') && !Character.isAlphabetic(line.charAt(end)) && !Character.isDigit(line.charAt(end))) {
					break;
				}
			}

			String value = onTag(line.substring(pos + tag.length(), end));

			if (value == null) {
				value = line.substring(pos, end);
			}

			StringBuilder sb = new StringBuilder(line.substring(0, pos));

			sb.append(value);

			int length = sb.length();

			sb.append(line.substring(end));

			line = sb.toString();

			pos = line.indexOf(tag, length);
		}

		return line;
	}

	/**
	 * Traitement du modèle
	 * @param templateFile Nom complet du fichier modèle
	 * @return un List<String> contenant le texte complet basé sur le modèle
	 * @throws Exception en cas d'erreur...
	 */
	public List<String> produce(String templateFile) throws Exception {
		return produce(CommonHelper.loadListFromFile(CommonHelper.getFullname(this.baseDir, templateFile)));
	}

	/**
	 * Traitement du modèle
	 * @param templateFile Nom complet du fichier modèle
	 * @param charsetType Type de jeu de caractères
	 * @return un List<String> contenant le texte complet basé sur le modèle
	 * @throws Exception en cas d'erreur...
	 */
	public List<String> produce(String templateFile, FilesCharsetsTypes charsetType) throws Exception {
		return produce(CommonHelper.loadListFromFile(CommonHelper.getFullname(this.baseDir, templateFile), charsetType));
	}

	/**
	 * Traitement du modèle
	 * @param inputStream Flux du fichier modèle
	 * @return un List<String> contenant le texte complet basé sur le modèle
	 * @throws Exception en cas d'erreur...
	 */
	public List<String> produce(InputStream inputStream) throws Exception {
		return produce(CommonHelper.loadListFromFile(inputStream));
	}

	/**
	 * Traitement du modèle
	 * @param inputStream Flux du fichier modèle
	 * @param charsetType Type de jeu de caractères
	 * @return un List<String> contenant le texte complet basé sur le modèle
	 * @throws Exception en cas d'erreur...
	 */
	public List<String> produce(InputStream inputStream, FilesCharsetsTypes charsetType) throws Exception {
		return produce(CommonHelper.loadListFromFile(inputStream, charsetType));
	}

	/**
	 * Traitement du modèle
	 * @param template Un List<String> contenant un modèle
	 * @return un List<String> contenant le texte complet basé sur le modèle
	 * @throws Exception en cas d'erreur...
	 */
	public List<String> produce(List<String> template) throws Exception {
		List<String> result = new ArrayList<>();

		result.addAll(beforeProduce());

		for (int i = 0; i < template.size(); i++) {
			result.add(afterProduceLine(produceLine(beforeProduceLine(template.get(i)))));
		}

		result.addAll(afterProduce());

		return result;
	}

	/**
	 * Traitement du modèle
	 * @return un List<String> contenant le texte complet basé sur le modèle
	 * @throws Exception en cas d'erreur...
	 */
	public List<String> produce() throws Exception {
		return produce(this.template);
	}

	/**
	 * Traitement d'une simple ligne de texte donnée
	 * @param line La ligne de texte à traiter
	 * @return la ligne de texte traitée
	 * @throws Exception en cas d'erreur...
	 */
	public String produceSingleLine(String line) throws Exception {
		if (!BasicsHelper.isEmpty(line)) {
			return afterProduceLine(produceLine(beforeProduceLine(line)));
		}

		return line;
	}

	/**
	 * Extrait le champ startTag
	 * @return un String
	 */
	public String getStartTag() {
		return startTag;
	}

	/**
	 * Modifie le champ startTag
	 * @param startTag La valeur du champ startTag
	 */
	public void setStartTag(String startTag) {
		this.startTag = startTag;
	}

	/**
	 * Extrait le champ endTag
	 * @return un String
	 */
	public String getEndTag() {
		return endTag;
	}

	/**
	 * Modifie le champ endTag
	 * @param endTag La valeur du champ endTag
	 */
	public void setEndTag(String endTag) {
		this.endTag = endTag;
	}

	/**
	 * Extrait le champ tag
	 * @return un String
	 */
	@SkipTesting
	public String getTag() {
		return tag;
	}

	/**
	 * Modifie le champ tag
	 * @param tag La valeur du champ tag
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * Extrait le champ template
	 * @return un List<String>
	 */
	public List<String> getTemplate() {
		return template;
	}

	/**
	 * Modifie le champ template
	 * @param template La valeur du champ template
	 */
	public void setTemplate(List<String> template) {
		this.template = template;
	}

}
