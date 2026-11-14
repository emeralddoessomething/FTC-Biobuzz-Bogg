package org.firstinspires.ftc.teamcode.TeleOp;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.util.InterpLUT;
import com.skeletonarmy.marrow.BulkCaching;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Pedro.Constants;
import org.firstinspires.ftc.teamcode.SubSystems.AutoTeleOpTransfer.TransferClass;
import org.firstinspires.ftc.teamcode.SubSystems.Intake;
import org.firstinspires.ftc.teamcode.SubSystems.Shooter;
import org.firstinspires.ftc.teamcode.SubSystems.Transfer;
import org.firstinspires.ftc.teamcode.Utils.DriveUtil;
import org.firstinspires.ftc.teamcode.Utils.GoBildaIndicatorLight;
import org.firstinspires.ftc.teamcode.Utils.HeadingLock;
import org.firstinspires.ftc.teamcode.Utils.HtmlTelemetryUtil;
import org.firstinspires.ftc.teamcode.Utils.Round;
import org.firstinspires.ftc.teamcode.Utils.Toggle;

@BulkCaching
@TeleOp(name = "Bogg TeleOp", group = "TeleOp")
public class BoggTeleop extends OpMode {
    Follower follower;

    Intake intake;
    Shooter shooter;
    InterpLUT rpmToNumLut = new InterpLUT();
    InterpLUT numToRpmLut = new InterpLUT();
    Transfer transfer;

    HeadingLock headingLock;
    Toggle headingLockToggle = new Toggle();

    GoBildaIndicatorLight indicatorLight;

    @Override
    public void init() {
        telemetry.setDisplayFormat(Telemetry.DisplayFormat.HTML);
        telemetry.setMsTransmissionInterval(500);

        // Pedro
        follower = Constants.create(hardwareMap);

        if(TransferClass.robotPose == null) {
            telemetry.addLine(HtmlTelemetryUtil.constructString(HtmlTelemetryUtil.Size.h1, HtmlTelemetryUtil.Colors.red, "Transfer Pose is Null"));
            telemetry.addLine(HtmlTelemetryUtil.constructString(HtmlTelemetryUtil.Size.h2, HtmlTelemetryUtil.Colors.yellow, "Odo Heading Locking is Default OFF"));

            DriveUtil.headingLockMethod = DriveUtil.HeadingLockMethod.APRILTAG;
        } else {
            follower.setPose(TransferClass.robotPose);
        }

        if(TransferClass.alliance == null) {
            telemetry.addLine(HtmlTelemetryUtil.constructString(HtmlTelemetryUtil.Size.h1, HtmlTelemetryUtil.Colors.red, "Transfer Alliance is Null"));
            telemetry.addLine(HtmlTelemetryUtil.constructString(HtmlTelemetryUtil.Size.h2, HtmlTelemetryUtil.Colors.yellow, "Locking to All Hives+Tags"));
        }

        // Subsystems
        intake = new Intake(hardwareMap);

        shooter = new Shooter(hardwareMap);
        shooter.setTargetRpm(3000);

        rpmToNumLut.add(1000, 1); //todo - add lut range
        rpmToNumLut.add(6000, 30);
        rpmToNumLut.createLUT();

        numToRpmLut.add(1, 1000); //todo - add lut range
        numToRpmLut.add(30, 6000);
        numToRpmLut.createLUT();

        transfer = new Transfer(hardwareMap);

        indicatorLight = new GoBildaIndicatorLight(hardwareMap);

        // Heading Locking
        headingLock = new HeadingLock(hardwareMap, follower);

        telemetry.update();
    }

    @Override
    public void init_loop() {
        headingLock.configureVisionPortal();
    }

    @Override
    public void start() {
        headingLock.getOdometryController().reset();
        headingLock.getVisionController().reset();
        shooter.getControllerObject().reset();
    }

