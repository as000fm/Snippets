package outils.javadoc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.spi.ToolProvider;

import outils.javadoc.data.ClassJavadocData;
import outils.javadoc.doclets.ExtractClassesJavadocDoclet;

/**
 * Classe des méthodes utilitaires pour javadoc jdk 9+ de type final public static
 * @author Claude Toupin - 11 mai 2024
 */
public class OutilsJavadoc {
	/** Nom du paramètre du docletpath **/
	public static final String DOCLET_PATH_PARAM = "-docletpath";

	/** Valeur du paramètre du docletpath **/
	public static final String DOCLET_PATH_VALUE = "outils-javadoc-1.0.jar";

	/** Nom du paramètre du doclet **/
	public static final String DOCLET_PARAM = "-doclet";

	/** Valeur du paramètre du doclet **/
	public static final String DOCLET_VALUE = ExtractClassesJavadocDoclet.class.getName();

	/** Nom du paramètre du sourcepath **/
	public static final String SOURCE_PATH_PARAM = "-sourcepath";

	/** Nom du paramètre du subpackages **/
	public static final String SUB_PACKAGES_PARAM = "-subpackages";

	/** Valeur du paramètre du sourcepath pour le répertoire source courant d'un projet java **/
	public static final String JAVA_SOURCE_PATH_VALUE = "src";

	/** Valeur du paramètre du sourcepath pour le répertoire source courant d'un projet maven **/
	public static final String MAVEN_SOURCE_PATH_VALUE = "src" + File.separator + "main" + File.separator + "java";

	/** Répertoire de base **/
	public static final String BASE_DIR = System.getProperty("user.dir");

	/**
	 * Exécution de javadoc selon les paramètres donnés
	 * @param params Les paramètres de l'exécution
	 * @return le statut de l'exécution (0 -> OK)
	 */
	protected static final int runJavadoc(String... params) {
		ToolProvider javadoc = ToolProvider.findFirst("javadoc").orElseThrow();

		List<String> javadocArgs = new ArrayList<>();
		javadocArgs.add(DOCLET_PATH_PARAM);
		javadocArgs.add(DOCLET_PATH_VALUE);
		javadocArgs.add(DOCLET_PARAM);
		javadocArgs.add(DOCLET_VALUE);

		for (String param : params) {
			javadocArgs.add(param);
		}

		return javadoc.run(System.out, System.err, javadocArgs.toArray(new String[javadocArgs.size()]));
	}

	/**
	 * Extraction des données javadoc d'une classe donnée depuis le répertoire du projet java courant
	 * @param classe Classe java à extraire
	 * @return le statut de l'exécution (0 -> OK)
	 */
	public static final int extractJavaClassJavadoc(Class<?> classe) {
		return extractClassJavadoc(null, false, classe);
	}

	/**
	 * Extraction des données javadoc d'une classe donnée depuis le répertoire du projet maven courant
	 * @param classe Classe java à extraire
	 * @return le statut de l'exécution (0 -> OK)
	 */
	public static final int extractMavenClassJavadoc(Class<?> classe) {
		return extractClassJavadoc(null, true, classe);
	}

	/**
	 * Extraction des données javadoc d'une classe donnée depuis le répertoire du projet courant
	 * @param maven Indicateur de répertoire source maven (true) ou java (false) à extraire
	 * @param classe Classe java à extraire
	 * @return le statut de l'exécution (0 -> OK)
	 */
	public static final int extractClassJavadoc(boolean maven, Class<?> classe) {
		return extractClassJavadoc(null, maven, classe);
	}

	/**
	 * Extraction des données javadoc d'une classe donnée du répertoire de base d'un projet java
	 * @param baseDir Répertoire de base du projet à extraire
	 * @param classe Classe java à extraire
	 * @return le statut de l'exécution (0 -> OK)
	 */
	public static final int extractJavaClassJavadoc(String baseDir, Class<?> classe) {
		return extractClassJavadoc(baseDir, false, classe);
	}

	/**
	 * Extraction des données javadoc d'une classe donnée du répertoire de base d'un projet maven
	 * @param baseDir Répertoire de base du projet à extraire
	 * @param classe Classe java à extraire
	 * @return le statut de l'exécution (0 -> OK)
	 */
	public static final int extractMavenClassJavadoc(String baseDir, Class<?> classe) {
		return extractClassJavadoc(baseDir, true, classe);
	}

	/**
	 * Extraction des données javadoc d'une classe donnée du répertoire de base
	 * @param baseDir Répertoire de base du projet à extraire
	 * @param maven Indicateur de répertoire source maven (true) ou java (false) à extraire
	 * @param classe Classe java à extraire
	 * @return le statut de l'exécution (0 -> OK)
	 */
	public static final int extractClassJavadoc(String baseDir, boolean maven, Class<?> classe) {
		return extractClassesJavadoc(baseDir, maven, classe.getName().replace(".", File.separator) + ".java");
	}

	/**
	 * Extraction des données javadoc des classes depuis le répertoire du projet java courant
	 * @param classes Nom de la classe ou des classes (ex: *.java)
	 * @return le statut de l'exécution (0 -> OK)
	 */
	public static final int extractJavaClassesJavadoc(String classes) {
		return extractClassesJavadoc(null, false, classes);
	}

	/**
	 * Extraction des données javadoc des classes depuis le répertoire du projet maven courant
	 * @param classes Nom de la classe ou des classes (ex: *.java)
	 * @return le statut de l'exécution (0 -> OK)
	 */
	public static final int extractMavenClassesJavadoc(String classes) {
		return extractClassesJavadoc(null, true, classes);
	}

