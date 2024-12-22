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
		this("    ", "│   ", "├── ", "└── ");
	}

	/**
	 * Constructeur de base
	 * @param spacingLevel Espacement du niveau
	 * @param branchLevel Branche du niveau Branche du niveau
	 * @param lastBranchLevel Dernière branche du niveau
	 */
	public TreeContent(String spacingLevel, String directorySpacingLevel, String branchLevel, String lastBranchLevel) {
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
	 * @param maxDepth Profondeur maximale de l'arborescence
	 * @param directoriesFirst Indicateur d'affichage des répertoires en premier
	 * @param showFiles Indicateur d'affichage des noms de fichiers
	 * @param subdir Indicateur de sous-répertoire
	 */
	protected void addContent(File base, String spacing, int level, FilenameFilter filter, int maxDepth, boolean directoriesFirst, boolean showFiles, boolean subdir) {
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
							addContent(entry, spacing + spacingLevel, level + 1, filter, maxDepth, directoriesFirst, showFiles, subdir);
						}
					} else {
						lines.add(spacing + branchLevel + entry.getName());
						
						
						if (entry.isDirectory()) {
							addContent(entry, spacing + directorySpacingLevel, level + 1, filter, maxDepth, directoriesFirst, showFiles, subdir);
						}
					}
				}
			}
		}
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param baseDir Répertoire à extraire
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(File baseDir) {
		return extractTreeContent(baseDir.getAbsolutePath(), baseDir);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param title Titre de l'arborescence
	 * @param baseDir Répertoire à extraire
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(String title, File baseDir) {
		return extractTreeContent(title, baseDir, Integer.MAX_VALUE);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param baseDir Répertoire à extraire
	 * @param maxDepth Profondeur maximale de l'arborescence
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(File baseDir, int maxDepth) {
		return extractTreeContent(baseDir.getAbsolutePath(), baseDir, maxDepth);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param title Titre de l'arborescence
	 * @param baseDir Répertoire à extraire
	 * @param maxDepth Profondeur maximale de l'arborescence
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(String title, File baseDir, int maxDepth) {
		return extractTreeContent(title, baseDir, maxDepth, OutilsCommun.isWindows());
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param baseDir Répertoire à extraire
	 * @param maxDepth Profondeur maximale de l'arborescence
	 * @param directoriesFirst Indicateur d'affichage des répertoires en premier
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(File baseDir, int maxDepth, boolean directoriesFirst) {
		return extractTreeContent(baseDir.getAbsolutePath(), baseDir, maxDepth, directoriesFirst);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param title Titre de l'arborescence
	 * @param baseDir Répertoire à extraire
	 * @param maxDepth Profondeur maximale de l'arborescence
	 * @param directoriesFirst Indicateur d'affichage des répertoires en premier
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(String title, File baseDir, int maxDepth, boolean directoriesFirst) {
		return extractTreeContent(title, baseDir, maxDepth, directoriesFirst, true);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param baseDir Répertoire à extraire
	 * @param maxDepth Profondeur maximale de l'arborescence
	 * @param directoriesFirst Indicateur d'affichage des répertoires en premier
	 * @param showFiles Indicateur d'affichage des noms de fichiers
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(File baseDir, int maxDepth, boolean directoriesFirst, boolean showFiles) {
		return extractTreeContent(baseDir.getAbsolutePath(), baseDir, maxDepth, directoriesFirst, showFiles);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param title Titre de l'arborescence
	 * @param baseDir Répertoire à extraire
	 * @param maxDepth Profondeur maximale de l'arborescence
	 * @param directoriesFirst Indicateur d'affichage des répertoires en premier
	 * @param showFiles Indicateur d'affichage des noms de fichiers
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(String title, File baseDir, int maxDepth, boolean directoriesFirst, boolean showFiles) {
		return extractTreeContent(title, baseDir, maxDepth, directoriesFirst, showFiles, true);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param baseDir Répertoire à extraire
	 * @param maxDepth Profondeur maximale de l'arborescence
	 * @param directoriesFirst Indicateur d'affichage des répertoires en premier
	 * @param showFiles Indicateur d'affichage des noms de fichiers
	 * @param subdir Indicateur de sous-répertoire
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(File baseDir, int maxDepth, boolean directoriesFirst, boolean showFiles, boolean subdir) {
		return extractTreeContent(baseDir.getAbsolutePath(), baseDir, maxDepth, directoriesFirst, showFiles, subdir);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param title Titre de l'arborescence
	 * @param baseDir Répertoire à extraire
	 * @param maxDepth Profondeur maximale de l'arborescence
	 * @param directoriesFirst Indicateur d'affichage des répertoires en premier
	 * @param showFiles Indicateur d'affichage des noms de fichiers
	 * @param subdir Indicateur de sous-répertoire
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(String title, File baseDir, int maxDepth, boolean directoriesFirst, boolean showFiles, boolean subdir) {
		return extractTreeContent(title, baseDir, null, maxDepth, directoriesFirst, showFiles, subdir);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param baseDir Répertoire à extraire
	 * @param filter Filtre de répertoires et fichiers
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(File baseDir, FilenameFilter filter) {
		return extractTreeContent(baseDir.getAbsolutePath(), baseDir, filter);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param title Titre de l'arborescence
	 * @param baseDir Répertoire à extraire
	 * @param filter Filtre de répertoires et fichiers
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(String title, File baseDir, FilenameFilter filter) {
		return extractTreeContent(title, baseDir, filter, Integer.MAX_VALUE);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param baseDir Répertoire à extraire
	 * @param filter Filtre de répertoires et fichiers
	 * @param maxDepth Profondeur maximale de l'arborescence
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(File baseDir, FilenameFilter filter, int maxDepth) {
		return extractTreeContent(baseDir.getAbsolutePath(), baseDir, filter, maxDepth);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param title Titre de l'arborescence
	 * @param baseDir Répertoire à extraire
	 * @param filter Filtre de répertoires et fichiers
	 * @param maxDepth Profondeur maximale de l'arborescence
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(String title, File baseDir, FilenameFilter filter, int maxDepth) {
		return extractTreeContent(title, baseDir, filter, maxDepth, OutilsCommun.isWindows());
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param baseDir Répertoire à extraire
	 * @param filter Filtre de répertoires et fichiers
	 * @param maxDepth Profondeur maximale de l'arborescence
	 * @param directoriesFirst Indicateur d'affichage des répertoires en premier
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(File baseDir, FilenameFilter filter, int maxDepth, boolean directoriesFirst) {
		return extractTreeContent(baseDir.getAbsolutePath(), baseDir, filter, maxDepth, directoriesFirst);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param title Titre de l'arborescence
	 * @param baseDir Répertoire à extraire
	 * @param filter Filtre de répertoires et fichiers
	 * @param maxDepth Profondeur maximale de l'arborescence
	 * @param directoriesFirst Indicateur d'affichage des répertoires en premier
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(String title, File baseDir, FilenameFilter filter, int maxDepth, boolean directoriesFirst) {
		return extractTreeContent(title, baseDir, filter, maxDepth, directoriesFirst, true);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param baseDir Répertoire à extraire
	 * @param filter Filtre de répertoires et fichiers
	 * @param maxDepth Profondeur maximale de l'arborescence
	 * @param directoriesFirst Indicateur d'affichage des répertoires en premier
	 * @param showFiles Indicateur d'affichage des noms de fichiers
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(File baseDir, FilenameFilter filter, int maxDepth, boolean directoriesFirst, boolean showFiles) {
		return extractTreeContent(baseDir.getAbsolutePath(), baseDir, filter, maxDepth, directoriesFirst, showFiles);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param title Titre de l'arborescence
	 * @param baseDir Répertoire à extraire
	 * @param filter Filtre de répertoires et fichiers
	 * @param maxDepth Profondeur maximale de l'arborescence
	 * @param directoriesFirst Indicateur d'affichage des répertoires en premier
	 * @param showFiles Indicateur d'affichage des noms de fichiers
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(String title, File baseDir, FilenameFilter filter, int maxDepth, boolean directoriesFirst, boolean showFiles) {
		return extractTreeContent(title, baseDir, filter, maxDepth, directoriesFirst, showFiles, true);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param baseDir Répertoire à extraire
	 * @param filter Filtre de répertoires et fichiers
	 * @param maxDepth Profondeur maximale de l'arborescence
	 * @param directoriesFirst Indicateur d'affichage des répertoires en premier
	 * @param showFiles Indicateur d'affichage des noms de fichiers
	 * @param subdir Indicateur de sous-répertoire
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(File baseDir, FilenameFilter filter, int maxDepth, boolean directoriesFirst, boolean showFiles, boolean subdir) {
		return extractTreeContent(baseDir.getAbsolutePath(), baseDir, filter, maxDepth, directoriesFirst, showFiles, subdir);
	}

	/**
	 * Extrait l'arborescence des fichiers et répertoires sous forme de texte pour un répertoire donné
	 * @param title Titre de l'arborescence
	 * @param baseDir Répertoire à extraire
	 * @param filter Filtre de répertoires et fichiers
	 * @param maxDepth Profondeur maximale de l'arborescence
	 * @param directoriesFirst Indicateur d'affichage des répertoires en premier
	 * @param showFiles Indicateur d'affichage des noms de fichiers
	 * @param subdir Indicateur de sous-répertoire
	 * @return les lignes de l'arborescence
	 */
	public List<String> extractTreeContent(String title, File baseDir, FilenameFilter filter, int maxDepth, boolean directoriesFirst, boolean showFiles, boolean subdir) {
		lines.clear();
		directoriesCount = 0;
		filesCount = 0;
		
		if (!OutilsBase.isEmpty(title)) {
			lines.add(title);
		}
		
		addContent(baseDir, "", 0, filter, maxDepth, directoriesFirst, showFiles, subdir);
		
		return lines;
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