    @Override
    public void loop() {
        follower.update();
        shooter.update();

        headingLockToggle.update(gamepad1.right_stick_button); // todo - change

        if(Math.abs(shooter.getCurrentRpm() - shooter.getTargetRpm()) > 500) {
            indicatorLight.setColor(GoBildaIndicatorLight.Color.RED);
        } else if(Math.abs(shooter.getCurrentRpm() - shooter.getTargetRpm()) > 150) {
            indicatorLight.setColor(GoBildaIndicatorLight.Color.YELLOW);
        } else {
            indicatorLight.setColor(GoBildaIndicatorLight.Color.GREEN);
        }

        // Gamepad 1 controls
        if(gamepad1.right_trigger > 0.1) { // Intake controls
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
        if(gamepad2.rightBumperWasPressed()) { // Flywheel speed controls
            shooter.bumpRpm(100);
        }

        if(gamepad2.leftBumperWasPressed()) { // Flywheel speed controls
            shooter.bumpRpm(-100);
        }

        if(gamepad2.right_trigger > 0.1) { // Shooting controls (transfer)
            transfer.setMotorState(Transfer.MotorState.FORWARD);
            transfer.setServoState(Transfer.ServoState.FORWARD);
        } else if(gamepad2.left_trigger > 0.1) {
            transfer.setMotorState(Transfer.MotorState.FORWARD);
            transfer.setServoState(Transfer.ServoState.REVERSE);
        } else {
            transfer.setMotorState(Transfer.MotorState.STOP);
            transfer.setServoState(Transfer.ServoState.STOP);
        }

        // Telemetry
        String flywheelSpeedToReturn = String.valueOf(Round.round(rpmToNumLut.get(shooter.getCurrentRpm()), 2));

        switch(indicatorLight.getColor()) {
            case RED:
                flywheelSpeedToReturn = HtmlTelemetryUtil.constructString(HtmlTelemetryUtil.Size.h1, HtmlTelemetryUtil.Colors.red, flywheelSpeedToReturn);
                break;
            case YELLOW:
                flywheelSpeedToReturn = HtmlTelemetryUtil.constructString(HtmlTelemetryUtil.Size.h1, HtmlTelemetryUtil.Colors.yellow, flywheelSpeedToReturn);
                break;
            case GREEN:
                flywheelSpeedToReturn = HtmlTelemetryUtil.constructString(HtmlTelemetryUtil.Size.h1, HtmlTelemetryUtil.Colors.green, flywheelSpeedToReturn);
                break;
        }

        telemetry.addLine(HtmlTelemetryUtil.constructString(HtmlTelemetryUtil.Size.h1, "Flywheel Speed: ") + flywheelSpeedToReturn + HtmlTelemetryUtil.constructString(HtmlTelemetryUtil.Size.h1, " / " + Round.round(rpmToNumLut.get(shooter.getTargetRpm()), 2)));
        telemetry.addLine("RPM: " + Round.round(shooter.getCurrentRpm(), 2) + " / " + Round.round(shooter.getTargetRpm(), 2));

        String headingLockingToReturn = "";

        if(headingLockToggle.isEnabled()) {
            headingLockingToReturn = HtmlTelemetryUtil.constructString(HtmlTelemetryUtil.Size.h1, HtmlTelemetryUtil.Colors.green, "Heading Locking Mode: " + DriveUtil.headingLockMethod.toString());
        } else {
            headingLockingToReturn = HtmlTelemetryUtil.constructString(HtmlTelemetryUtil.Size.h1, HtmlTelemetryUtil.Colors.red, "Heading Locking Mode: " + DriveUtil.headingLockMethod.toString());
        }

        telemetry.addLine(headingLockingToReturn);

        telemetry.update();

        // Drive
        DrivePowers returnedDrivePowers = DriveUtil.getRobotCentricDrivePowers(gamepad1, headingLock, headingLockToggle);
        follower.manual(returnedDrivePowers);
    }

    @Override
    public void stop() {
        headingLock.getVisionPortal().close();
    }
}