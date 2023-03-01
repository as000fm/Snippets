package junit.src.outils.commun;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import junit.JunitHelper;
import outils.base.OutilsBase;
import outils.commun.OutilsCommun;
import outils.listes.NameValue;

/**
 * Cas de tests suppémentaires pour obtenir 100% de couverture
 * @author Claude Toupin - 23 janv. 2023
 */
@DisplayName("outils.commun.OutilsCommun")
public class OutilsCommunCasesTest {

	@Test
	@DisplayName("junit.src.outils.commun.testOutilsCommunCasesTest()")
	void testOutilsCommunCasesTest(TestInfo testInfo) throws Exception {
		System.out.println(testInfo.getDisplayName());

		// decompress

		Vector<String> vector = new Vector<String>();
		vector.add("AAA");

		outils.commun.OutilsCommun.decompress(OutilsCommun.compress(vector, 1));

		// decompressSerializable

		outils.commun.OutilsCommun.decompressSerializable(OutilsCommun.compressSerializable("AAA"));

		// cleanDirectories

		String cleanDirectories = JunitHelper.getFullname(JunitHelper.TEST_TEMP_DIR, "cleanDirectories");

		OutilsCommun.saveToFile("AAA", cleanDirectories);

		try {
			outils.commun.OutilsCommun.cleanDirectories(cleanDirectories);
		} catch (Exception e) {
			// OK
		}

		Files.deleteIfExists(new File(cleanDirectories).toPath());

		outils.commun.OutilsCommun.cleanDirectories(cleanDirectories);

		outils.commun.OutilsCommun.cleanDirectories(cleanDirectories);

		String subdir = JunitHelper.getFullname(cleanDirectories, "subdir");

		OutilsCommun.buildDirectory(subdir);

		OutilsCommun.saveToFile("AAA", JunitHelper.getFullname(subdir, "subdir.txt"));

		outils.commun.OutilsCommun.cleanDirectories(cleanDirectories);

		// backupFile

		String backupFileDir = JunitHelper.getFullname(JunitHelper.TEST_TEMP_DIR, "backupFile");

		OutilsCommun.buildDirectory(backupFileDir, true);

		String baseFileName = JunitHelper.getFullname(backupFileDir, "backupFile");
		String extension = OutilsCommun.TEXT_EXTENSION;

		outils.commun.OutilsCommun.backupFile(baseFileName, extension);

		OutilsCommun.saveToFile("AAA", baseFileName + extension);

		outils.commun.OutilsCommun.backupFile(baseFileName, extension);

		outils.commun.OutilsCommun.backupFile(baseFileName, extension);

		// buildDirectory

		String buildDirectory = JunitHelper.getFullname(JunitHelper.TEST_TEMP_DIR, "buildDirectory");

		OutilsCommun.saveToFile("AAA", buildDirectory);

		try {
			outils.commun.OutilsCommun.buildDirectory(buildDirectory);
		} catch (Exception e) {
			// OK
		}

		Files.deleteIfExists(new File(buildDirectory).toPath());

		outils.commun.OutilsCommun.buildDirectory(buildDirectory);

		outils.commun.OutilsCommun.buildDirectory(buildDirectory);

		String buildDirectory1 = JunitHelper.getFullname(JunitHelper.TEST_TEMP_DIR, "buildDirectory1");

		OutilsCommun.saveToFile("AAA", buildDirectory1);

		try {
			outils.commun.OutilsCommun.buildDirectory(buildDirectory1, 0x640);
		} catch (Exception e) {
			// OK
		}

		Files.deleteIfExists(new File(buildDirectory1).toPath());

		outils.commun.OutilsCommun.buildDirectory(buildDirectory1, 0x640);

		outils.commun.OutilsCommun.buildDirectory(buildDirectory1, 0x640);

		String buildDirectory2 = JunitHelper.getFullname(JunitHelper.TEST_TEMP_DIR, "buildDirectory2");

		OutilsCommun.saveToFile("AAA", buildDirectory2);

		try {
			outils.commun.OutilsCommun.buildDirectory(buildDirectory2, false);
		} catch (Exception e) {
			// OK
		}

		outils.commun.OutilsCommun.buildDirectory(buildDirectory2, true);

		Files.deleteIfExists(new File(buildDirectory2).toPath());

		outils.commun.OutilsCommun.buildDirectory(buildDirectory2, false);

		outils.commun.OutilsCommun.buildDirectory(buildDirectory2, false);

		outils.commun.OutilsCommun.buildDirectory(buildDirectory2, true);

		String buildDirectory3 = JunitHelper.getFullname(JunitHelper.TEST_TEMP_DIR, "buildDirectory3");

		OutilsCommun.saveToFile("AAA", buildDirectory3);

		try {
			outils.commun.OutilsCommun.buildDirectory(buildDirectory3, 0x640, false);
		} catch (Exception e) {
			// OK
		}

		outils.commun.OutilsCommun.buildDirectory(buildDirectory3, 0x640, true);

		Files.deleteIfExists(new File(buildDirectory3).toPath());

		outils.commun.OutilsCommun.buildDirectory(buildDirectory3, 0x640, false);

		outils.commun.OutilsCommun.buildDirectory(buildDirectory3, 0x640, false);

		outils.commun.OutilsCommun.buildDirectory(buildDirectory3, 0x640, true);

		// copyDirectory

		String sourceDir = JunitHelper.getFullname(JunitHelper.TEST_TEMP_DIR, "sourceDir");

		OutilsCommun.buildDirectory(sourceDir, true);

		OutilsCommun.saveToFile("AAA", JunitHelper.getFullname(sourceDir, "File.txt"));

		String sourceSubDir = JunitHelper.getFullname(sourceDir, "sourceSubDir");

		OutilsCommun.buildDirectory(sourceSubDir, true);

		OutilsCommun.saveToFile("AAA", JunitHelper.getFullname(sourceSubDir, "FileSubDir.txt"));

		String destinationDir = JunitHelper.getFullname(JunitHelper.TEST_TEMP_DIR, "destinationDir");

		OutilsCommun.buildDirectory(destinationDir, true);

		outils.commun.OutilsCommun.copyDirectory(sourceDir, destinationDir);

		outils.commun.OutilsCommun.copyDirectory(new File(sourceDir), destinationDir);

		// deleteEmptyDirectory

		assertEquals(true, outils.commun.OutilsCommun.deleteEmptyDirectory(destinationDir), "static boolean deleteEmptyDirectory(String dirName, int retries) -> outils.commun.OutilsCommun.deleteEmptyDirectory(\"" + destinationDir + "\""); // AssertTemplate

		assertEquals(true, outils.commun.OutilsCommun.deleteEmptyDirectory(new File(destinationDir), 10), "static boolean deleteEmptyDirectory(File dir, int retries) -> outils.commun.OutilsCommun.deleteEmptyDirectory(new File(\"" + destinationDir + "\"), 10"); // AssertTemplate

		OutilsCommun.buildDirectory(destinationDir, true);

		assertEquals(true, outils.commun.OutilsCommun.deleteEmptyDirectory(destinationDir), "static boolean deleteEmptyDirectory(String dirName, int retries) -> outils.commun.OutilsCommun.deleteEmptyDirectory(\"" + destinationDir + "\""); // AssertTemplate

		// deleteFileDirectory

		try {
			outils.commun.OutilsCommun.deleteFileDirectory((String) null);
		} catch (Exception e) {
			// OK
		}

		outils.commun.OutilsCommun.deleteFileDirectory(sourceSubDir);
		outils.commun.OutilsCommun.deleteFileDirectory(sourceSubDir);

		// removeEmptyDirectories

		String removeDir = JunitHelper.getFullname(JunitHelper.TEST_TEMP_DIR, "removeDir");

		OutilsCommun.buildDirectory(removeDir, true);

		OutilsCommun.saveToFile("AAA", JunitHelper.getFullname(removeDir, "File.txt"));

		String removeSubDir = JunitHelper.getFullname(removeDir, "removeSubDir");

		OutilsCommun.buildDirectory(removeSubDir, true);

		OutilsCommun.saveToFile("AAA", JunitHelper.getFullname(removeSubDir, "FileSubDir.txt"));

		String removeEmptySubDir = JunitHelper.getFullname(removeDir, "removeEmptySubDir");

		OutilsCommun.buildDirectory(removeEmptySubDir, true);

		outils.commun.OutilsCommun.removeEmptyDirectories(new File(removeDir).listFiles());

		try {
			outils.commun.OutilsCommun.removeEmptyDirectories("Automated Test Value");
		} catch (Exception e) {
			// OK
		}

		try {
			outils.commun.OutilsCommun.removeEmptyDirectories(JunitHelper.getFullname(removeDir, "File.txt"));
		} catch (Exception e) {
			// OK
		}

		outils.commun.OutilsCommun.removeEmptyDirectories(removeDir);

		// renameFile

		try {
			outils.commun.OutilsCommun.renameFile((String) null, (String) null);
		} catch (Exception e) {
			// OK
		}

		try {
			outils.commun.OutilsCommun.renameFile("Automated Test Value", (String) null);
		} catch (Exception e) {
			// OK
		}

		outils.commun.OutilsCommun.renameFile(JunitHelper.getFullname(removeDir, "File.txt"), JunitHelper.getFullname(removeDir, "File2.txt"));

		try {
			outils.commun.OutilsCommun.renameFile((File) null, (String) null);
		} catch (Exception e) {
			// OK
		}

		outils.commun.OutilsCommun.renameFile(new File(JunitHelper.getFullname(removeDir, "File2.txt")), JunitHelper.getFullname(removeDir, "File.txt"));

		// scanForDirectories

		String scanForDir = JunitHelper.getFullname(JunitHelper.TEST_TEMP_DIR, "scanForDir");

		OutilsCommun.buildDirectory(scanForDir, true);

		OutilsCommun.saveToFile("AAA", JunitHelper.getFullname(scanForDir, "File.txt"));

		String scanForSubDir1 = JunitHelper.getFullname(scanForDir, "scanForSubDir1");

		OutilsCommun.buildDirectory(scanForSubDir1, true);

		OutilsCommun.saveToFile("AAA", JunitHelper.getFullname(scanForSubDir1, "FileSubDir.txt"));

		OutilsCommun.saveToFile("AAA", JunitHelper.getFullname(scanForSubDir1, "FileSubDir.csv"));

		String scanForSubDir2 = JunitHelper.getFullname(scanForDir, "scanForSubDir2");

		OutilsCommun.buildDirectory(scanForSubDir2, true);

		OutilsCommun.saveToFile("AAA", JunitHelper.getFullname(scanForSubDir2, "FileSubDir.txt"));

		OutilsCommun.saveToFile("AAA", JunitHelper.getFullname(scanForSubDir2, "FileSubDir.csv"));

		outils.commun.OutilsCommun.scanForDirectories(new File(scanForDir));

		outils.commun.OutilsCommun.scanForDirectories(scanForDir);

		outils.commun.OutilsCommun.scanForDirectories(new File(scanForDir), new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return "scanForSubDir2".equals(name);
			}
		});

		outils.commun.OutilsCommun.scanForDirectories(new File(scanForDir), false);

		outils.commun.OutilsCommun.scanForDirectories(new File(scanForDir), true);

		outils.commun.OutilsCommun.scanForDirectories(scanForDir, new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return "scanForSubDir1".equals(name);
			}
		});

		outils.commun.OutilsCommun.scanForDirectories(scanForDir, false);

		outils.commun.OutilsCommun.scanForDirectories(scanForDir, true);

		outils.commun.OutilsCommun.scanForDirectories(new File(scanForDir), new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return "scanForSubDir1".equals(name);
			}
		}, false);

		outils.commun.OutilsCommun.scanForDirectories(new File(scanForDir), new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return "scanForSubDir1".equals(name);
			}
		}, true);

		outils.commun.OutilsCommun.scanForDirectories(new File(scanForDir), false, false);

		outils.commun.OutilsCommun.scanForDirectories(new File(scanForDir), false, true);

		outils.commun.OutilsCommun.scanForDirectories(new File(scanForDir), true, false);

		outils.commun.OutilsCommun.scanForDirectories(new File(scanForDir), true, true);

		outils.commun.OutilsCommun.scanForDirectories(scanForDir, new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return "scanForSubDir1".equals(name);
			}
		}, false);

		outils.commun.OutilsCommun.scanForDirectories(scanForDir, new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return "scanForSubDir1".equals(name);
			}
		}, true);

		outils.commun.OutilsCommun.scanForDirectories(scanForDir, false, false);

		outils.commun.OutilsCommun.scanForDirectories(scanForDir, false, true);

		outils.commun.OutilsCommun.scanForDirectories(scanForDir, true, false);

		outils.commun.OutilsCommun.scanForDirectories(scanForDir, true, true);

		outils.commun.OutilsCommun.scanForDirectories(new File(scanForDir), new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return "scanForSubDir2".equals(name);
			}
		}, false, false);

		outils.commun.OutilsCommun.scanForDirectories(new File(scanForDir), new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return "scanForSubDir2".equals(name);
			}
		}, false, true);

		outils.commun.OutilsCommun.scanForDirectories(new File(scanForDir), new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return "scanForSubDir2".equals(name);
			}
		}, true, false);

		outils.commun.OutilsCommun.scanForDirectories(new File(scanForDir), new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return "scanForSubDir2".equals(name);
			}
		}, true, true);

		outils.commun.OutilsCommun.scanForDirectories(scanForDir, new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return "scanForSubDir2".equals(name);
			}
		}, false, false);

		outils.commun.OutilsCommun.scanForDirectories(scanForDir, new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return "scanForSubDir2".equals(name);
			}
		}, false, true);

		outils.commun.OutilsCommun.scanForDirectories(scanForDir, new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return "scanForSubDir2".equals(name);
			}
		}, true, false);

		outils.commun.OutilsCommun.scanForDirectories(scanForDir, new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return "scanForSubDir2".equals(name);
			}
		}, true, true);

		// scanForFiles

		outils.commun.OutilsCommun.scanForFiles(new File(scanForDir));

		outils.commun.OutilsCommun.scanForFiles(scanForDir);

		outils.commun.OutilsCommun.scanForFiles(new File(scanForDir), new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return OutilsBase.endsWithIgnoreCase(name, OutilsCommun.TEXT_EXTENSION);
			}
		});

		outils.commun.OutilsCommun.scanForFiles(new File(scanForDir), false);

		outils.commun.OutilsCommun.scanForFiles(new File(scanForDir), true);

		outils.commun.OutilsCommun.scanForFiles(scanForDir, new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return OutilsBase.endsWithIgnoreCase(name, OutilsCommun.TEXT_EXTENSION);
			}
		});

		outils.commun.OutilsCommun.scanForFiles(scanForDir, false);

		outils.commun.OutilsCommun.scanForFiles(scanForDir, true);

		outils.commun.OutilsCommun.scanForFiles(new File(scanForDir), new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return OutilsBase.endsWithIgnoreCase(name, OutilsCommun.CSV_EXTENSION);
			}
		}, false);

		outils.commun.OutilsCommun.scanForFiles(new File(scanForDir), new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return OutilsBase.endsWithIgnoreCase(name, OutilsCommun.CSV_EXTENSION);
			}
		}, true);

		outils.commun.OutilsCommun.scanForFiles(scanForDir, new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return OutilsBase.endsWithIgnoreCase(name, OutilsCommun.CSV_EXTENSION);
			}
		}, false);

		outils.commun.OutilsCommun.scanForFiles(scanForDir, new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return OutilsBase.endsWithIgnoreCase(name, OutilsCommun.CSV_EXTENSION);
			}
		}, true);

		// setDirectoryPermissions

		String directoryPermissions = JunitHelper.getFullname(JunitHelper.TEST_TEMP_DIR, "directoryPermissions");

		OutilsCommun.buildDirectory(directoryPermissions, true);

		String filePermissions = JunitHelper.getFullname(directoryPermissions, "File.txt");

		OutilsCommun.saveToFile("AAA", filePermissions);

		outils.commun.OutilsCommun.setDirectoryPermissions(new File(directoryPermissions), 0x640);

		try {
			outils.commun.OutilsCommun.setDirectoryPermissions(new File(filePermissions), 0x640);
		} catch (Exception e) {
			// OK
		}

		outils.commun.OutilsCommun.setDirectoryPermissions(directoryPermissions, 0x640);

		// setFilePermissions

		outils.commun.OutilsCommun.setFilePermissions(new File(filePermissions), 0x640);

		try {
			outils.commun.OutilsCommun.setFilePermissions(new File(directoryPermissions), 0x640);
		} catch (Exception e) {
			// OK
		}

		outils.commun.OutilsCommun.setFilePermissions(filePermissions, 0x640);

		// splitFile

		String splitFile = JunitHelper.getFullname(JunitHelper.TEST_TEMP_DIR, "File.txt");

		OutilsCommun.saveToFile("AAA", splitFile);

		String splitFileDir = JunitHelper.getFullname(JunitHelper.TEST_TEMP_DIR, "splitFileDir");

		OutilsCommun.buildDirectory(splitFileDir, true);

		outils.commun.OutilsCommun.splitFile(new File(splitFile), (long) 2, splitFileDir);

		OutilsCommun.cleanDirectories(splitFileDir);

		outils.commun.OutilsCommun.splitFile(splitFile, (long) 2, splitFileDir);
		
		// MIME

		List<NameValue> mimeList = new ArrayList<>();

		mimeList.add(new NameValue("3dmf", "x-world/x-3dmf"));
		mimeList.add(new NameValue("a", "application/octet-stream"));
		mimeList.add(new NameValue("aab", "application/x-authorware-bin"));
		mimeList.add(new NameValue("aam", "application/x-authorware-map"));
		mimeList.add(new NameValue("aas", "application/x-authorware-seg"));
		mimeList.add(new NameValue("abc", "text/vnd.abc"));
		mimeList.add(new NameValue("acgi", "text/html"));
		mimeList.add(new NameValue("afl", "video/animaflex"));
		mimeList.add(new NameValue("ai", "application/postscript"));
		mimeList.add(new NameValue("aif", "audio/x-aiff"));
		mimeList.add(new NameValue("aifc", "audio/x-aiff"));
		mimeList.add(new NameValue("aiff", "audio/x-aiff"));
		mimeList.add(new NameValue("aim", "application/x-aim"));
		mimeList.add(new NameValue("aip", "text/x-audiosoft-intra"));
		mimeList.add(new NameValue("ani", "application/x-navi-animation"));
		mimeList.add(new NameValue("aos", "application/x-nokia-9000-communicator-add-on-software"));
		mimeList.add(new NameValue("aps", "application/mime"));
		mimeList.add(new NameValue("arc", "application/octet-stream"));
		mimeList.add(new NameValue("arj", "application/arj"));
		mimeList.add(new NameValue("art", "image/x-jg"));
		mimeList.add(new NameValue("asf", "video/x-ms-asf"));
		mimeList.add(new NameValue("asm", "text/plain"));
		mimeList.add(new NameValue("asp", "text/plain"));
		mimeList.add(new NameValue("asx", "video/x-ms-asf"));
		mimeList.add(new NameValue("au", "audio/x-au"));
		mimeList.add(new NameValue("avi", "video/avi"));
		mimeList.add(new NameValue("avs", "video/avs-video"));
		mimeList.add(new NameValue("bcpio", "application/x-bcpio"));
		mimeList.add(new NameValue("bin", "application/x-binary"));
		mimeList.add(new NameValue("bm", "image/bmp"));
		mimeList.add(new NameValue("bmp", "image/bmp"));
		mimeList.add(new NameValue("boo", "application/book"));
		mimeList.add(new NameValue("book", "application/book"));
		mimeList.add(new NameValue("boz", "application/x-bzip2"));
		mimeList.add(new NameValue("bsh", "application/x-bsh"));
		mimeList.add(new NameValue("bz", "application/x-bzip"));
		mimeList.add(new NameValue("bz2", "application/x-bzip2"));
		mimeList.add(new NameValue("c", "text/plain"));
		mimeList.add(new NameValue("c++", "text/plain"));
		mimeList.add(new NameValue("cat", "application/vnd.ms-pki.seccat"));
		mimeList.add(new NameValue("cc", "text/plain"));
		mimeList.add(new NameValue("ccad", "application/clariscad"));
		mimeList.add(new NameValue("cco", "application/x-cocoa"));
		mimeList.add(new NameValue("cdf", "application/x-cdf"));
		mimeList.add(new NameValue("cer", "application/x-x509-ca-cert"));
		mimeList.add(new NameValue("cha", "application/x-chat"));
		mimeList.add(new NameValue("chat", "application/x-chat"));
		mimeList.add(new NameValue("class", "application/java"));
		mimeList.add(new NameValue("com", "application/octet-stream"));
		mimeList.add(new NameValue("conf", "text/plain"));
		mimeList.add(new NameValue("cpio", "application/x-cpio"));
		mimeList.add(new NameValue("cpp", "text/plain"));
		mimeList.add(new NameValue("cpt", "application/x-cpt"));
		mimeList.add(new NameValue("crl", "application/pkix-crl"));
		mimeList.add(new NameValue("crt", "application/x-x509-ca-cert"));
		mimeList.add(new NameValue("csh", "application/x-csh"));
		mimeList.add(new NameValue("css", "text/css"));
		mimeList.add(new NameValue("cxx", "text/plain"));
		mimeList.add(new NameValue("dcr", "application/x-director"));
		mimeList.add(new NameValue("deepv", "application/x-deepv"));
		mimeList.add(new NameValue("def", "text/plain"));
		mimeList.add(new NameValue("der", "application/x-x509-ca-cert"));
		mimeList.add(new NameValue("dif", "video/x-dv"));
		mimeList.add(new NameValue("dir", "application/x-director"));
		mimeList.add(new NameValue("dl", "video/dl"));
		mimeList.add(new NameValue("doc", "application/msword"));
		mimeList.add(new NameValue("dot", "application/msword"));
		mimeList.add(new NameValue("dp", "application/commonground"));
		mimeList.add(new NameValue("drw", "application/drafting"));
		mimeList.add(new NameValue("dump", "application/octet-stream"));
		mimeList.add(new NameValue("dv", "video/x-dv"));
		mimeList.add(new NameValue("dvi", "application/x-dvi"));
		mimeList.add(new NameValue("dwf", "model/vnd.dwf"));
		mimeList.add(new NameValue("dwg", "application/acad"));
		mimeList.add(new NameValue("dxf", "image/x-dwg"));
		mimeList.add(new NameValue("dxr", "application/x-director"));
		mimeList.add(new NameValue("el", "text/plain"));
		mimeList.add(new NameValue("elc", "application/x-elc"));
		mimeList.add(new NameValue("env", "application/x-envoy"));
		mimeList.add(new NameValue("eps", "application/postscript"));
		mimeList.add(new NameValue("es", "application/x-esrehber"));
		mimeList.add(new NameValue("etx", "text/plain"));
		mimeList.add(new NameValue("evy", "application/x-envoy"));
		mimeList.add(new NameValue("exe", "application/octet-stream"));
		mimeList.add(new NameValue("f", "text/plain"));
		mimeList.add(new NameValue("f77", "text/plain"));
		mimeList.add(new NameValue("f90", "text/plain"));
		mimeList.add(new NameValue("fdf", "application/vnd.fdf"));
		mimeList.add(new NameValue("fif", "image/fif"));
		mimeList.add(new NameValue("fli", "video/fli"));
		mimeList.add(new NameValue("flo", "image/florian"));
		mimeList.add(new NameValue("flx", "text/vnd.fmi.flexstor"));
		mimeList.add(new NameValue("fmf", "video/x-atomic3d-feature"));
		mimeList.add(new NameValue("for", "text/plain"));
		mimeList.add(new NameValue("fpx", "image/vnd.fpx"));
		mimeList.add(new NameValue("frl", "application/freeloader"));
		mimeList.add(new NameValue("funk", "audio/make"));
		mimeList.add(new NameValue("g", "text/plain"));
		mimeList.add(new NameValue("g3", "image/g3fax"));
		mimeList.add(new NameValue("gif", "image/gif"));
		mimeList.add(new NameValue("gl", "video/gl"));
		mimeList.add(new NameValue("gsd", "audio/x-gsm"));
		mimeList.add(new NameValue("gsm", "audio/x-gsm"));
		mimeList.add(new NameValue("gsp", "application/x-gsp"));
		mimeList.add(new NameValue("gss", "application/x-gss"));
		mimeList.add(new NameValue("gtar", "application/x-gtar"));
		mimeList.add(new NameValue("gz", "application/x-gzip"));
		mimeList.add(new NameValue("gzip", "application/x-gzip"));
		mimeList.add(new NameValue("h", "text/plain"));
		mimeList.add(new NameValue("hdf", "application/x-hdf"));
		mimeList.add(new NameValue("help", "application/x-helpfile"));
		mimeList.add(new NameValue("hgl", "application/vnd.hp-hpgl"));
		mimeList.add(new NameValue("hh", "text/plain"));
		mimeList.add(new NameValue("hlb", "text/plain"));
		mimeList.add(new NameValue("hlp", "application/x-winhelp"));
		mimeList.add(new NameValue("hpg", "application/vnd.hp-hpgl"));
		mimeList.add(new NameValue("hpgl", "application/vnd.hp-hpgl"));
		mimeList.add(new NameValue("hqx", "application/binhex"));
		mimeList.add(new NameValue("hta", "application/hta"));
		mimeList.add(new NameValue("htc", "text/x-component"));
		mimeList.add(new NameValue("htm", "text/html"));
		mimeList.add(new NameValue("html", "text/html"));
		mimeList.add(new NameValue("htmls", "text/html"));
		mimeList.add(new NameValue("htt", "text/webviewhtml"));
		mimeList.add(new NameValue("htx", "text/html"));
		mimeList.add(new NameValue("ice", "x-conference/x-cooltalk"));
		mimeList.add(new NameValue("ico", "image/x-icon"));
		mimeList.add(new NameValue("idc", "text/plain"));
		mimeList.add(new NameValue("ief", "image/ief"));
		mimeList.add(new NameValue("iefs", "image/ief"));
		mimeList.add(new NameValue("iges", "model/iges"));
		mimeList.add(new NameValue("igs", "model/iges"));
		mimeList.add(new NameValue("ima", "application/x-ima"));
		mimeList.add(new NameValue("imap", "application/x-httpd-imap"));
		mimeList.add(new NameValue("inf", "application/inf"));
		mimeList.add(new NameValue("ins", "application/x-internett-signup"));
		mimeList.add(new NameValue("ip", "application/x-ip2"));
		mimeList.add(new NameValue("isu", "video/x-isvideo"));
		mimeList.add(new NameValue("it", "audio/it"));
		mimeList.add(new NameValue("iv", "application/x-inventor"));
		mimeList.add(new NameValue("ivr", "i-world/i-vrml"));
		mimeList.add(new NameValue("ivy", "application/x-livescreen"));
		mimeList.add(new NameValue("jam", "audio/x-jam"));
		mimeList.add(new NameValue("jav", "text/plain"));
		mimeList.add(new NameValue("java", "text/plain"));
		mimeList.add(new NameValue("jcm", "application/x-java-commerce"));
		mimeList.add(new NameValue("jfif", "image/jpeg"));
		mimeList.add(new NameValue("jfif-tbnl", "image/jpeg"));
		mimeList.add(new NameValue("jpe", "image/jpeg"));
		mimeList.add(new NameValue("jpeg", "image/jpeg"));
		mimeList.add(new NameValue("jpg", "image/jpeg"));
		mimeList.add(new NameValue("jps", "image/x-jps"));
		mimeList.add(new NameValue("js", "application/x-javascript"));
		mimeList.add(new NameValue("jut", "image/jutvision"));
		mimeList.add(new NameValue("kar", "audio/midi"));
		mimeList.add(new NameValue("ksh", "application/x-ksh"));
		mimeList.add(new NameValue("la", "audio/x-nspaudio"));
		mimeList.add(new NameValue("lam", "audio/x-liveaudio"));
		mimeList.add(new NameValue("latex", "application/x-latex"));
		mimeList.add(new NameValue("lha", "application/x-lha"));
		mimeList.add(new NameValue("list", "text/plain"));
		mimeList.add(new NameValue("lma", "audio/x-nspaudio"));
		mimeList.add(new NameValue("log", "text/plain"));
		mimeList.add(new NameValue("lsp", "application/x-lisp"));
		mimeList.add(new NameValue("lst", "text/plain"));
		mimeList.add(new NameValue("lsx", "text/x-la-asf"));
		mimeList.add(new NameValue("ltx", "application/x-latex"));
		mimeList.add(new NameValue("lzh", "application/x-lzh"));
		mimeList.add(new NameValue("lzx", "application/x-lzx"));
		mimeList.add(new NameValue("m", "text/plain"));
		mimeList.add(new NameValue("m1v", "video/mpeg"));
		mimeList.add(new NameValue("m2a", "audio/mpeg"));
		mimeList.add(new NameValue("m2v", "video/mpeg"));
		mimeList.add(new NameValue("m3u", "audio/x-mpequrl"));
		mimeList.add(new NameValue("man", "application/x-troff-man"));
		mimeList.add(new NameValue("map", "application/x-navimap"));
		mimeList.add(new NameValue("mar", "text/plain"));
		mimeList.add(new NameValue("mbd", "application/mbedlet"));
		mimeList.add(new NameValue("mc$", "application/x-magic-cap-package-1.0"));
		mimeList.add(new NameValue("mcd", "application/x-mathcad"));
		mimeList.add(new NameValue("mcf", "text/plain"));
		mimeList.add(new NameValue("mcp", "application/netmc"));
		mimeList.add(new NameValue("me", "application/x-troff-me"));
		mimeList.add(new NameValue("mht", "message/rfc822"));
		mimeList.add(new NameValue("mhtml", "message/rfc822"));
		mimeList.add(new NameValue("mid", "audio/midi"));
		mimeList.add(new NameValue("midi", "audio/midi"));
		mimeList.add(new NameValue("mif", "application/x-mif"));
		mimeList.add(new NameValue("mime", "www/mime"));
		mimeList.add(new NameValue("mjf", "audio/x-vnd.audioexplosion.mjuicemediafile"));
		mimeList.add(new NameValue("mjpg", "video/x-motion-jpeg"));
		mimeList.add(new NameValue("mm", "application/base64"));
		mimeList.add(new NameValue("mme", "application/base64"));
		mimeList.add(new NameValue("mod", "audio/x-mod"));
		mimeList.add(new NameValue("moov", "video/quicktime"));
		mimeList.add(new NameValue("mov", "video/quicktime"));
		mimeList.add(new NameValue("movie", "video/x-sgi-movie"));
		mimeList.add(new NameValue("mp2", "audio/mpeg"));
		mimeList.add(new NameValue("mp3", "audio/mpeg3"));
		mimeList.add(new NameValue("mp4", "video/mp4"));
		mimeList.add(new NameValue("mpa", "audio/mpeg"));
		mimeList.add(new NameValue("mpc", "application/x-project"));
		mimeList.add(new NameValue("mpe", "video/mpeg"));
		mimeList.add(new NameValue("mpeg", "video/mpeg"));
		mimeList.add(new NameValue("mpg", "video/mpeg"));
		mimeList.add(new NameValue("mpga", "audio/mpeg"));
		mimeList.add(new NameValue("mpp", "application/vnd.ms-project"));
		mimeList.add(new NameValue("mpt", "application/x-project"));
		mimeList.add(new NameValue("mpv", "application/x-project"));
		mimeList.add(new NameValue("mpx", "application/x-project"));
		mimeList.add(new NameValue("mrc", "application/marc"));
		mimeList.add(new NameValue("ms", "application/x-troff-ms"));
		mimeList.add(new NameValue("mv", "video/x-sgi-movie"));
		mimeList.add(new NameValue("my", "audio/make"));
		mimeList.add(new NameValue("mzz", "application/x-vnd.audioexplosion.mzz"));
		mimeList.add(new NameValue("nap", "image/naplps"));
		mimeList.add(new NameValue("naplps", "image/naplps"));
		mimeList.add(new NameValue("nc", "application/x-netcdf"));
		mimeList.add(new NameValue("ncm", "application/vnd.nokia.configuration-message"));
		mimeList.add(new NameValue("nif", "image/x-niff"));
		mimeList.add(new NameValue("niff", "image/x-niff"));
		mimeList.add(new NameValue("nix", "application/x-mix-transfer"));
		mimeList.add(new NameValue("nsc", "application/x-conference"));
		mimeList.add(new NameValue("nvd", "application/x-navidoc"));
		mimeList.add(new NameValue("o", "application/octet-stream"));
		mimeList.add(new NameValue("oda", "application/oda"));
		mimeList.add(new NameValue("omc", "application/x-omc"));
		mimeList.add(new NameValue("omcd", "application/x-omcdatamaker"));
		mimeList.add(new NameValue("omcr", "application/x-omcregerator"));
		mimeList.add(new NameValue("p", "text/plain"));
		mimeList.add(new NameValue("p10", "application/x-pkcs10"));
		mimeList.add(new NameValue("p12", "application/x-pkcs12"));
		mimeList.add(new NameValue("p7a", "application/x-pkcs7-signature"));
		mimeList.add(new NameValue("p7c", "application/x-pkcs7-mime"));
		mimeList.add(new NameValue("p7m", "application/x-pkcs7-mime"));
		mimeList.add(new NameValue("p7r", "application/x-pkcs7-certreqresp"));
		mimeList.add(new NameValue("p7s", "application/pkcs7-signature"));
		mimeList.add(new NameValue("part", "application/pro_eng"));
		mimeList.add(new NameValue("pas", "text/plain"));
		mimeList.add(new NameValue("pbm", "image/x-portable-bitmap"));
		mimeList.add(new NameValue("pcl", "application/x-pcl"));
		mimeList.add(new NameValue("pct", "image/x-pict"));
		mimeList.add(new NameValue("pcx", "image/x-pcx"));
		mimeList.add(new NameValue("pdb", "chemical/x-pdb"));
		mimeList.add(new NameValue("pdf", "application/pdf"));
		mimeList.add(new NameValue("pfunk", "audio/make"));
		mimeList.add(new NameValue("pgm", "image/x-portable-graymap"));
		mimeList.add(new NameValue("pic", "image/pict"));
		mimeList.add(new NameValue("pict", "image/pict"));
		mimeList.add(new NameValue("pkg", "application/x-newton-compatible-pkg"));
		mimeList.add(new NameValue("pko", "application/vnd.ms-pki.pko"));
		mimeList.add(new NameValue("pl", "text/plain"));
		mimeList.add(new NameValue("plx", "application/x-pixclscript"));
		mimeList.add(new NameValue("pm", "text/plain"));
		mimeList.add(new NameValue("pm4", "application/x-pagemaker"));
		mimeList.add(new NameValue("pm5", "application/x-pagemaker"));
		mimeList.add(new NameValue("png", "image/png"));
		mimeList.add(new NameValue("pnm", "image/x-portable-anymap"));
		mimeList.add(new NameValue("pot", "application/mspowerpoint"));
		mimeList.add(new NameValue("pov", "model/x-pov"));
		mimeList.add(new NameValue("ppa", "application/vnd.ms-powerpoint"));
		mimeList.add(new NameValue("ppm", "image/x-portable-pixmap"));
		mimeList.add(new NameValue("pps", "application/mspowerpoint"));
		mimeList.add(new NameValue("ppt", "application/mspowerpoint"));
		mimeList.add(new NameValue("ppz", "application/mspowerpoint"));
		mimeList.add(new NameValue("pre", "application/x-freelance"));
		mimeList.add(new NameValue("prt", "application/pro_eng"));
		mimeList.add(new NameValue("ps", "application/postscript"));
		mimeList.add(new NameValue("psd", "application/octet-stream"));
		mimeList.add(new NameValue("pvu", "paleovu/x-pv"));
		mimeList.add(new NameValue("pwz", "application/vnd.ms-powerpoint"));
		mimeList.add(new NameValue("py", "text/plain"));
		mimeList.add(new NameValue("pyc", "applicaiton/x-bytecode.python"));
		mimeList.add(new NameValue("qcp", "audio/vnd.qcelp"));
		mimeList.add(new NameValue("qd3", "x-world/x-3dmf"));
		mimeList.add(new NameValue("qd3d", "x-world/x-3dmf"));
		mimeList.add(new NameValue("qif", "image/x-quicktime"));
		mimeList.add(new NameValue("qt", "video/quicktime"));
		mimeList.add(new NameValue("qtc", "video/x-qtc"));
		mimeList.add(new NameValue("qti", "image/x-quicktime"));
		mimeList.add(new NameValue("qtif", "image/x-quicktime"));
		mimeList.add(new NameValue("ra", "audio/x-realaudio"));
		mimeList.add(new NameValue("ram", "audio/x-pn-realaudio"));
		mimeList.add(new NameValue("ras", "image/cmu-raster"));
		mimeList.add(new NameValue("rast", "image/cmu-raster"));
		mimeList.add(new NameValue("rexx", "text/plain"));
		mimeList.add(new NameValue("rf", "image/vnd.rn-realflash"));
		mimeList.add(new NameValue("rgb", "image/x-rgb"));
		mimeList.add(new NameValue("rm", "audio/x-pn-realaudio"));
		mimeList.add(new NameValue("rmi", "audio/mid"));
		mimeList.add(new NameValue("rmm", "audio/x-pn-realaudio"));
		mimeList.add(new NameValue("rmp", "audio/x-pn-realaudio"));
		mimeList.add(new NameValue("rng", "application/ringing-tones"));
		mimeList.add(new NameValue("rnx", "application/vnd.rn-realplayer"));
		mimeList.add(new NameValue("roff", "application/x-troff"));
		mimeList.add(new NameValue("rp", "image/vnd.rn-realpix"));
		mimeList.add(new NameValue("rpm", "audio/x-pn-realaudio-plugin"));
		mimeList.add(new NameValue("rt", "text/richtext"));
		mimeList.add(new NameValue("rtf", "text/richtext"));
		mimeList.add(new NameValue("rtx", "text/richtext"));
		mimeList.add(new NameValue("rv", "video/vnd.rn-realvideo"));
		mimeList.add(new NameValue("s", "text/plain"));
		mimeList.add(new NameValue("s3m", "audio/s3m"));
		mimeList.add(new NameValue("saveme", "application/octet-stream"));
		mimeList.add(new NameValue("sbk", "application/x-tbook"));
		mimeList.add(new NameValue("scm", "video/x-scm"));
		mimeList.add(new NameValue("sdml", "text/plain"));
		mimeList.add(new NameValue("sdp", "application/x-sdp"));
		mimeList.add(new NameValue("sdr", "application/sounder"));
		mimeList.add(new NameValue("sea", "application/x-sea"));
		mimeList.add(new NameValue("set", "application/set"));
		mimeList.add(new NameValue("sgm", "text/x-sgml"));
		mimeList.add(new NameValue("sgml", "text/x-sgml"));
		mimeList.add(new NameValue("sh", "application/x-sh"));
		mimeList.add(new NameValue("shar", "application/x-shar"));
		mimeList.add(new NameValue("shtml", "text/html"));
		mimeList.add(new NameValue("sid", "audio/x-psid"));
		mimeList.add(new NameValue("sit", "application/x-stuffit"));
		mimeList.add(new NameValue("skd", "application/x-koan"));
		mimeList.add(new NameValue("skm", "application/x-koan"));
		mimeList.add(new NameValue("skp", "application/x-koan"));
		mimeList.add(new NameValue("skt", "application/x-koan"));
		mimeList.add(new NameValue("sl", "application/x-seelogo"));
		mimeList.add(new NameValue("smi", "application/smil"));
		mimeList.add(new NameValue("smil", "application/smil"));
		mimeList.add(new NameValue("snd", "audio/basic"));
		mimeList.add(new NameValue("sol", "application/solids"));
		mimeList.add(new NameValue("spc", "text/x-speech"));
		mimeList.add(new NameValue("spl", "application/futuresplash"));
		mimeList.add(new NameValue("spr", "application/x-sprite"));
		mimeList.add(new NameValue("sprite", "application/x-sprite"));
		mimeList.add(new NameValue("src", "application/x-wais-source"));
		mimeList.add(new NameValue("ssi", "text/x-server-parsed-html"));
		mimeList.add(new NameValue("ssm", "application/streamingmedia"));
		mimeList.add(new NameValue("sst", "application/vnd.ms-pki.certstore"));
		mimeList.add(new NameValue("step", "application/step"));
		mimeList.add(new NameValue("stl", "application/x-navistyle"));
		mimeList.add(new NameValue("stp", "application/step"));
		mimeList.add(new NameValue("sv4cpio", "application/x-sv4cpio"));
		mimeList.add(new NameValue("sv4crc", "application/x-sv4crc"));
		mimeList.add(new NameValue("svf", "image/x-dwg"));
		mimeList.add(new NameValue("svr", "application/x-world"));
		mimeList.add(new NameValue("swf", "application/x-shockwave-flash"));
		mimeList.add(new NameValue("t", "application/x-troff"));
		mimeList.add(new NameValue("talk", "text/x-speech"));
		mimeList.add(new NameValue("tar", "application/x-tar"));
		mimeList.add(new NameValue("tbk", "application/x-tbook"));
		mimeList.add(new NameValue("tcl", "text/plain"));
		mimeList.add(new NameValue("tcsh", "text/plain"));
		mimeList.add(new NameValue("tex", "application/x-tex"));
		mimeList.add(new NameValue("texi", "application/x-texinfo"));
		mimeList.add(new NameValue("texinfo", "application/x-texinfo"));
		mimeList.add(new NameValue("text", "text/plain"));
		mimeList.add(new NameValue("tgz", "application/x-compressed"));
		mimeList.add(new NameValue("tif", "image/tiff"));
		mimeList.add(new NameValue("tiff", "image/tiff"));
		mimeList.add(new NameValue("tr", "application/x-troff"));
		mimeList.add(new NameValue("tsi", "audio/tsp-audio"));
		mimeList.add(new NameValue("tsp", "application/dsptype"));
		mimeList.add(new NameValue("tsv", "text/tab-separated-values"));
		mimeList.add(new NameValue("turbot", "image/florian"));
		mimeList.add(new NameValue("txt", "text/plain"));
		mimeList.add(new NameValue("uil", "text/x-uil"));
		mimeList.add(new NameValue("uni", "text/uri-list"));
		mimeList.add(new NameValue("unis", "text/uri-list"));
		mimeList.add(new NameValue("unv", "application/i-deas"));
		mimeList.add(new NameValue("uri", "text/uri-list"));
		mimeList.add(new NameValue("uris", "text/uri-list"));
		mimeList.add(new NameValue("ustar", "application/x-ustar"));
		mimeList.add(new NameValue("uu", "text/x-uuencode"));
		mimeList.add(new NameValue("uue", "text/x-uuencode"));
		mimeList.add(new NameValue("vcd", "application/x-cdlink"));
		mimeList.add(new NameValue("vcs", "text/x-vcalendar"));
		mimeList.add(new NameValue("vda", "application/vda"));
		mimeList.add(new NameValue("vdo", "video/vdo"));
		mimeList.add(new NameValue("vew", "application/groupwise"));
		mimeList.add(new NameValue("viv", "video/vivo"));
		mimeList.add(new NameValue("vivo", "video/vivo"));
		mimeList.add(new NameValue("vmd", "application/vocaltec-media-desc"));
		mimeList.add(new NameValue("vmf", "application/vocaltec-media-file"));
		mimeList.add(new NameValue("voc", "audio/voc"));
		mimeList.add(new NameValue("vos", "video/vosaic"));
		mimeList.add(new NameValue("vox", "audio/voxware"));
		mimeList.add(new NameValue("vqe", "audio/x-twinvq-plugin"));
		mimeList.add(new NameValue("vqf", "audio/x-twinvq"));
		mimeList.add(new NameValue("vql", "audio/x-twinvq-plugin"));
		mimeList.add(new NameValue("vrml", "model/vrml"));
		mimeList.add(new NameValue("vrt", "x-world/x-vrt"));
		mimeList.add(new NameValue("vsd", "application/x-visio"));
		mimeList.add(new NameValue("vst", "application/x-visio"));
		mimeList.add(new NameValue("vsw", "application/x-visio"));
		mimeList.add(new NameValue("w60", "application/wordperfect6.0"));
		mimeList.add(new NameValue("w61", "application/wordperfect6.1"));
		mimeList.add(new NameValue("w6w", "application/msword"));
		mimeList.add(new NameValue("wav", "audio/wav"));
		mimeList.add(new NameValue("wb1", "application/x-qpro"));
		mimeList.add(new NameValue("wbmp", "image/vnd.wap.wbmp"));
		mimeList.add(new NameValue("web", "application/vnd.xara"));
		mimeList.add(new NameValue("wiz", "application/msword"));
		mimeList.add(new NameValue("wk1", "application/x-123"));
		mimeList.add(new NameValue("wmf", "windows/metafile"));
		mimeList.add(new NameValue("wml", "text/vnd.wap.wml"));
		mimeList.add(new NameValue("wmlc", "application/vnd.wap.wmlc"));
		mimeList.add(new NameValue("wmls", "text/vnd.wap.wmlscript"));
		mimeList.add(new NameValue("wmlsc", "application/vnd.wap.wmlscriptc"));
		mimeList.add(new NameValue("word", "application/msword"));
		mimeList.add(new NameValue("wp", "application/wordperfect"));
		mimeList.add(new NameValue("wp5", "application/wordperfect"));
		mimeList.add(new NameValue("wp6", "application/wordperfect"));
		mimeList.add(new NameValue("wpd", "application/wordperfect"));
		mimeList.add(new NameValue("wq1", "application/x-lotus"));
		mimeList.add(new NameValue("wri", "application/mswrite"));
		mimeList.add(new NameValue("wrl", "model/vrml"));
		mimeList.add(new NameValue("wrz", "model/vrml"));
		mimeList.add(new NameValue("wsc", "text/scriplet"));
		mimeList.add(new NameValue("wsrc", "application/x-wais-source"));
		mimeList.add(new NameValue("wtk", "application/x-wintalk"));
		mimeList.add(new NameValue("xbm", "image/xbm"));
		mimeList.add(new NameValue("xdr", "video/x-amt-demorun"));
		mimeList.add(new NameValue("xgz", "xgl/drawing"));
		mimeList.add(new NameValue("xif", "image/vnd.xiff"));
		mimeList.add(new NameValue("xl", "application/excel"));
		mimeList.add(new NameValue("xla", "application/excel"));
		mimeList.add(new NameValue("xlb", "application/excel"));
		mimeList.add(new NameValue("xlc", "application/excel"));
		mimeList.add(new NameValue("xld", "application/excel"));
		mimeList.add(new NameValue("xlk", "application/excel"));
		mimeList.add(new NameValue("xll", "application/excel"));
		mimeList.add(new NameValue("xlm", "application/excel"));
		mimeList.add(new NameValue("xls", "application/excel"));
		mimeList.add(new NameValue("xlt", "application/excel"));
		mimeList.add(new NameValue("xlv", "application/excel"));
		mimeList.add(new NameValue("xlw", "application/excel"));
		mimeList.add(new NameValue("xm", "audio/xm"));
		mimeList.add(new NameValue("xml", "text/xml"));
		mimeList.add(new NameValue("xmz", "xgl/movie"));
		mimeList.add(new NameValue("xpix", "application/x-vnd.ls-xpix"));
		mimeList.add(new NameValue("xpm", "image/xpm"));
		mimeList.add(new NameValue("x-png", "image/png"));
		mimeList.add(new NameValue("xsr", "video/x-amt-showrun"));
		mimeList.add(new NameValue("xwd", "image/x-xwd"));
		mimeList.add(new NameValue("xyz", "chemical/x-pdb"));
		mimeList.add(new NameValue("z", "application/x-compressed"));
		mimeList.add(new NameValue("zip", "application/x-compressed"));
		mimeList.add(new NameValue("zoo", "application/octet-stream"));
		mimeList.add(new NameValue("zsh", "text/plain"));

		for (NameValue item : mimeList) {
			String filename = "OutilsCommun." + item.getName();

			outils.commun.OutilsCommun.getMIME(filename);
		}
	}
}
