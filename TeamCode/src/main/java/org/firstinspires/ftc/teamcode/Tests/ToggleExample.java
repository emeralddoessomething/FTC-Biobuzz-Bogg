package org.firstinspires.ftc.teamcode.Tests;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Utils.Toggle;

@TeleOp(name = "Toggle Example", group = "Tests")
@Disabled
public class ToggleExample extends OpMode {
    private Toggle exampleToggle = new Toggle();

    @Override
    public void init() {
        // you can also put exampleToggle = new Toggle(); here as well, but not in both places
    }

    @Override
    public void loop() {
        // Call this update method each loop with the boolean input acting as the toggle, in this case, the a button on the gamepad
        exampleToggle.update(gamepad1.a);

        telemetry.addLine("Toggle State: " + exampleToggle.isEnabled());
    }
}
