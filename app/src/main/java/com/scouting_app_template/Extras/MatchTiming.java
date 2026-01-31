package com.scouting_app_template.Extras;

import static com.scouting_app_template.MainActivity.autonLengthMs;
import static com.scouting_app_template.MainActivity.context;
import static com.scouting_app_template.MainActivity.teleopLengthMs;
import static com.scouting_app_template.MainActivity.timeBufferMs;

import com.scouting_app_template.MainActivity;

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
    public static void scheduleRunAfterAuto(Runnable task) {
        long timeToWait = autonLengthMs + timeBufferMs
                - (Calendar.getInstance(Locale.US).getTimeInMillis() - ((MainActivity)context).auton.getAutonStart());

        scheduler.schedule(runWithUpdate(task), timeToWait, TimeUnit.MILLISECONDS);
    }

    /**
     * calculates the length of teleop and the buffer, but subtracts the
     * amount of time that has already passed (current time - when teleop started)
     * @param task What you want to be run when teleop ends
     */
    public static void scheduleRunAfterTeleop(Runnable task) {
        long timeToWait = teleopLengthMs + timeBufferMs
                - (Calendar.getInstance(Locale.US).getTimeInMillis() - ((MainActivity)context).teleop.getTeleopStart());

        scheduler.schedule(runWithUpdate(task), timeToWait, TimeUnit.MILLISECONDS);
    }

    private static Runnable runWithUpdate(Runnable task) {
        return () -> {
            task.run();
            ((MainActivity)context).updateFragments();
        };
    }
}
