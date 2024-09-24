import java.io.*;
import java.nio.file.*;
import java.util.jar.*;

public class JarGenerator {

    public static void generateJarFile(String jarFileName, String sourceDir) throws IOException {
        // Ensure the source directory exists and is a directory
        Path sourcePath = Paths.get(sourceDir);
        if (!Files.isDirectory(sourcePath)) {
            throw new IllegalArgumentException("Source directory does not exist or is not a directory: " + sourceDir);
        }

        // Set up the output stream for the JAR file
        try (FileOutputStream fos = new FileOutputStream(jarFileName);
             JarOutputStream jos = new JarOutputStream(fos)) {

            // Walk through the source directory and add each class file to the JAR
            Files.walk(sourcePath).forEach(path -> {
                if (Files.isRegularFile(path) && path.toString().endsWith(".class")) {
                    String entryName = sourcePath.relativize(path).toString().replace("\\", "/");
                    try (InputStream is = Files.newInputStream(path)) {
                        JarEntry entry = new JarEntry(entryName);
                        jos.putNextEntry(entry);

                        // Copy file content to the JAR output stream
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            jos.write(buffer, 0, bytesRead);
                        }

                        jos.closeEntry();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void main(String[] args) {
        try {
            String jarFileName = "output.jar";
            String sourceDir = "path/to/class/files";

            generateJarFile(jarFileName, sourceDir);
            System.out.println("JAR file created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
