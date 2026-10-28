package org.firstinspires.ftc.teamcode.Tests;

import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.SubSystems.Shooter;

@TeleOp(name = "Flywheel Test", group = "Tests")
public class FlywheelTest extends OpMode {
    Shooter shooter;
    JoinedTelemetry joinedTelemetry;

    @Override
    public void init() {
        shooter = new Shooter(hardwareMap);
        joinedTelemetry = new JoinedTelemetry(PanelsTelemetry.INSTANCE.getFtcTelemetry(), telemetry);

        shooter.setTargetRpm(100);
    }

    @Override
    public void loop() {
        shooter.update();

        joinedTelemetry.addLine("Current RPM: " + shooter.getCurrentRpm());
        joinedTelemetry.addLine("Target RPM: " + shooter.getTargetRpm());
        joinedTelemetry.addLine("Is Ready? " + shooter.isReady());

        if(gamepad1.rightBumperWasPressed()) {
            shooter.bumpRpm(10);
        }

        if(gamepad1.leftBumperWasPressed()) {
            shooter.bumpRpm(-10);
        }
    }
}