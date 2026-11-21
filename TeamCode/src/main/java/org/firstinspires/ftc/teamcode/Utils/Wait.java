package org.firstinspires.ftc.teamcode.Utils;

import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Creates easy non-blocking 'waits' that return false until a set duration in seconds has elapsed, then return true.
 */
public class Wait {
    private final ElapsedTime timer = new ElapsedTime();
    private double targetDuration = -1;

    /**
     * Waits a certain amount of time. If called repeatedly while waiting, returns false.
     * Once the time has elapsed, resets its internal state and returns true.
     * @param duration Time to wait in seconds
     * @return true if done waiting, false if still waiting
     */
    public boolean waitSeconds(double duration) {
        if (targetDuration != duration) { // If this is a new timer request or duration changed
            timer.reset();
            // Bumps targetDuration to passed duration so calling repeatedly won't reset timer
            targetDuration = duration;
        }

        if (timer.seconds() >= targetDuration) {
            targetDuration = -1; // Reset targetDuration so a new wait can be started
            return true;
        }

        return false;
    }

    /**
     * Explicitly resets the timer state.
     */
    public void reset() {
        targetDuration = -1;
        timer.reset();
    }

    /**
     * Returns elapsed time in seconds since the current wait started.
     */
    public double seconds() {
        return timer.seconds();
    }

    /**
     * Returns remaining time in seconds for the current wait.
     */
    public double timeRemaining() {
        if (targetDuration < 0) return 0;
        return Math.max(0, targetDuration - timer.seconds());
    }

    /**
     * Checks if the timer is currently active/waiting.
     */
    public boolean isWaiting() {
        return targetDuration >= 0;
    }
}

