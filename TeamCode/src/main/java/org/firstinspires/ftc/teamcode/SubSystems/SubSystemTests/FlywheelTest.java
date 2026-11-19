package org.firstinspires.ftc.teamcode.SubSystems.SubSystemTests;

import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.SubSystems.Intake;
import org.firstinspires.ftc.teamcode.SubSystems.Shooter;
import org.firstinspires.ftc.teamcode.SubSystems.Transfer;
import org.firstinspires.ftc.teamcode.Utils.Toggle;

//@Disabled
@TeleOp(name = "Flywheel Test", group = "Tests")
public class FlywheelTest extends OpMode {
    Shooter shooter;
    JoinedTelemetry joinedTelemetry;
    DcMotor bl;
    DcMotor br;
    DcMotor fl;
    DcMotor fr;

    Toggle transferToggle = new Toggle();
    Transfer transfer;
    Intake intake;

    @Override
    public void init() {
        shooter = new Shooter(hardwareMap);
        joinedTelemetry = new JoinedTelemetry(PanelsTelemetry.INSTANCE.getFtcTelemetry(), telemetry);

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

        shooter.setTargetRpm(10);

        transfer = new Transfer(hardwareMap);

        intake = new Intake(hardwareMap);
    }

    @Override
    public void loop() {
        shooter.update();
        shooter.updateControllerConstants();

        joinedTelemetry.addLine("Current RPM: " + shooter.getCurrentRpm());
        joinedTelemetry.addLine("Target RPM: " + shooter.getTargetRpm());
        joinedTelemetry.addLine("Is Ready? " + shooter.isReady());
        joinedTelemetry.update();

        if(gamepad1.rightBumperWasPressed()) {
            shooter.bumpRpm(100);
        }

        if(gamepad1.leftBumperWasPressed()) {
            shooter.bumpRpm(-100);
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
    }
}