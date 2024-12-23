package outils.commun;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import outils.base.OutilsBase;

/**
 * Extrait l'arborescence des fichiers et répertoires sous forme de texte
 */
public class TreeContent {
	/** Profondeur maximale de l'arborescence par défaut **/
	public static final int MAX_DEPTH_DEF = Integer.MAX_VALUE;

	/** Indicateur d'affichage des répertoires en premier par défaut **/
	public static final boolean DIRECTORIES_FIRST_DEF = OutilsCommun.isWindows();

	/** Espacement du niveau par défaut **/
	public static final String SPACING_LEVEL_DEF = "    ";

	/** Espacement d'un répertoire du niveau par défaut **/
	public static final String DIRECTORY_SPACING_LEVEL_DEF = "│   ";

	/** Branche du niveau par défaut **/
	public static final String BRANCH_LEVEL_DEF = "├── ";

	/** Dernière branche du niveau par défaut **/
	public static final String LAST_BRANCH_LEVEL_DEF = "└── ";

	/** Profondeur maximale de l'arborescence **/
	private int maxDepth;

	/** Indicateur d'affichage des répertoires en premier **/
	private boolean directoriesFirst;

	/** Espacement du niveau **/
	private String spacingLevel;

	/** Espacement d'un répertoire du niveau **/
	private String directorySpacingLevel;

	/** Branche du niveau **/
	private String branchLevel;

	/** Dernière branche du niveau **/
	private String lastBranchLevel;

	/** Nombre total de répertoires **/
	private int directoriesCount;

	/** Nombre total de fichiers **/
	private int filesCount;

	/** Lignes de l'arborescence **/
	private final List<String> lines;

	/**
	 * Constructeur de base
	 */
	public TreeContent() {
		this( //
				MAX_DEPTH_DEF, //
				DIRECTORIES_FIRST_DEF, //
				SPACING_LEVEL_DEF, //
				DIRECTORY_SPACING_LEVEL_DEF, //
				BRANCH_LEVEL_DEF, //
				LAST_BRANCH_LEVEL_DEF //
		);
	}

	/**
	 * Constructeur de base
	 * @param maxDepth Profondeur maximale de l'arborescence
	 */
	public TreeContent(int maxDepth) {
		this( //
				maxDepth, //
				DIRECTORIES_FIRST_DEF, //
				SPACING_LEVEL_DEF, //
				DIRECTORY_SPACING_LEVEL_DEF, //
				BRANCH_LEVEL_DEF, //
				LAST_BRANCH_LEVEL_DEF //
		);
	}

	/**
	 * Constructeur de base
	 * @param directoriesFirst Indicateur d'affichage des répertoires en premier
	 */
	public TreeContent(boolean directoriesFirst) {
		this( //
				MAX_DEPTH_DEF, //
				directoriesFirst, //
				SPACING_LEVEL_DEF, //
				DIRECTORY_SPACING_LEVEL_DEF, //
				BRANCH_LEVEL_DEF, //
				LAST_BRANCH_LEVEL_DEF //
		);
	}

	/**
	 * Constructeur de base
	 * @param maxDepth Profondeur maximale de l'arborescence
	 * @param directoriesFirst Indicateur d'affichage des répertoires en premier
	 */
	public TreeContent(int maxDepth, boolean directoriesFirst) {
		this( //
				maxDepth, //
				directoriesFirst, //
				SPACING_LEVEL_DEF, //
				DIRECTORY_SPACING_LEVEL_DEF, //
				BRANCH_LEVEL_DEF, //
				LAST_BRANCH_LEVEL_DEF //
		);
	}

	/**
	 * Constructeur de base
	 * @param spacingLevel Espacement du niveau
	 * @param directorySpacingLevel Espacement d'un répertoire du niveau
	 * @param branchLevel Branche du niveau Branche du niveau
	 * @param lastBranchLevel Dernière branche du niveau
	 */
	public TreeContent(String spacingLevel, String directorySpacingLevel, String branchLevel, String lastBranchLevel) {
		this( //
				MAX_DEPTH_DEF, //
				DIRECTORIES_FIRST_DEF, //
				spacingLevel, //
				directorySpacingLevel, //
				branchLevel, //
				lastBranchLevel //
		);
	}

