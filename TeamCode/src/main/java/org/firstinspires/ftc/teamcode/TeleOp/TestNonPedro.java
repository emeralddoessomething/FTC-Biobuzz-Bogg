package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.SubSystems.Intake;
import org.firstinspires.ftc.teamcode.SubSystems.Shooter;
import org.firstinspires.ftc.teamcode.Utils.Toggle;

@TeleOp(name = "Test Non-Pedro", group = "TeleOp")
public class TestNonPedro extends OpMode {
    DcMotor bl;
    DcMotor br;
    DcMotor fl;
    DcMotor fr;

    Intake intake;
    Toggle intakeToggle;
    Shooter shooter;

    @Override
    public void init() {
        bl = hardwareMap.get(DcMotor.class, "bl");
        br = hardwareMap.get(DcMotor.class, "br");
        fl = hardwareMap.get(DcMotor.class, "fl");
        fr = hardwareMap.get(DcMotor.class, "fr");

        bl.setDirection(DcMotorSimple.Direction.FORWARD);
        br.setDirection(DcMotorSimple.Direction.REVERSE);
        fl.setDirection(DcMotorSimple.Direction.FORWARD);
        fr.setDirection(DcMotorSimple.Direction.REVERSE);

        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intake = new Intake(hardwareMap);
        intakeToggle = new Toggle();

        shooter = new Shooter(hardwareMap);
        shooter.setTargetRpm(2000);
    }

    @Override
    public void loop() {
//        shooter.update();
//        shooter.updateControllerConstants();

//        intakeToggle.update(gamepad1.rightTriggerWasPressed());
//
//        if(intakeToggle.isEnabled()) {
//            intake.setState(Intake.State.INTAKE);
//        } else {
//            intake.setState(Intake.State.STOP);
//        }

        if(gamepad1.right_trigger > 0.0) {
            intake.setState(Intake.State.INTAKE);
        } else if(gamepad1.left_trigger > 0.0) {
            intake.setState(Intake.State.OUTTAKE);
        } else {
            intake.setState(Intake.State.STOP);
        }

        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x * 1.1;
        double rx = gamepad1.right_stick_x;

        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (y + x + rx) / denominator;
        double backLeftPower = (y - x + rx) / denominator;
        double frontRightPower = (y - x - rx) / denominator;
        double backRightPower = (y + x - rx) / denominator;

        fl.setPower(frontLeftPower);
        bl.setPower(backLeftPower);
        fr.setPower(frontRightPower);
        br.setPower(backRightPower);

        telemetry.addLine("Intake state: " + intake.getState().toString());

        telemetry.addLine("Current RPM: " + shooter.getCurrentRpm());
        telemetry.addLine("Target RPM: " + shooter.getTargetRpm());
        telemetry.addLine("Is Ready? " + shooter.isReady());

        if(gamepad1.rightBumperWasPressed()) {
            shooter.bumpRpm(100);
        }

        if(gamepad1.leftBumperWasPressed()) {
            shooter.bumpRpm(-100);
        }
    }
}
