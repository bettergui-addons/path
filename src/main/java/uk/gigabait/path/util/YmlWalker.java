package uk.gigabait.path.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class YmlWalker {

    public static List<File> walk(File root) {
        List<File> files = new ArrayList<>();
        collect(root, files);
        return files;
    }

    private static void collect(File dir, List<File> files) {
        if (dir == null || !dir.exists()) return;

        File[] list = dir.listFiles();
        if (list == null) return;

        for (File file : list) {
            if (file.isDirectory()) {
                collect(file, files);
            } else {
                files.add(file);
            }
        }
    }
}