	/**
	 * Constructeur de base
	 * @param maxDepth Profondeur maximale de l'arborescence
	 * @param directoriesFirst Indicateur d'affichage des répertoires en premier
	 * @param spacingLevel Espacement du niveau
	 * @param directorySpacingLevel Espacement d'un répertoire du niveau
	 * @param branchLevel Branche du niveau Branche du niveau
	 * @param lastBranchLevel Dernière branche du niveau
	 */
	public TreeContent(int maxDepth, boolean directoriesFirst, String spacingLevel, String directorySpacingLevel, String branchLevel, String lastBranchLevel) {
		this.maxDepth = maxDepth;
		this.directoriesFirst = directoriesFirst;
		this.spacingLevel = spacingLevel;
		this.directorySpacingLevel = directorySpacingLevel;
		this.branchLevel = branchLevel;
		this.lastBranchLevel = lastBranchLevel;
		this.directoriesCount = 0;
		this.filesCount = 0;
		this.lines = new ArrayList<>();
	}

	/**
	 * Ajoute le contenu d'un repertoire
	 * @param base Répertoire de base courant
	 * @param spacing Espacement courant
	 * @param level Niveau courant de l'arborescence
	 * @param filter Filtre de répertoires et fichiers
	 * @param subdir Indicateur de sous-répertoire
	 * @param showFiles Indicateur d'affichage des noms de fichiers
	 */
	protected void addContent(File base, String spacing, int level, FilenameFilter filter, boolean subdir, boolean showFiles) {
		if (base.exists() && (level < maxDepth)) {
			File[] entries = base.listFiles((dir, name) -> {
				File f = new File(dir, name);

				if (f.isDirectory()) {
					if (!subdir) {
						return false;
					}
				} else if (f.isFile()) {
					if (!showFiles) {
						return false;
					}
				}

				if (OutilsCommun.isLinux()) {
					if (".".equals(name) || "..".equals(name)) {
						return false;
					}
				}

				return (filter != null) ? filter.accept(dir, name) : true;
			});

			if (entries != null) {
				Arrays.sort(entries, (f1, f2) -> {
					int compare = f1.getName().compareToIgnoreCase(f2.getName());

					if (directoriesFirst) {
						if (f1.isDirectory() && f2.isFile()) {
							compare = -1;
						} else if (f1.isFile() && f2.isDirectory()) {
							compare = 1;
						}
					}

					return compare;
				});

				int lastIndex = entries.length - 1;

				for (int index = 0; index < entries.length; index++) {
					File entry = entries[index];

					if (entry.isDirectory()) {
						directoriesCount++;
					} else {
						filesCount++;
					}

					if (index == lastIndex) {
						lines.add(spacing + lastBranchLevel + entry.getName());

						if (entry.isDirectory()) {
							addContent(entry, spacing + spacingLevel, level + 1, filter, subdir, showFiles);
						}
					} else {
						lines.add(spacing + branchLevel + entry.getName());

						if (entry.isDirectory()) {
							addContent(entry, spacing + directorySpacingLevel, level + 1, filter, subdir, showFiles);
						}
					}
				}
			}
		}
	}

	/**
	 * Extrait le répertoire validé de base
	 * @param baseDir Répertoire à extraire
	 * @return le répertoire validé
	 */
	protected File validateBaseDir(String baseDir) {
		if (OutilsBase.isEmpty(baseDir)) {
			throw new IllegalArgumentException("Pas de valeur pour baseDir");
		}

		return validateBaseDir(new File(baseDir));
	}

