package org.firstinspires.ftc.teamcode.Utils;

/**
 * Class to make toggles without needing extra variables in main, etc
 */

public class Toggle {
    private boolean lastButtonState = false;
    private boolean state = false;

    /**
     * Update loop for toggles
     * @param currentButtonState Current input state
     */
    public void update(boolean currentButtonState) {
        if (currentButtonState && !lastButtonState) {
            state = !state;
        }
        lastButtonState = currentButtonState;
    }

    /**
     * Returns if the toggle is enabled (true) or disabled (false)
     * @return state
     */
    public boolean isEnabled() {
        return state;
    }

    /**
     * Manually set the state of the toggle
     * @param newState state as a boolean
     */
    public void setState(boolean newState) {
        state = newState;
    }
}