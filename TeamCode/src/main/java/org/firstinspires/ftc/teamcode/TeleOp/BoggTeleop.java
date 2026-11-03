package org.firstinspires.ftc.teamcode.TeleOp;

import com.bylazar.panels.Panels;
import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Pedro.Constants;
import org.firstinspires.ftc.teamcode.SubSystems.AutoTeleOpTransfer.TransferClass;
import org.firstinspires.ftc.teamcode.SubSystems.Intake;
import org.firstinspires.ftc.teamcode.SubSystems.Shooter;
import org.firstinspires.ftc.teamcode.SubSystems.Transfer;
import org.firstinspires.ftc.teamcode.Utils.DriveUtil;
import org.firstinspires.ftc.teamcode.Utils.HeadingLockV2;
import org.firstinspires.ftc.teamcode.Utils.HtmlTelemetryUtil;
import org.firstinspires.ftc.teamcode.Utils.PanelsFieldDrawing;
import org.firstinspires.ftc.teamcode.Utils.Toggle;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.seattlesolvers.solverslib.util.InterpLUT;
import com.skeletonarmy.marrow.BulkCaching;

@BulkCaching
@TeleOp(name = "Bogg Teleop", group = "TeleOp")
public class BoggTeleop extends OpMode {
    DcMotor bl;
    DcMotor br;
    DcMotor fl;
    DcMotor fr;

    Follower follower;
    Intake intake;
    Shooter shooter;
    Transfer transfer;
    HeadingLockV2 headingLock;

    Toggle headingLockToggle = new Toggle();

    InterpLUT lut = new InterpLUT();
    int speedLut;

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);

        telemetry.setDisplayFormat(Telemetry.DisplayFormat.HTML);

        if(TransferClass.robotPose != null) {
            follower.setPose(TransferClass.robotPose);
        } else {
            telemetry.addLine(HtmlTelemetryUtil.constructString(HtmlTelemetryUtil.Size.h1, HtmlTelemetryUtil.Colors.red, "Transfer pose is null. DO A CORNER RESET - ODO LOCKING WILL NOT WORK"));
            DriveUtil.headingLockMethod = DriveUtil.HeadingLockMethod.APRILTAG;
        }

        if(TransferClass.alliance == null) {
            telemetry.addLine(HtmlTelemetryUtil.constructString(HtmlTelemetryUtil.Size.h1, HtmlTelemetryUtil.Colors.red, "Transfer alliance is null. ODO LOCKING WILL LOCK ONTO ALL HIVES"));
        }

        lut.add(1000, 1); //todo - add lut range
        lut.add(6000, 30);
        lut.createLUT();

        intake = new Intake(hardwareMap);

        shooter = new Shooter(hardwareMap);
        shooter.setTargetRpm(3000);

        transfer = new Transfer(hardwareMap);

        headingLock = new HeadingLockV2(hardwareMap,follower);

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
    }

    @Override
    public void start() {
        shooter.getControllerObject().reset();
    }

    @Override
    public void loop() {
//        follower.update();
        shooter.update();
        headingLockToggle.update(gamepad1.right_stick_button);

        if(shooter.isReady()) {
//            gamepad1.rumble(10);
//            gamepad2.rumble(10);
        }

        // Gamepad 1 controls
        if(gamepad1.right_trigger > 0) { // Intake controls
            intake.setState(Intake.State.INTAKE);
        } else if(gamepad1.right_bumper) {
            intake.setState(Intake.State.OUTTAKE);
        } else {
            intake.setState(Intake.State.STOP);
        }

        if(gamepad1.dpadUpWasPressed()) { // Heading locking controls
            DriveUtil.cycleHeadingLockMethodUp();
        }
        if(gamepad1.dpadDownWasPressed()) {
            DriveUtil.cycleHeadingLockMethodDown();
        }

        // Gamepad 2 controls
        if(gamepad2.rightBumperWasPressed()) {
            shooter.bumpRpm(100);
        }

        if(gamepad2.leftBumperWasPressed()) {
            shooter.bumpRpm(-100);
        }

        if(gamepad2.right_trigger > 0) {
            transfer.setState(Transfer.State.UP);
        } else {
            transfer.setState(Transfer.State.DOWN);
        }

        // Telemetry
        String speedLutColorToReturn = "";
        if(Math.abs(shooter.getCurrentRpm() - shooter.getTargetRpm()) > 500) {
            speedLutColorToReturn = HtmlTelemetryUtil.constructString(HtmlTelemetryUtil.Size.h1, HtmlTelemetryUtil.Colors.red, String.valueOf(Math.round(lut.get(shooter.getCurrentRpm()) * 100.0) / 100.0));
        } else if(Math.abs(shooter.getCurrentRpm() - shooter.getTargetRpm()) > 250) {
            speedLutColorToReturn = HtmlTelemetryUtil.constructString(HtmlTelemetryUtil.Size.h1, HtmlTelemetryUtil.Colors.yellow, String.valueOf(Math.round(lut.get(shooter.getCurrentRpm()) * 100.0) / 100.0));
        } else {
            speedLutColorToReturn = HtmlTelemetryUtil.constructString(HtmlTelemetryUtil.Size.h1, HtmlTelemetryUtil.Colors.green, String.valueOf(Math.round(lut.get(shooter.getCurrentRpm()) * 100.0) / 100.0));
        }

        telemetry.addLine((HtmlTelemetryUtil.constructString(HtmlTelemetryUtil.Size.h1, "Flywheel Target Speed: ") + speedLutColorToReturn));
        telemetry.addLine("Flywheel Current RPM: " + ((Math.round(shooter.getCurrentRpm() * 100.0) / 100.0)) + " ~ Flywheel Current Velocity: " + ((Math.round(shooter.getMotorObject().getVelocity() * 100.0) / 100.0)));

//        String headingLockingToReturn = "";
//
//        if(headingLockToggle.isEnabled()) {
//            headingLockingToReturn = HtmlTelemetryUtil.constructString(HtmlTelemetryUtil.Size.h1, HtmlTelemetryUtil.Colors.green, "Heading Locking Mode: " + DriveUtil.headingLockMethod.toString());
//        } else {
//            headingLockingToReturn = HtmlTelemetryUtil.constructString(HtmlTelemetryUtil.Size.h1, HtmlTelemetryUtil.Colors.red, "Heading Locking Mode: " + DriveUtil.headingLockMethod.toString());
//        }

//        telemetry.addLine(headingLockingToReturn);

//        DrivePowers returnedDrivePowers = DriveUtil.getRobotCentricDrivePowers(gamepad1, headingLock, headingLockToggle);
//        follower.manual(returnedDrivePowers);
//        follower.drivetrain.drive(returnedDrivePowers, true);

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