	/**
	 * Extrait le répertoire validé de base
	 * @param baseDir Répertoire à extraire
	 * @return le répertoire validé
	 */
	protected File validateBaseDir(File baseDir) {
		if (baseDir == null) {
			throw new IllegalArgumentException("Pas de valeur pour baseDir");
		} else if (!baseDir.exists()) {
			throw new IllegalArgumentException("Pas de répertoire \"" + baseDir.getAbsolutePath() + "\" pour baseDir");
		} else if (!baseDir.isDirectory()) {
			throw new IllegalArgumentException("N'est pas un répertoire valide \"" + baseDir.getAbsolutePath() + "\" pour baseDir");
		}

		return baseDir;
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param baseDir Répertoire à extraire
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(String baseDir) {
		return extractTreeContent(null, validateBaseDir(baseDir), null, true, true);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param baseDir Répertoire à extraire
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(File baseDir) {
		return extractTreeContent(null, validateBaseDir(baseDir), null, true, true);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param baseDir Répertoire à extraire
	 * @param subdir Indicateur de sous-répertoire
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(String baseDir, boolean subdir) {
		return extractTreeContent(null, validateBaseDir(baseDir), null, subdir, true);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param baseDir Répertoire à extraire
	 * @param subdir Indicateur de sous-répertoire
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(File baseDir, boolean subdir) {
		return extractTreeContent(null, validateBaseDir(baseDir), null, subdir, true);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param baseDir Répertoire à extraire
	 * @param subdir Indicateur de sous-répertoire
	 * @param showFiles Indicateur d'affichage des noms de fichiers
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(String baseDir, boolean subdir, boolean showFiles) {
		return extractTreeContent(null, validateBaseDir(baseDir), null, subdir, showFiles);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param baseDir Répertoire à extraire
	 * @param subdir Indicateur de sous-répertoire
	 * @param showFiles Indicateur d'affichage des noms de fichiers
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(File baseDir, boolean subdir, boolean showFiles) {
		return extractTreeContent(null, validateBaseDir(baseDir), null, subdir, showFiles);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param baseDir Répertoire à extraire
	 * @param filter Filtre de répertoires et fichiers
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(String baseDir, FilenameFilter filter) {
		return extractTreeContent(null, validateBaseDir(baseDir), filter, true, true);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param baseDir Répertoire à extraire
	 * @param filter Filtre de répertoires et fichiers
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(File baseDir, FilenameFilter filter) {
		return extractTreeContent(null, validateBaseDir(baseDir), filter, true, true);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param baseDir Répertoire à extraire
	 * @param filter Filtre de répertoires et fichiers
	 * @param subdir Indicateur de sous-répertoire
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(String baseDir, FilenameFilter filter, boolean subdir) {
		return extractTreeContent(null, validateBaseDir(baseDir), filter, subdir, true);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param baseDir Répertoire à extraire
	 * @param filter Filtre de répertoires et fichiers
	 * @param subdir Indicateur de sous-répertoire
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(File baseDir, FilenameFilter filter, boolean subdir) {
		return extractTreeContent(null, validateBaseDir(baseDir), filter, subdir, true);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param baseDir Répertoire à extraire
	 * @param filter Filtre de répertoires et fichiers
	 * @param subdir Indicateur de sous-répertoire
	 * @param showFiles Indicateur d'affichage des noms de fichiers
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(String baseDir, FilenameFilter filter, boolean subdir, boolean showFiles) {
		return extractTreeContent(null, validateBaseDir(baseDir), filter, subdir, showFiles);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param baseDir Répertoire à extraire
	 * @param filter Filtre de répertoires et fichiers
	 * @param subdir Indicateur de sous-répertoire
	 * @param showFiles Indicateur d'affichage des noms de fichiers
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(File baseDir, FilenameFilter filter, boolean subdir, boolean showFiles) {
		return extractTreeContent(null, validateBaseDir(baseDir), filter, subdir, showFiles);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param title Titre de l'arborescence
	 * @param baseDir Répertoire à extraire
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(String title, String baseDir) {
		return extractTreeContent(title, validateBaseDir(baseDir), null, true, true);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param title Titre de l'arborescence
	 * @param baseDir Répertoire à extraire
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(String title, File baseDir) {
		return extractTreeContent(title, validateBaseDir(baseDir), null, true, true);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param title Titre de l'arborescence
	 * @param baseDir Répertoire à extraire
	 * @param subdir Indicateur de sous-répertoire
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(String title, String baseDir, boolean subdir) {
		return extractTreeContent(title, validateBaseDir(baseDir), null, subdir, true);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param title Titre de l'arborescence
	 * @param baseDir Répertoire à extraire
	 * @param subdir Indicateur de sous-répertoire
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(String title, File baseDir, boolean subdir) {
		return extractTreeContent(title, validateBaseDir(baseDir), null, subdir, true);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param title Titre de l'arborescence
	 * @param baseDir Répertoire à extraire
	 * @param subdir Indicateur de sous-répertoire
	 * @param showFiles Indicateur d'affichage des noms de fichiers
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(String title, String baseDir, boolean subdir, boolean showFiles) {
		return extractTreeContent(title, validateBaseDir(baseDir), null, subdir, showFiles);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param title Titre de l'arborescence
	 * @param baseDir Répertoire à extraire
	 * @param subdir Indicateur de sous-répertoire
	 * @param showFiles Indicateur d'affichage des noms de fichiers
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(String title, File baseDir, boolean subdir, boolean showFiles) {
		return extractTreeContent(title, validateBaseDir(baseDir), null, subdir, showFiles);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param title Titre de l'arborescence
	 * @param baseDir Répertoire à extraire
	 * @param filter Filtre de répertoires et fichiers
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(String title, String baseDir, FilenameFilter filter) {
		return extractTreeContent(title, validateBaseDir(baseDir), filter, true, true);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param title Titre de l'arborescence
	 * @param baseDir Répertoire à extraire
	 * @param filter Filtre de répertoires et fichiers
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(String title, File baseDir, FilenameFilter filter) {
		return extractTreeContent(title, validateBaseDir(baseDir), filter, true, true);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param title Titre de l'arborescence
	 * @param baseDir Répertoire à extraire
	 * @param filter Filtre de répertoires et fichiers
	 * @param subdir Indicateur de sous-répertoire
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(String title, String baseDir, FilenameFilter filter, boolean subdir) {
		return extractTreeContent(title, validateBaseDir(baseDir), filter, subdir, true);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param title Titre de l'arborescence
	 * @param baseDir Répertoire à extraire
	 * @param filter Filtre de répertoires et fichiers
	 * @param subdir Indicateur de sous-répertoire
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(String title, File baseDir, FilenameFilter filter, boolean subdir) {
		return extractTreeContent(title, validateBaseDir(baseDir), filter, subdir, true);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param title Titre de l'arborescence
	 * @param baseDir Répertoire à extraire
	 * @param filter Filtre de répertoires et fichiers
	 * @param subdir Indicateur de sous-répertoire
	 * @param showFiles Indicateur d'affichage des noms de fichiers
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(String title, String baseDir, FilenameFilter filter, boolean subdir, boolean showFiles) {
		return extractTreeContent(title, validateBaseDir(baseDir), filter, subdir, showFiles);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param title Titre de l'arborescence
	 * @param baseDir Répertoire à extraire
	 * @param filter Filtre de répertoires et fichiers
	 * @param subdir Indicateur de sous-répertoire
	 * @param showFiles Indicateur d'affichage des noms de fichiers
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(String title, File baseDir, FilenameFilter filter, boolean subdir, boolean showFiles) {
		baseDir = validateBaseDir(baseDir);

		lines.clear();
		directoriesCount = 0;
		filesCount = 0;

		if (title == null) {
			lines.add(baseDir.getAbsolutePath());
		} else if (!title.isEmpty()) {
			lines.add(title);
		}

		addContent(baseDir, "", 0, filter, subdir, showFiles);

		return lines;
	}

	/**
	 * Extrait le champ maxDepth
	 * @return un int
	 */
	public int getMaxDepth() {
		return maxDepth;
	}

	/**
	 * Modifie le champ maxDepth
	 * @param maxDepth La valeur du champ maxDepth
	 */
	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	/**
	 * Extrait le champ directoriesFirst
	 * @return un boolean
	 */
	public boolean isDirectoriesFirst() {
		return directoriesFirst;
	}

	/**
	 * Modifie le champ directoriesFirst
	 * @param directoriesFirst La valeur du champ directoriesFirst
	 */
	public void setDirectoriesFirst(boolean directoriesFirst) {
		this.directoriesFirst = directoriesFirst;
	}

	/**
	 * Extrait le champ spacingLevel
	 * @return un String
	 */
	public String getSpacingLevel() {
		return spacingLevel;
	}

	/**
	 * Modifie le champ spacingLevel
	 * @param spacingLevel La valeur du champ spacingLevel
	 */
	public void setSpacingLevel(String spacingLevel) {
		this.spacingLevel = spacingLevel;
	}

	/**
	 * Extrait le champ directorySpacingLevel
	 * @return un String
	 */
	public String getDirectorySpacingLevel() {
		return directorySpacingLevel;
	}

	/**
	 * Modifie le champ directorySpacingLevel
	 * @param directorySpacingLevel La valeur du champ directorySpacingLevel
	 */
	public void setDirectorySpacingLevel(String directorySpacingLevel) {
		this.directorySpacingLevel = directorySpacingLevel;
	}

	/**
	 * Extrait le champ branchLevel
	 * @return un String
	 */
	public String getBranchLevel() {
		return branchLevel;
	}

	/**
	 * Modifie le champ branchLevel
	 * @param branchLevel La valeur du champ branchLevel
	 */
	public void setBranchLevel(String branchLevel) {
		this.branchLevel = branchLevel;
	}

	/**
	 * Extrait le champ lastBranchLevel
	 * @return un String
	 */
	public String getLastBranchLevel() {
		return lastBranchLevel;
	}

	/**
	 * Modifie le champ lastBranchLevel
	 * @param lastBranchLevel La valeur du champ lastBranchLevel
	 */
	public void setLastBranchLevel(String lastBranchLevel) {
		this.lastBranchLevel = lastBranchLevel;
	}

	/**
	 * Extrait le champ directoriesCount
	 * @return un int
	 */
	public int getDirectoriesCount() {
		return directoriesCount;
	}

	/**
	 * Extrait le champ filesCount
	 * @return un int
	 */
	public int getFilesCount() {
		return filesCount;
	}

	/**
	 * Extrait le champ lines
	 * @return un List<String>
	 */
	public List<String> getLines() {
		return lines;
	}

}
