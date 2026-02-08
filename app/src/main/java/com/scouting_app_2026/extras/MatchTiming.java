package com.scouting_app_2026.extras;

import static com.scouting_app_2026.MainActivity.autonLengthMs;
import static com.scouting_app_2026.MainActivity.teleopLengthMs;
import static com.scouting_app_2026.MainActivity.timeBufferMs;

import com.scouting_app_2026.MainActivity;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MatchTiming {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    /**
     * calculates the length of auto and the buffer, but subtracts the
     * amount of time that has already passed (current time - when auton started)
     * @param task What you want run when auton ends
     */
    public static void scheduleRunAfterAuto(Runnable task, MainActivity mainActivity) {
        long timeToWait = autonLengthMs + timeBufferMs
                - (Calendar.getInstance(Locale.US).getTimeInMillis() - mainActivity.auton.getAutonStart());

        scheduler.schedule(runWithUpdate(task, mainActivity), timeToWait, TimeUnit.MILLISECONDS);
    }

    /**
     * calculates the length of teleop and the buffer, but subtracts the
     * amount of time that has already passed (current time - when teleop started)
     * @param task What you want to be run when teleop ends
     */
    public static void scheduleRunAfterTeleop(Runnable task, MainActivity mainActivity) {
        long timeToWait = teleopLengthMs + timeBufferMs
                - (Calendar.getInstance(Locale.US).getTimeInMillis() - mainActivity.teleop.getTeleopStart());

        scheduler.schedule(runWithUpdate(task, mainActivity), timeToWait, TimeUnit.MILLISECONDS);
    }

    private static Runnable runWithUpdate(Runnable task, MainActivity mainActivity) {
        return () -> {
            task.run();
            mainActivity.updateFragments();
        };
    }
}
