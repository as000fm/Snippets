package outils.commun;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import outils.abstractions.IRawFileConverter;
import outils.abstractions.RawFileConverter;
import outils.base.OutilsBase;
import outils.listes.NameValue;
import outils.listes.StringData;

/**
 * Classe des méthodes utilitaires de type public static final pour la compression en format zip et gzip
 * @author Claude Toupin - 11 août 2022
 */
public class OutilsCompression {

	/** java.io.File.separator de unix/linux **/
	public static final String UNIX_FILE_SEPARATOR = "/";

	/** java.io.File.separatorChar de unix/linux **/
	public static final char UNIX_FILE_SEPARATOR_CHAR = '/';

	/**
	 * Extrait le chemin complet d'un fichier unix/linux
	 * @param values Liste des valeurs du chemin complet d'un fichier à extraire
	 * @return le chemin complet d'un fichier unix/linux
	 */
	public static final String getUnixFullname(String... values) {
		String fullname = "";

		if (values != null) {
			for (String value : values) {
				if (!isEmpty(value)) {
					if (value.endsWith(UNIX_FILE_SEPARATOR)) {
						value = value.substring(0, value.length() - 1);
					}

					if (value.startsWith(UNIX_FILE_SEPARATOR)) {
						fullname += value;
					} else {
						if (!isEmpty(fullname)) {
							if (!fullname.endsWith(UNIX_FILE_SEPARATOR)) {
								fullname += UNIX_FILE_SEPARATOR;
							}
						}

						fullname += value;
					}
				}
			}
		}

		return fullname;
	}

	/**
	 * Normalization d'un chemin de nom de répertoire ou fichier en format unix
	 * @param path le chemin à normaliser
	 * @return le chemin normalisé
	 */
	protected static final String toUnixPath(String path) {
		return !OutilsBase.isEmpty(path) ? path.replace(OutilsCommun.WINDOWS_FILE_SEPARATOR, OutilsCommun.UNIX_FILE_SEPARATOR) : path;
	}

	/**
	 * Ajout au zip des entrées de la hierarchie d'un répertoire donné
	 * @param zos Le zip à ajouter les répertoires
	 * @param directory Le répertoire <a ajouter
	 * @param addedDirectories Liste des répertoire déja ajoutés au zip
	 * @throws Exception en cas d'erreur...
	 */
	protected static final void addDirectoryEntries(ZipOutputStream zos, String directory, Set<String> addedDirectories) throws Exception {
		if (!OutilsBase.isEmpty(directory)) {
			addDirectoryEntries(zos, new File(directory).getParent(), addedDirectories);

			String entryName = toUnixPath(directory) + OutilsCommun.UNIX_FILE_SEPARATOR;

			if (addedDirectories.add(entryName)) {
				ZipEntry zipEntry = new ZipEntry(entryName);
				zos.putNextEntry(zipEntry);
				zos.closeEntry();
			}
		}
	}

