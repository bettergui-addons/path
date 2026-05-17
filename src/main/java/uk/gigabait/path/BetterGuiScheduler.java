package uk.gigabait.path;

import me.hsgamer.bettergui.util.SchedulerUtil;

final class BetterGuiScheduler {
    private BetterGuiScheduler() {
    }

    static void runNextTick(Runnable task) {
        SchedulerUtil.global().run(task);
    }
}
