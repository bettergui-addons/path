package uk.gigabait.path;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PathRegistryTest {
    @Test
    public void avoidsLegacyBukkitScheduler() throws IOException {
        String source = new String(
                Files.readAllBytes(Paths.get("src/main/java/uk/gigabait/path/PathRegistry.java")),
                StandardCharsets.UTF_8);

        assertFalse(source.contains("getScheduler().runTask"));
        assertFalse(source.contains("Bukkit.getScheduler"));
    }

    @Test
    public void coalescesPendingRegistrationRequests() {
        List<Runnable> scheduled = new ArrayList<>();
        AtomicInteger runs = new AtomicInteger();

        PathRegistry.schedule(scheduled::add, runs::incrementAndGet, exception -> {
            throw new AssertionError(exception);
        });
        PathRegistry.schedule(scheduled::add, runs::incrementAndGet, exception -> {
            throw new AssertionError(exception);
        });

        assertEquals(1, scheduled.size());

        scheduled.get(0).run();

        assertEquals(1, runs.get());

        PathRegistry.schedule(scheduled::add, runs::incrementAndGet, exception -> {
            throw new AssertionError(exception);
        });

        assertEquals(2, scheduled.size());
        scheduled.get(1).run();
    }

    @Test
    public void releasesPendingRegistrationAfterFailure() {
        List<Runnable> scheduled = new ArrayList<>();
        List<Exception> errors = new ArrayList<>();

        PathRegistry.schedule(scheduled::add, () -> {
            throw new IllegalStateException("boom");
        }, errors::add);

        scheduled.get(0).run();

        assertEquals(1, errors.size());

        PathRegistry.schedule(scheduled::add, () -> {
        }, errors::add);

        assertEquals(2, scheduled.size());
    }
}
