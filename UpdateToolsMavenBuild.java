package tools;

import java.io.File;
import java.io.FilenameFilter;

import outils.commun.OutilsCommun;

/**
 * Mise à jour des projets maven pour Outils
 */
public class UpdateToolsMavenBuild {
	/** Répertoire courant des sources **/
	private static final String SOURCE_DIR = OutilsCommun.getCurrentDirectory();

	/** Répertoire courant du workspace d'Eclipse Maven **/
	private static final String MAVEN_WORKSPACE_DIR = OutilsCommun.getCurrentEclipseWorkspaceDirectory();

	/** Répertoire du project destination Tools (Maven Build) **/
	private static final String OUTILS_MAVEN_DIR = OutilsCommun.getFullname(MAVEN_WORKSPACE_DIR, "Tools (Maven Build)");

	/** Répertoire du project destination Tools Generators (Maven Build) **/
	private static final String OUTILS_GENERATORS_MAVEN_DIR = OutilsCommun.getFullname(MAVEN_WORKSPACE_DIR, "Tools Generators (Maven Build)");

	/** Répertoire de destination maven src/main/java **/
	private static final String SRC_MAIN_JAVA_DIR = OutilsCommun.getFullname("src", "main", "java");

	/** Répertoire de destination maven src/main/resources **/
	private static final String SRC_MAIN_RESOURCES_DIR = OutilsCommun.getFullname("src", "main", "resources");

	/** Répertoire de destination maven src/main/java **/
	// private static final String SRC_TEST_JAVA_DIR = OutilsCommun.getFullname("src", "test", "java");

	/** Répertoire de destination maven src/main/resources **/
	// private static final String SRC_TEST_RESOURCES_DIR = OutilsCommun.getFullname("src", "test", "resources");

	/** Filtre pour les fichiers java **/
	private static final FilenameFilter JAVA_FILTER = (dir, name) -> {
		return new File(dir, name).isDirectory() || name.endsWith(OutilsCommun.JAVA_EXTENSION);
	};

	/** Filtre pour les fichiers resources (i.e. non java) **/
	private static final FilenameFilter RESOURCES_FILTER = (dir, name) -> {
		return new File(dir, name).isDirectory() || !name.endsWith(OutilsCommun.JAVA_EXTENSION);
	};

	/**
	 * Mise à jour des projets des tests unitaires automatisés via maven
	 * @param args Arguments
	 * @throws Exception en cas d'erreur...
	 */
	public static void main(String[] args) throws Exception {
		OutilsCommun.console("Mise à jour de Tools (Maven Build)");

		OutilsCommun.cleanDirectories(OutilsCommun.getFullname(OUTILS_MAVEN_DIR, SRC_MAIN_JAVA_DIR));
		OutilsCommun.copyDirectory(OutilsCommun.getFullname(SOURCE_DIR, "src"), OutilsCommun.getFullname(OUTILS_MAVEN_DIR, SRC_MAIN_JAVA_DIR), JAVA_FILTER);

		OutilsCommun.cleanDirectories(OutilsCommun.getFullname(OUTILS_MAVEN_DIR, SRC_MAIN_RESOURCES_DIR));
		OutilsCommun.copyDirectory(OutilsCommun.getFullname(SOURCE_DIR, "src"), OutilsCommun.getFullname(OUTILS_MAVEN_DIR, SRC_MAIN_RESOURCES_DIR), RESOURCES_FILTER);

		OutilsCommun.console("Mise à jour de Tools Generators (Maven Build)");

		OutilsCommun.cleanDirectories(OutilsCommun.getFullname(OUTILS_GENERATORS_MAVEN_DIR, SRC_MAIN_JAVA_DIR));
		OutilsCommun.copyDirectory(OutilsCommun.getFullname(SOURCE_DIR, "generators"), OutilsCommun.getFullname(OUTILS_GENERATORS_MAVEN_DIR, SRC_MAIN_JAVA_DIR), JAVA_FILTER);

		OutilsCommun.cleanDirectories(OutilsCommun.getFullname(OUTILS_GENERATORS_MAVEN_DIR, SRC_MAIN_RESOURCES_DIR));
		OutilsCommun.copyDirectory(OutilsCommun.getFullname(SOURCE_DIR, "generators"), OutilsCommun.getFullname(OUTILS_GENERATORS_MAVEN_DIR, SRC_MAIN_RESOURCES_DIR), RESOURCES_FILTER);
	}
}