	/**
	 * Zip de données d'une liste de paires fichier=texte
	 * @param items Liste des paires à ziper
	 * @return Le contenu ziper
	 * @throws Exception en cas d'erreur...
	 */
	public static final byte[] zipContent(List<NameValue> items) throws Exception {
		if (items == null) {
			items = new ArrayList<>();
		}

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			try (ZipOutputStream zos = new ZipOutputStream(baos)) {
				for (NameValue item : items) {
					zos.putNextEntry(new ZipEntry(item.getName()));

					byte[] data = OutilsBase.asString(item.getValue()).getBytes();

					zos.write(data, 0, data.length);

					zos.closeEntry();
				}
			}

			return baos.toByteArray();
		}
	}

	/**
	 * Zip de données
	 * @param data Les données à ziper
	 * @param name Le nom des données (i.e. fichier) à ziper
	 * @param zipFilename Le chemin complet du fichier zip de sortie
	 * @throws Exception en cas d'erreur...
	 */
	public static final void zipFile(byte[] data, String name, String zipFilename) throws Exception {
		if (data == null) {
			data = new byte[0];
		}

		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilename))) {
			zos.putNextEntry(new ZipEntry(name));
			zos.write(data, 0, data.length);
			zos.closeEntry();
		}
	}

	/**
	 * Zip le contenu d'un fichier
	 * @param filename Le chemin complet du fichier à ziper
	 * @param zipFilename Le chemin complet du fichier zip de sortie
	 * @throws Exception en cas d'erreur...
	 */
	public static final void zipFile(String filename, String zipFilename) throws Exception {
		File file = new File(filename);

		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilename))) {
			zos.putNextEntry(new ZipEntry(file.getName()));

			try (FileInputStream fis = new FileInputStream(file)) {
				int len;

				byte[] buffer = new byte[8192];

				while ((len = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}
			}

			zos.closeEntry();
		}
	}

	/**
	 * Zip le contenu d'un fichier
	 * @param filename Le chemin complet du fichier à ziper
	 * @param name Le nom des données (i.e. fichier) à ziper
	 * @param zipFilename Le chemin complet du fichier zip de sortie
	 * @throws Exception en cas d'erreur...
	 */
	public static final void zipFile(String filename, String name, String zipFilename) throws Exception {
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilename))) {
			zos.putNextEntry(new ZipEntry(name));

			try (FileInputStream fis = new FileInputStream(new File(filename))) {
				int len;

				byte[] buffer = new byte[8192];

				while ((len = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}
			}

			zos.closeEntry();
		}
	}

	/**
	 * Zip de données
	 * @param data Les données à ziper
	 * @param name Le nom des données (i.e. fichier) à ziper
	 * @return Le contenu ziper
	 * @throws Exception en cas d'erreur...
	 */
	public static final byte[] zipFileContent(byte[] data, String name) throws Exception {
		if (data == null) {
			data = new byte[0];
		}

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			try (ZipOutputStream zos = new ZipOutputStream(baos)) {
				zos.putNextEntry(new ZipEntry(name));
				zos.write(data, 0, data.length);
				zos.closeEntry();
			}

			return baos.toByteArray();
		}
	}

	/**
	 * Zip le contenu d'un fichier
	 * @param filename Le chemin complet du fichier à ziper
	 * @return Le contenu ziper
	 * @throws Exception en cas d'erreur...
	 */
	public static final byte[] zipFileContent(String filename) throws Exception {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			try (ZipOutputStream zos = new ZipOutputStream(baos)) {
				File file = new File(filename);

				zos.putNextEntry(new ZipEntry(file.getName()));

				try (FileInputStream fis = new FileInputStream(file)) {
					int len;

					byte[] buffer = new byte[8192];

					while ((len = fis.read(buffer)) > 0) {
						zos.write(buffer, 0, len);
					}

				}

				zos.closeEntry();
			}

			return baos.toByteArray();
		}
	}

	/**
	 * Zip le contenu d'un fichier
	 * @param filename Le chemin complet du fichier à ziper
	 * @param name Le nom des données (i.e. fichier) à ziper
	 * @return Le contenu ziper
	 * @throws Exception en cas d'erreur...
	 */
	public static final byte[] zipFileContent(String filename, String name) throws Exception {
		return zipFileContent(new File(filename), name);
	}

	/**
	 * Zip le contenu d'un fichier
	 * @param file Le fichier à ziper
	 * @param name Le nom des données (i.e. fichier) à ziper
	 * @return Le contenu ziper
	 * @throws Exception en cas d'erreur...
	 */
	public static final byte[] zipFileContent(File file, String name) throws Exception {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			try (ZipOutputStream zos = new ZipOutputStream(baos)) {
				zos.putNextEntry(new ZipEntry(name));

				try (FileInputStream fis = new FileInputStream(file)) {
					int len;

					byte[] buffer = new byte[8192];

					while ((len = fis.read(buffer)) > 0) {
						zos.write(buffer, 0, len);
					}

				}

				zos.closeEntry();
			}

			return baos.toByteArray();
		}
	}

	/**
	 * Zip le contenu d'un répertoire
	 * @param baseDir Le répertoire source
	 * @param zipFilename Le chemin complet du fichier zip de sortie
	 * @throws Exception en cas d'erreur...
	 */
	public static final void zipDirectory(String baseDir, String zipFilename) throws Exception {
		zipDirectoryList(OutilsCommun.scanForFiles(baseDir), baseDir, zipFilename);
	}

	/**
	 * Zip le contenu basé sur la liste des fichiers d'un répertoire
	 * @param La liste des fichiers du répertoire
	 * @param baseDir Le répertoire source
	 * @param zipFilename Le chemin complet du fichier zip de sortie
	 * @throws Exception en cas d'erreur...
	 */
	public static final void zipDirectoryList(List<NameValue> filesList, String baseDir, String zipFilename) throws Exception {
		try (FileOutputStream fos = new FileOutputStream(zipFilename)) {
			try (ZipOutputStream zos = new ZipOutputStream(fos)) {
				Set<String> addedDirectories = new HashSet<>();

				for (NameValue item : filesList) {
					addDirectoryEntries(zos, item.getName(), addedDirectories);

					String entry = OutilsCommun.getUnixFullname(toUnixPath(item.getName()), item.getValue());

					zos.putNextEntry(new ZipEntry(entry));

					String filename = OutilsCommun.getFullname(baseDir, item.getName(), item.getValue());

					try (FileInputStream fis = new FileInputStream(filename)) {
						int len;

						byte[] buffer = new byte[8192];

						while ((len = fis.read(buffer)) > 0) {
							zos.write(buffer, 0, len);
						}
					}

					zos.closeEntry();
				}
			}
		}
	}

	/**
	 * Zip le contenu basé sur la liste des fichiers d'un répertoire
	 * @param baseDir Le répertoire source
	 * @return le contenu zipper en format binaire
	 * @throws Exception en cas d'erreur...
	 */
	public static final byte[] zipDirectoryList(String baseDir) throws Exception {
		return zipDirectoryList(OutilsCommun.scanForFiles(baseDir), baseDir);
	}

	/**
	 * Zip le contenu basé sur la liste des fichiers d'un répertoire
	 * @param La liste des fichiers du répertoire
	 * @param baseDir Le répertoire source
	 * @return le contenu zipper en format binaire
	 * @throws Exception en cas d'erreur...
	 */
	public static final byte[] zipDirectoryList(List<NameValue> filesList, String baseDir) throws Exception {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			try (ZipOutputStream zos = new ZipOutputStream(baos)) {
				Set<String> addedDirectories = new HashSet<>();

				for (NameValue item : filesList) {
					addDirectoryEntries(zos, item.getName(), addedDirectories);

					String entry = OutilsCommun.getUnixFullname(toUnixPath(item.getName()), item.getValue());

					zos.putNextEntry(new ZipEntry(entry));

					String filename = OutilsCommun.getFullname(baseDir, item.getName(), item.getValue());

					try (FileInputStream fis = new FileInputStream(filename)) {
						int len;

						byte[] buffer = new byte[8192];

						while ((len = fis.read(buffer)) > 0) {
							zos.write(buffer, 0, len);
						}
					}

					zos.closeEntry();
				}
			}

			return baos.toByteArray();
		}
	}

	/**
	 * Extrait la liste des fichiers depuis un fichier zip source donné
	 * @param zipFilename Le chemin complet du fichier zip source
	 * @return une liste de paires sous forme répertoire=fichier
	 * @throws Exception en cas d'erreur..
	 */
	public static final List<NameValue> zipListFiles(String zipFilename) throws Exception {
		return zipListFiles(zipFilename, null);
	}

	/**
	 * Extrait la liste des fichiers depuis un fichier zip source donné
	 * @param zipFilename Le chemin complet du fichier zip source
	 * @param filter Le filtre des noms de fichiers
	 * @return une liste de paires sous forme répertoire=fichier
	 * @throws Exception en cas d'erreur..
	 */
	public static final List<NameValue> zipListFiles(String zipFilename, FilenameFilter filter) throws Exception {
		List<NameValue> filesList = new ArrayList<NameValue>();

		try (FileInputStream fis = new FileInputStream(zipFilename)) {
			try (ZipInputStream zis = new ZipInputStream(fis)) {
				ZipEntry ze = zis.getNextEntry();

				while (ze != null) {
					if (!ze.isDirectory()) {
						boolean ok = true;

						File file = new File(ze.getName());

						if (filter != null) {
							ok = filter.accept(file.getParentFile(), file.getName());
						}

						if (ok) {
							filesList.add(new NameValue(file.getParent(), file.getName()));
						}
					}

					ze = zis.getNextEntry();
				}

				zis.closeEntry();
			}
		}

		filesList.sort(new NameValue.Compare(true, false));

		return filesList;
	}

	/**
	 * Dézip le contenu d'un fichier zip
	 * @param baseDir Le répertoire de sortie
	 * @param zipFilename Le chemin complet du fichier zip source
	 * @throws Exception en cas d'erreur...
	 */
	public static final void unzipFile(String baseDir, String zipFilename) throws Exception {
		unzipFile(baseDir, zipFilename, null, false);
	}

	/**
	 * Dézip le contenu d'un fichier zip
	 * @param baseDir Le répertoire de sortie
	 * @param zipFilename Le chemin complet du fichier zip source
	 * @param force Indicateur pour forcer la création
	 * @throws Exception en cas d'erreur...
	 */
	public static final void unzipFile(String baseDir, String zipFilename, boolean force) throws Exception {
		unzipFile(baseDir, zipFilename, null, force);
	}

	/**
	 * Dézip le contenu d'un fichier zip
	 * @param baseDir Le répertoire de sortie
	 * @param zipFilename Le chemin complet du fichier zip source
	 * @param filter Le filtre des noms de fichiers à déziper
	 * @throws Exception en cas d'erreur...
	 */
	public static final void unzipFile(String baseDir, String zipFilename, FilenameFilter filter) throws Exception {
		unzipFile(baseDir, zipFilename, filter, false);
	}

	/**
	 * Dézip le contenu d'un fichier zip
	 * @param baseDir Le répertoire de sortie
	 * @param zipFilename Le chemin complet du fichier zip source
	 * @param filter Le filtre des noms de fichiers à déziper
	 * @param force Indicateur pour forcer la création
	 * @throws Exception en cas d'erreur...
	 */
	public static final void unzipFile(String baseDir, String zipFilename, FilenameFilter filter, boolean force) throws Exception {
		OutilsCommun.buildDirectory(baseDir, force);

		try (FileInputStream fis = new FileInputStream(zipFilename)) {
			try (ZipInputStream zis = new ZipInputStream(fis)) {
				ZipEntry ze = zis.getNextEntry();

				while (ze != null) {
					boolean ok = true;

					File file = new File(ze.getName());

					if (filter != null) {
						ok = filter.accept(file.getParentFile(), file.getName());
					}

					if (ok) {
						file = new File(OutilsCommun.getFullname(baseDir, ze.getName())).getCanonicalFile();

						if (ze.isDirectory()) {
							file.mkdirs();
						} else {
							file.getParentFile().mkdirs();

							try (FileOutputStream fos = new FileOutputStream(file)) {
								int len;

								byte[] buffer = new byte[8192];

								while ((len = zis.read(buffer)) > 0) {
									fos.write(buffer, 0, len);
								}
							}
						}
					}

					ze = zis.getNextEntry();
				}

				zis.closeEntry();
			}
		}
	}

	/**
	 * Dézip le contenu d'un fichier zip en mémoire
	 * @param zipFilename Le chemin complet du fichier zip source
	 * @throws Exception en cas d'erreur...
	 */
	public static final List<StringData> unzipFile(String zipFilename) throws Exception {
		return unzipFile(zipFilename, null, new RawFileConverter());
	}

	/**
	 * Dézip le contenu d'un fichier zip en mémoire
	 * @param zipFilename Le chemin complet du fichier zip source
	 * @param filter Le filtre des noms de fichiers à déziper
	 * @throws Exception en cas d'erreur...
	 */
	public static final List<StringData> unzipFile(String zipFilename, FilenameFilter filter) throws Exception {
		return unzipFile(zipFilename, filter, new RawFileConverter());
	}

	/**
	 * Dézip le contenu d'un fichier zip en mémoire
	 * @param zipFilename Le chemin complet du fichier zip source
	 * @param converter Conversion de données brute selon le type du fichier
	 * @throws Exception en cas d'erreur...
	 */
	public static final List<StringData> unzipFile(String zipFilename, IRawFileConverter converter) throws Exception {
		return unzipFile(zipFilename, null, converter);
	}

	/**
	 * Dézip le contenu d'un fichier zip en mémoire
	 * @param zipFilename Le chemin complet du fichier zip source
	 * @param filter Le filtre des noms de fichiers à déziper
	 * @param converter Conversion de données brute selon le type du fichier
	 * @throws Exception en cas d'erreur...
	 */
	public static final List<StringData> unzipFile(String zipFilename, FilenameFilter filter, IRawFileConverter converter) throws Exception {
		List<StringData> list = new ArrayList<StringData>();

		try (FileInputStream fis = new FileInputStream(zipFilename)) {
			try (ZipInputStream zis = new ZipInputStream(fis)) {
				ZipEntry ze = zis.getNextEntry();

				while (ze != null) {
					if (!ze.isDirectory()) {
						boolean ok = true;

						if (filter != null) {
							File file = new File(ze.getName());
							ok = filter.accept(file.getParentFile(), file.getName());
						}

						if (ok) {
							StringData data = new StringData(ze.getName());
							list.add(data);

							try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
								int len;

								byte[] buffer = new byte[8192];

								while ((len = zis.read(buffer)) > 0) {
									baos.write(buffer, 0, len);
								}

								if (converter != null) {
									data.setData(converter.convert(data.getString(), baos.toByteArray()));
								} else {
									data.setData(baos.toByteArray());
								}
							}
						}
					}

					ze = zis.getNextEntry();
				}

				zis.closeEntry();
			}
		}

		return list;
	}

	/**
	 * Dézip un contenu binaire en format zip en mémoire
	 * @param content Le contenu binaire en format zip à dézipper
	 * @return la liste des fichiers et données
	 * @throws Exception en cas d'erreur...
	 */
	public static final List<StringData> unzipFile(byte[] content) throws Exception {
		return unzipFile(content, null, new RawFileConverter());
	}

	/**
	 * Dézip un contenu binaire en format zip en mémoire
	 * @param content Le contenu binaire en format zip à dézipper
	 * @param filter Le filtre des noms de fichiers à déziper
	 * @return la liste des fichiers et données
	 * @throws Exception en cas d'erreur...
	 */
	public static final List<StringData> unzipFile(byte[] content, FilenameFilter filter) throws Exception {
		return unzipFile(content, filter, new RawFileConverter());
	}

	/**
	 * Dézip un contenu binaire en format zip en mémoire
	 * @param content Le contenu binaire en format zip à dézipper
	 * @param converter Conversion de données brute selon le type du fichier
	 * @return la liste des fichiers et données
	 * @throws Exception en cas d'erreur...
	 */
	public static final List<StringData> unzipFile(byte[] content, IRawFileConverter converter) throws Exception {
		return unzipFile(content, null, converter);
	}

	/**
	 * Dézip un contenu binaire en format zip en mémoire
	 * @param content Le contenu binaire en format zip à dézipper
	 * @param filter Le filtre des noms de fichiers à déziper
	 * @param converter Conversion de données brute selon le type du fichier
	 * @return la liste des fichiers et données
	 * @throws Exception en cas d'erreur...
	 */
	public static final List<StringData> unzipFile(byte[] content, FilenameFilter filter, IRawFileConverter converter) throws Exception {
		List<StringData> list = new ArrayList<StringData>();

		try (ByteArrayInputStream bais = new ByteArrayInputStream(content)) {
			try (ZipInputStream zis = new ZipInputStream(bais)) {
				ZipEntry ze = zis.getNextEntry();

				while (ze != null) {
					if (!ze.isDirectory()) {
						boolean ok = true;

						if (filter != null) {
							File file = new File(ze.getName());
							ok = filter.accept(file.getParentFile(), file.getName());
						}

						if (ok) {
							StringData data = new StringData(ze.getName());
							list.add(data);

							try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
								int len;

								byte[] buffer = new byte[8192];

								while ((len = zis.read(buffer)) > 0) {
									baos.write(buffer, 0, len);
								}

								if (converter != null) {
									data.setData(converter.convert(data.getString(), baos.toByteArray()));
								} else {
									data.setData(baos.toByteArray());
								}
							}
						}
					}

					ze = zis.getNextEntry();
				}

				zis.closeEntry();
			}
		}

		return list;
	}

	/**
	 * Extrait la liste des items d'un fichier zip
	 * @param content Le contenu binaire en format zip à dézipper
	 * @throws Exception en cas d'erreur...
	 */
	public static final List<ZipEntry> unzipListEntries(byte[] content) throws Exception {
		return unzipListEntries(content, null);
	}

	/**
	 * Extrait la liste des items d'un fichier zip
	 * @param content Le contenu binaire en format zip à dézipper
	 * @param filter Le filtre des noms de fichiers à déziper
	 * @throws Exception en cas d'erreur...
	 */
	public static final List<ZipEntry> unzipListEntries(byte[] content, FilenameFilter filter) throws Exception {
		List<ZipEntry> entriesList = new ArrayList<ZipEntry>();

		try (ByteArrayInputStream bais = new ByteArrayInputStream(content)) {
			try (ZipInputStream zis = new ZipInputStream(bais)) {
				ZipEntry ze = zis.getNextEntry();

				while (ze != null) {
					boolean ok = true;

					if (filter != null) {
						File file = new File(ze.getName());
						ok = filter.accept(file.getParentFile(), file.getName());
					}

					if (ok) {
						entriesList.add(ze);
					}

					ze = zis.getNextEntry();
				}

				zis.closeEntry();
			}
		}

		return entriesList;
	}

	/**
	 * Extrait la liste des items d'un contenu binaire en format zip
	 * @param zipFilename Le chemin complet du fichier zip source
	 * @throws Exception en cas d'erreur...
	 */
	public static final List<ZipEntry> unzipListEntries(String zipFilename) throws Exception {
		return unzipListEntries(zipFilename, null);
	}

	/**
	 * Extrait la liste des items d'un contenu binaire en format zip
	 * @param zipFilename Le chemin complet du fichier zip source
	 * @param filter Le filtre des noms de fichiers à déziper
	 * @throws Exception en cas d'erreur...
	 */
	public static final List<ZipEntry> unzipListEntries(String zipFilename, FilenameFilter filter) throws Exception {
		List<ZipEntry> entriesList = new ArrayList<ZipEntry>();

		try (FileInputStream fis = new FileInputStream(zipFilename)) {
			try (ZipInputStream zis = new ZipInputStream(fis)) {
				ZipEntry ze = zis.getNextEntry();

				while (ze != null) {
					boolean ok = true;

					if (filter != null) {
						File file = new File(ze.getName());
						ok = filter.accept(file.getParentFile(), file.getName());
					}

					if (ok) {
						entriesList.add(ze);
					}

					ze = zis.getNextEntry();
				}

				zis.closeEntry();
			}
		}

		return entriesList;
	}

	/**
	 * GZIP de données
	 * @param data Les données à gziper
	 * @param gzipFilename Le chemin complet du fichier gzip de sortie
	 * @throws Exception en cas d'erreur...
	 */
	public static final void gzipFile(byte[] data, String gzipFilename) throws Exception {
		if (data == null) {
			data = new byte[0];
		}

		try (FileOutputStream fos = new FileOutputStream(gzipFilename)) {
			try (GZIPOutputStream gos = new GZIPOutputStream(fos)) {

				gos.write(data, 0, data.length);
			}
		}
	}

	/**
	 * GZIP le contenu d'un fichier
	 * @param filename Le chemin complet du fichier à gziper
	 * @param gzipFilename Le chemin complet du fichier gzip de sortie
	 * @throws Exception en cas d'erreur...
	 */
	public static final void gzipFile(String filename, String gzipFilename) throws Exception {
		File file = new File(filename);

		try (FileOutputStream fos = new FileOutputStream(gzipFilename)) {
			try (GZIPOutputStream gos = new GZIPOutputStream(fos)) {
				try (FileInputStream fis = new FileInputStream(file)) {
					int len;

					byte[] buffer = new byte[8192];

					while ((len = fis.read(buffer)) > 0) {
						gos.write(buffer, 0, len);
					}
				}
			}
		}
	}

	/**
	 * GZIP de données
	 * @param data Les données à gziper
	 * @return Le contenu gziper
	 * @throws Exception en cas d'erreur...
	 */
	public static final byte[] gzipFileContent(byte[] data) throws Exception {
		if (data == null) {
			data = new byte[0];
		}

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			try (GZIPOutputStream gos = new GZIPOutputStream(baos)) {
				gos.write(data, 0, data.length);
			}

			return baos.toByteArray();
		}
	}

	/**
	 * GZIP le contenu d'un fichier
	 * @param filename Le chemin complet du fichier à gziper
	 * @return Le contenu gziper
	 * @throws Exception en cas d'erreur...
	 */
	public static final byte[] gzipFileContent(String filename) throws Exception {
		return gzipFileContent(new File(filename));
	}

	/**
	 * GZIP le contenu d'un fichier
	 * @param file Le fichier à gziper
	 * @return Le contenu gziper
	 * @throws Exception en cas d'erreur...
	 */
	public static final byte[] gzipFileContent(File file) throws Exception {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			try (GZIPOutputStream gos = new GZIPOutputStream(baos)) {
				try (FileInputStream fis = new FileInputStream(file)) {
					int len;

					byte[] buffer = new byte[8192];

					while ((len = fis.read(buffer)) > 0) {
						gos.write(buffer, 0, len);
					}
				}
			}

			return baos.toByteArray();
		}
	}

	/**
	 * GZIP le contenu d'un répertoire
	 * @param baseDir Le répertoire source
	 * @throws Exception en cas d'erreur...
	 */
	public static final void gzipDirectory(String baseDir) throws Exception {
		gzipDirectoryList(OutilsCommun.scanForFiles(baseDir), baseDir);
	}

	/**
	 * GZIP le contenu basé sur la liste des fichiers d'un répertoire
	 * @param La liste des fichiers du répertoire
	 * @param baseDir Le répertoire source
	 * @throws Exception en cas d'erreur...
	 */
	public static final void gzipDirectoryList(List<NameValue> filesList, String baseDir) throws Exception {
		for (NameValue item : filesList) {
			String entry = OutilsCommun.getFullname(item.getName(), item.getValue());

			String filename = OutilsCommun.getFullname(baseDir, entry);

			String gzipFilename = filename + OutilsCommun.GZIP_EXTENSION;

			gzipFile(filename, gzipFilename);
		}
	}

	/**
	 * Dézip le contenu d'un fichier gzip
	 * @param gzipFilename Le chemin complet du fichier gzip source
	 * @throws Exception en cas d'erreur...
	 */
	public static final void ungzipFile(String gzipFilename) throws Exception {
		String filename;

		if (OutilsBase.endsWithIgnoreCase(gzipFilename, OutilsCommun.GZIP_EXTENSION)) {
			filename = gzipFilename.substring(0, gzipFilename.length() - OutilsCommun.GZIP_EXTENSION.length());
		} else {
			throw new Exception("Le nom du fichier gzip ne se termine pas par l'extension " + OutilsCommun.GZIP_EXTENSION);
		}

		ungzipFile(gzipFilename, filename);
	}

	/**
	 * Dézip le contenu d'un fichier gzip
	 * @param gzipFilename Le chemin complet du fichier gzip source
	 * @param filename Le chemin complet du fichier de sortie
	 * @throws Exception en cas d'erreur...
	 */
	public static final void ungzipFile(String gzipFilename, String filename) throws Exception {
		try (FileInputStream fis = new FileInputStream(gzipFilename)) {
			try (GZIPInputStream gis = new GZIPInputStream(fis)) {
				try (FileOutputStream fos = new FileOutputStream(filename)) {
					int len;

					byte[] buffer = new byte[8192];

					while ((len = gis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
				}
			}
		}
	}

	/**
	 * Dézip le contenu d'un fichier gzip en mémoire
	 * @param gzipFilename Le chemin complet du fichier gzip source
	 * @return le contenu d'un fichier gzip en mémoire
	 * @throws Exception en cas d'erreur...
	 */
	public static final Object ungzipFileToObject(String gzipFilename) throws Exception {
		return ungzipFileToObject(gzipFilename, new RawFileConverter());
	}

	/**
	 * Dézip le contenu d'un fichier gzip en mémoire
	 * @param gzipFilename Le chemin complet du fichier gzip source
	 * @param converter Conversion de données brute selon le type du fichier
	 * @return le contenu d'un fichier gzip en mémoire
	 * @throws Exception en cas d'erreur...
	 */
	public static final Object ungzipFileToObject(String gzipFilename, IRawFileConverter converter) throws Exception {
		String filename;

		if (OutilsBase.endsWithIgnoreCase(gzipFilename, OutilsCommun.GZIP_EXTENSION)) {
			filename = gzipFilename.substring(0, gzipFilename.length() - OutilsCommun.GZIP_EXTENSION.length());
		} else {
			throw new Exception("Le nom du fichier gzip ne se termine pas par l'extension " + OutilsCommun.GZIP_EXTENSION);
		}

		return ungzipFileToObject(gzipFilename, filename, converter);
	}

	/**
	 * Dézip le contenu d'un fichier gzip en mémoire
	 * @param gzipFilename Le chemin complet du fichier gzip source
	 * @param filename Le chemin complet du fichier gzip source
	 * @param converter Conversion de données brute selon le type du fichier
	 * @return le contenu d'un fichier gzip en mémoire
	 * @throws Exception en cas d'erreur...
	 */
	public static final Object ungzipFileToObject(String gzipFilename, String filename, IRawFileConverter converter) throws Exception {
		try (FileInputStream fis = new FileInputStream(gzipFilename)) {
			try (GZIPInputStream gis = new GZIPInputStream(fis)) {
				try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

					int len;

					byte[] buffer = new byte[8192];

					while ((len = gis.read(buffer)) > 0) {
						baos.write(buffer, 0, len);
					}

					if (converter != null) {
						return converter.convert(filename, baos.toByteArray());
					} else {
						return baos.toByteArray();
					}
				}
			}
		}
	}

	/**
	 * Dézip un contenu binaire en format gzip en mémoire
	 * @param content Le contenu binaire en format gzip à dézipper
	 * @return le contenu binaire en format gzip en mémoire
	 * @throws Exception en cas d'erreur...
	 */
	public static final byte[] ungzipFile(byte[] content) throws Exception {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(content)) {
			try (GZIPInputStream gis = new GZIPInputStream(bais)) {
				try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
					int len;

					byte[] buffer = new byte[8192];

					while ((len = gis.read(buffer)) > 0) {
						baos.write(buffer, 0, len);
					}

					return baos.toByteArray();
				}
			}
		}
	}

}
