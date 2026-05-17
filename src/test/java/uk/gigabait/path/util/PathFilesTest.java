package uk.gigabait.path.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PathFilesTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void relativePathUsesBaseDirectory() throws IOException {
        File base = folder.newFolder("base");

        assertEquals(new File(base, "menus"), PathFiles.resolve(base, "menus"));
    }

    @Test
    public void absoluteDirectoryRemainsAbsolute() throws IOException {
        File base = folder.newFolder("base");
        File absolute = folder.newFolder("absolute");
        List<File> missing = new ArrayList<>();

        List<File> directories = PathFiles.directories(base, Arrays.asList(absolute.getAbsolutePath()), missing::add);

        assertEquals(1, directories.size());
        assertEquals(absolute.getCanonicalFile(), directories.get(0));
        assertTrue(missing.isEmpty());
    }

    @Test
    public void missingDirectoryIsReportedAndSkipped() throws IOException {
        File base = folder.newFolder("base");
        List<File> missing = new ArrayList<>();

        List<File> directories = PathFiles.directories(base, Arrays.asList("missing"), missing::add);

        assertTrue(directories.isEmpty());
        assertEquals(1, missing.size());
    }

    @Test
    public void ymlFilesAreCollectedRecursively() throws IOException {
        File root = folder.newFolder("root");
        folder.newFile("root/menu.yml");
        folder.newFile("root/notes.txt");
        File nested = folder.newFolder("root", "nested");
        File template = new File(nested, "template.yml");
        assertTrue(template.createNewFile());

        Set<String> names = new HashSet<>();
        for (File file : PathFiles.ymlFiles(root)) {
            names.add(file.getName());
        }

        assertEquals(new HashSet<>(Arrays.asList("menu.yml", "template.yml")), names);
    }
}
