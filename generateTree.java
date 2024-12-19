	/**
	 * Génération d'un arbre de fichiers et sous-dossiers
	 * @param list La liste des lignes de l'arbre
	 * @param level Niveau courant de sous-répertoire
	 * @param indent Indetation pour le niveau courant
	 * @param isLast Indicateur de dernier niveau
	 * @param baseDir Répertoire courant de base
	 * @param subdir Indicateur de sous-répertoire
	 * @param filter Le filtre des noms de fichiers ou de répertoires
	 * @throws Exception en cas d'erreur...
	 */
	protected static final void generateTree(List<String> list, int level, String indent, boolean isLast, File baseDir, final boolean subdir, final TreeContentFilter filter) throws Exception {
		if (indent == null) {
			list.add(baseDir.toString());
			indent = "";
		} else {
			list.add("|");
			list.add(indent + (isLast ? "\\--- " : "+--- ") + baseDir.getName());
			indent += "    ";
		}

		File[] files = baseDir.listFiles((dir, name) -> {
			File f = new File(dir, name);

			if (f.isDirectory()) {
				if (!subdir) {
					return false;
				}
			}

			return (filter != null) ? filter.accept(dir, name) : true;
		});

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				File file = files[i];

				boolean isFileLast = (i == files.length - 1);

				if (file.isFile()) {
					list.add(((isLast && isFileLast) ? "    " : "|   ") + indent + file.getName());
				}
			}

			for (int i = 0; i < files.length; i++) {
				File dir = files[i];

				boolean isDirLast = (i == files.length - 1);

				if (dir.isDirectory()) {
					boolean doSubDir = true;

					if (filter != null) {
						doSubDir = filter.doSubDir(dir, level, isLast, isDirLast);
					}

					if (doSubDir) {
						generateTree(list, level + 1, indent, isDirLast, dir, subdir, filter);
					} else {
						list.add("|");
						list.add(indent + (isLast ? "\\--- " : "+--- ") + dir.getName());
					}
				}
			}
		}
	}

	/**
	 * Génération d'un arbre de fichiers et sous-dossiers
	 * @param baseDir Le répertoire de base
	 * @return La liste des lignes de l'arbre
	 * @throws Exception en cas d'erreur...
	 */
	public static final List<String> generateTree(String baseDir) throws Exception {
		return generateTree(new File(baseDir));
	}

	/**
	 * Génération d'un arbre de fichiers et sous-dossiers
	 * @param baseDir Le répertoire de base
	 * @return La liste des lignes de l'arbre
	 * @throws Exception en cas d'erreur...
	 */
	public static final List<String> generateTree(File baseDir) throws Exception {
		return generateTree(baseDir, null, true);
	}

	/**
	 * Génération d'un arbre de fichiers et sous-dossiers
	 * @param baseDir Le répertoire de base
	 * @param subdir Indicateur de sous-répertoire
	 * @return La liste des lignes de l'arbre
	 * @throws Exception en cas d'erreur...
	 */
	public static final List<String> generateTree(String baseDir, boolean subdir) throws Exception {
		return generateTree(new File(baseDir));
	}

	/**
	 * Génération d'un arbre de fichiers et sous-dossiers
	 * @param baseDir Le répertoire de base
	 * @param subdir Indicateur de sous-répertoire
	 * @return La liste des lignes de l'arbre
	 * @throws Exception en cas d'erreur...
	 */
	public static final List<String> generateTree(File baseDir, boolean subdir) throws Exception {
		return generateTree(baseDir, null, subdir);
	}

	/**
	 * Génération d'un arbre de fichiers et sous-dossiers
	 * @param baseDir Le répertoire de base
	 * @param filter Le filtre des noms de fichiers
	 * @return La liste des lignes de l'arbre
	 * @throws Exception en cas d'erreur...
	 */
	public static final List<String> generateTree(String baseDir, TreeContentFilter filter) throws Exception {
		return generateTree(new File(baseDir), filter);
	}

	/**
	 * Génération d'un arbre de fichiers et sous-dossiers
	 * @param baseDir Le répertoire de base
	 * @param filter Le filtre du contenu d'une arborescence de répertoires
	 * @return La liste des lignes de l'arbre
	 * @throws Exception en cas d'erreur...
	 */
	public static final List<String> generateTree(File baseDir, TreeContentFilter filter) throws Exception {
		return generateTree(baseDir, filter, true);
	}

	/**
	 * Génération d'un arbre de fichiers et sous-dossiers
	 * @param baseDir Le répertoire de base
	 * @param filter Le filtre du contenu d'une arborescence de répertoires
	 * @param subdir Indicateur de sous-répertoire
	 * @return La liste des lignes de l'arbre
	 * @throws Exception en cas d'erreur...
	 */
	public static final List<String> generateTree(String baseDir, TreeContentFilter filter, boolean subdir) throws Exception {
		return generateTree(new File(baseDir), filter, subdir);
	}

	/**
	 * Génération d'un arbre de fichiers et sous-dossiers
	 * @param baseDir Le répertoire de base
	 * @param filter Le filtre du contenu d'une arborescence de répertoires
	 * @param subdir Indicateur de sous-répertoire
	 * @return La liste des lignes de l'arbre
	 * @throws Exception en cas d'erreur...
	 */
	public static final List<String> generateTree(File baseDir, TreeContentFilter filter, boolean subdir) throws Exception {
		List<String> list = new ArrayList<>();

		generateTree(list, 0, null, false, baseDir, subdir, filter);

		return list;
	}

package outils.commun.filters;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Interface pour filtrer le contenu d'une arborescence de répertoires
 */
public interface TreeContentFilter extends FilenameFilter {

    /**
     * Indicateur d'extraire le contenu d'un sous-répertoire
     * @param dir Le sous-répertoire à extraire
     * @param level Niveau courant de sous-répertoire
     * @param isLast Indicateur de dernier niveau
     * @param isDirLast Indicateur de dernier sous-répertoire du niveau
     * @return vrai si on doit extraire le contenu du sous-répertoire
     */
    boolean doSubDir(File dir, int level, boolean isLast, boolean isDirLast);

}