	/**
	 * Extraction des données javadoc des classes depuis le répertoire du projet courant
	 * @param maven Indicateur de répertoire source maven (true) ou java (false) à extraire
	 * @param classes Nom de la classe ou des classes (ex: *.java)
	 * @return le statut de l'exécution (0 -> OK)
	 */
	public static final int extractClassesJavadoc(boolean maven, String classes) {
		return extractClassesJavadoc(null, maven, classes);
	}

	/**
	 * Extraction des données javadoc des classes depuis le répertoire du projet donné
	 * @param baseDir Répertoire de base du projet à extraire
	 * @param maven Indicateur de répertoire source maven (true) ou java (false) à extraire
	 * @param classes Nom de la classe ou des classes (ex: *.java)
	 * @return le statut de l'exécution (0 -> OK)
	 */
	public static final int extractClassesJavadoc(String baseDir, boolean maven, String classes) {
		String sourcepath = (baseDir != null) ? baseDir : BASE_DIR;

		if (!sourcepath.endsWith(File.separator)) {
			sourcepath += File.separator;
		}

		sourcepath += maven ? MAVEN_SOURCE_PATH_VALUE : JAVA_SOURCE_PATH_VALUE;

		String classpath = sourcepath + File.separator + classes;

		return extractClassesJavadoc(sourcepath, classpath);
	}

	/**
	 * Extraction des données javadoc des classes depuis le répertoire du projet donné
	 * @param sourcepath Le chemin complet de base des sources java des classes à extraire
	 * @param classpath Le chemin complet des classes java des classes à extraire
	 * @return le statut de l'exécution (0 -> OK)
	 */
	public static final int extractClassesJavadoc(String sourcepath, String classpath) {
		return runJavadoc(SOURCE_PATH_PARAM, sourcepath, classpath);
	}

	/**
	 * Extraction des données javadoc des classes d'un package donné depuis le répertoire du projet java courant
	 * @param packagename Le nom complet du package des classes java des classes à extraire
	 * @return le statut de l'exécution (0 -> OK)
	 */
	public static final int extractJavaPackageJavadoc(String packagename) {
		return extractPackageJavadoc(null, false, packagename);
	}

	/**
	 * Extraction des données javadoc des classes d'un package donné depuis le répertoire du projet maven courant
	 * @param packagename Le nom complet du package des classes java des classes à extraire
	 * @return le statut de l'exécution (0 -> OK)
	 */
	public static final int extractMavenPackageJavadoc(String packagename) {
		return extractPackageJavadoc(null, true, packagename);
	}

	/**
	 * Extraction des données javadoc des classes d'un package donné depuis le répertoire du projet courant
	 * @param maven Indicateur de répertoire source maven (true) ou java (false) à extraire
	 * @param packagename Le nom complet du package des classes java des classes à extraire
	 * @return le statut de l'exécution (0 -> OK)
	 */
	public static final int extractPackageJavadoc(boolean maven, String packagename) {
		return extractPackageJavadoc(null, maven, packagename);
	}

	/**
	 * Extraction des données javadoc des classes d'un package donné depuis le répertoire du projet java donné
	 * @param baseDir Répertoire de base du projet à extraire
	 * @param packagename Le nom complet du package des classes java des classes à extraire
	 * @return le statut de l'exécution (0 -> OK)
	 */
	public static final int extractJavaPackageJavadoc(String baseDir, String packagename) {
		return extractPackageJavadoc(baseDir, false, packagename);
	}

	/**
	 * Extraction des données javadoc des classes d'un package donné depuis le répertoire du projet maven donné
	 * @param baseDir Répertoire de base du projet à extraire
	 * @param packagename Le nom complet du package des classes java des classes à extraire
	 * @return le statut de l'exécution (0 -> OK)
	 */
	public static final int extractMavenPackageJavadoc(String baseDir, String packagename) {
		return extractPackageJavadoc(baseDir, true, packagename);
	}

	/**
	 * Extraction des données javadoc des classes d'un package donné depuis le répertoire du projet donné
	 * @param baseDir Répertoire de base du projet à extraire
	 * @param maven Indicateur de répertoire source maven (true) ou java (false) à extraire
	 * @param packagename Le nom complet du package des classes java des classes à extraire
	 * @return le statut de l'exécution (0 -> OK)
	 */
	public static final int extractPackageJavadoc(String baseDir, boolean maven, String packagename) {
		String sourcepath = (baseDir != null) ? baseDir : BASE_DIR;

		if (!sourcepath.endsWith(File.separator)) {
			sourcepath += File.separator;
		}

		sourcepath += maven ? MAVEN_SOURCE_PATH_VALUE : JAVA_SOURCE_PATH_VALUE;

		return extractSubPackagesJavadoc(sourcepath, packagename);
	}

	/**
	 * Extraction des données javadoc des classes d'un package donné
	 * @param sourcepath Le chemin complet de base des sources java des classes à extraire
	 * @param packagename Le nom complet du package des classes java des classes à extraire
	 * @return le statut de l'exécution (0 -> OK)
	 */
	public static final int extractSubPackagesJavadoc(String sourcepath, String packagename) {
		return runJavadoc(SOURCE_PATH_PARAM, sourcepath, SUB_PACKAGES_PARAM, packagename);
	}

	/**
	 * Extrait le champ classesJavadocList
	 * @return un List<ClassJavadocData>
	 */
	public static final List<ClassJavadocData> getClassesJavadocList() {
		return ExtractClassesJavadocDoclet.getClassesJavadocList();
	}

	/**
	 * Extrait le champ classesJavadocDict
	 * @return un Map<String,ClassJavadocData>
	 */
	public static final Map<String, ClassJavadocData> getClassesJavadocDict() {
		return ExtractClassesJavadocDoclet.getClassesJavadocDict();
	}
}
