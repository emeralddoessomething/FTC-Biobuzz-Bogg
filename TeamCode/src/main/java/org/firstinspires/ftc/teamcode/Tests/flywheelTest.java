package org.firstinspires.ftc.teamcode.Tests;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.pedropathing.controllers.Controller;
import com.pedropathing.controllers.PIDController;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.Utils.Toggle;

//@Config

@Configurable
@Disabled
@TeleOp(name = "Flywheel Test", group = "Tests")
public class flywheelTest extends OpMode {
    public static double p = 0;
    public static double i = 0;
    public static double d = 0;
    public static double f = 0;

    public static double targetVelocity = 0;

    DcMotorEx flywheel;
    Controller flywheelController;
    Toggle toggle = new Toggle();

    @Override
    public void init() {
        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        flywheelController = Controller.pid(p, i, d);

//        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        telemetry = new JoinedTelemetry(PanelsTelemetry.INSTANCE.getFtcTelemetry(), telemetry);
        telemetry.setMsTransmissionInterval(500);
    }

    @Override
    public void loop() {
        toggle.update(gamepad1.a);

        if (flywheelController instanceof PIDController) {
            PIDController pid = (PIDController) flywheelController;
            pid.kP = p;
            pid.kI = i;
            pid.kD = d;
        }

        double output = Math.max(0.0, flywheelController.calculate(targetVelocity, targetVelocity - flywheel.getVelocity()));

        if(toggle.isEnabled()) {
            flywheel.setPower(output);
        } else {
            flywheel.setPower(0);
        }

        if(gamepad1.b) {
            flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        } else {
            flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }

        telemetry.addLine("Target: " + targetVelocity);
        telemetry.addLine("Current: " + flywheel.getVelocity());
        telemetry.addLine("Toggle status: " + toggle.isEnabled());
        telemetry.addLine("Controller Output: " + output);
    }
}
