package uk.gigabait.path.util;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class PathFiles {
    private PathFiles() {
    }

    public static List<File> directories(File baseFolder, List<String> rawPaths, Consumer<File> missingDirectory) {
        if (rawPaths == null || rawPaths.isEmpty()) {
            return Collections.emptyList();
        }

        return rawPaths.stream()
                .map(rawPath -> directory(baseFolder, rawPath, missingDirectory))
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    public static File resolve(File baseFolder, String path) {
        File file = new File(path);
        if (file.isAbsolute()) {
            return file;
        }
        return new File(baseFolder, path);
    }

    public static List<File> ymlFiles(File directory) {
        return YmlWalker.walk(directory).stream()
                .filter(PathFiles::isYmlFile)
                .collect(Collectors.toList());
    }

    public static String canonicalKey(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException ignored) {
            return file.getAbsolutePath();
        }
    }

    private static File directory(File baseFolder, String rawPath, Consumer<File> missingDirectory) {
        if (rawPath == null) {
            return null;
        }

        String path = rawPath.trim();
        if (path.isEmpty() || path.equalsIgnoreCase("none")) {
            return null;
        }

        File directory = resolve(baseFolder, path);
        if (!directory.isDirectory()) {
            if (missingDirectory != null) {
                missingDirectory.accept(directory);
            }
            return null;
        }

        try {
            return directory.getCanonicalFile();
        } catch (IOException ignored) {
            return directory;
        }
    }

    private static boolean isYmlFile(File file) {
        return file != null && file.isFile() && file.getName().toLowerCase(Locale.ROOT).endsWith(".yml");
    }
}
