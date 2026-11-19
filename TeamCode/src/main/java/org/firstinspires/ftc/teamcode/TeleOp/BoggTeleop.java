package org.firstinspires.ftc.teamcode.TeleOp;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
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
import org.firstinspires.ftc.teamcode.Utils.Prism.Color;
import org.firstinspires.ftc.teamcode.Utils.Prism.GoBildaPrismDriver;
import org.firstinspires.ftc.teamcode.Utils.Prism.PrismAnimations;
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
    GoBildaPrismDriver prismDriver;
    PrismAnimations.Blink redBlink = new PrismAnimations.Blink(Color.RED, Color.TRANSPARENT);
    PrismAnimations.Blink yellowBlink = new PrismAnimations.Blink(Color.YELLOW, Color.TRANSPARENT);
    PrismAnimations.Solid redSolid = new PrismAnimations.Solid(Color.RED);
    PrismAnimations.Solid blank = new PrismAnimations.Solid(Color.TRANSPARENT);

    enum PrismState {
        DEFAULT,
        HEADING_LOCKING,
        HEADING_LOCKED,
        RED_BLINK,
        SOLID_RED,
        RAINBOW
    }
    PrismState currentPrismState = PrismState.DEFAULT;

    ElapsedTime matchTimer = new ElapsedTime();

    @Override
    public void init() {
        telemetry.setDisplayFormat(Telemetry.DisplayFormat.HTML);
        telemetry.setMsTransmissionInterval(500);

        // Pedro
        follower = Constants.create(hardwareMap);

        if(TransferClass.robotPose == null) {
            telemetry.addLine(HtmlTelemetryUtil.constructString(HtmlTelemetryUtil.Size.h1, HtmlTelemetryUtil.Colors.red, "Transfer Pose is Null"));
            telemetry.addLine(HtmlTelemetryUtil.constructString(HtmlTelemetryUtil.Size.h2, HtmlTelemetryUtil.Colors.yellow, "Odo Heading Locking is Default OFF (Now Apriltags)"));

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

        /*
        Artboard 0 - solid purple
        Artboard 1 - solid yellow
        Artboard 2 - sold red
        Artboard 3 - RGB
         */
        prismDriver = hardwareMap.get(GoBildaPrismDriver.class, "prism");
        prismDriver.loadAnimationsFromArtboard(GoBildaPrismDriver.Artboard.ARTBOARD_0);
        currentPrismState = PrismState.DEFAULT;

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

        matchTimer.reset();
    }

    @Override
    public void loop() {
        follower.update();
        shooter.update();

        headingLockToggle.update(gamepad1.right_stick_button); // todo - change
        if(headingLockToggle.isEnabled()) {
            headingLock.enableApriltagProcesser();
        } else {
            headingLock.disableApriltagProcesser();
        }

        // Indicator Light
        if(Math.abs(shooter.getCurrentRpm() - shooter.getTargetRpm()) > 500) {
            indicatorLight.setColor(GoBildaIndicatorLight.Color.RED);
        } else if(Math.abs(shooter.getCurrentRpm() - shooter.getTargetRpm()) > 150) {
            indicatorLight.setColor(GoBildaIndicatorLight.Color.YELLOW);
        } else {
            indicatorLight.setColor(GoBildaIndicatorLight.Color.GREEN);
        }

        // Underglow
        double timeRemaining = 120.0 - matchTimer.seconds();
        boolean headingLocked = headingLockToggle.isEnabled() && (headingLock.getStatus() == HeadingLock.Status.VISION_LOCKED || headingLock.getStatus() == HeadingLock.Status.ODO_LOCKED);
        boolean headingLocking = headingLockToggle.isEnabled() && (headingLock.getStatus() == HeadingLock.Status.VISION_LOCKING || headingLock.getStatus() == HeadingLock.Status.ODO_LOCKING);

        PrismState targetState;
        if (timeRemaining <= 5.0) {
            targetState = PrismState.SOLID_RED;
        } else if (timeRemaining <= 15.0 && timeRemaining > 10.0) {
            targetState = PrismState.RED_BLINK;
        } else if (timeRemaining <= 30.0 && timeRemaining > 25.0) {
            targetState = PrismState.RED_BLINK;
        } else if(headingLocking) {
            targetState = PrismState.HEADING_LOCKING;
        } else if (headingLocked) {
            targetState = PrismState.HEADING_LOCKED;
        } else {
            targetState = PrismState.DEFAULT;
        }

        if (targetState != currentPrismState) { // I do it this way because i2c writes are an expensive operation, so this makes sure that I don't have to do one every loop
            currentPrismState = targetState;
            switch (currentPrismState) {
                case RED_BLINK:
                    prismDriver.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_1, redBlink);
                    break;
                case SOLID_RED:
                    prismDriver.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_1, redSolid);
                    break;
                case HEADING_LOCKING:
                    prismDriver.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_1, yellowBlink);
                    break;
                case HEADING_LOCKED:
                    prismDriver.loadAnimationsFromArtboard(GoBildaPrismDriver.Artboard.ARTBOARD_1);
                    break;
                case DEFAULT:
                    prismDriver.loadAnimationsFromArtboard(GoBildaPrismDriver.Artboard.ARTBOARD_0);
                    break;
            }
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
        } else if(gamepad2.left_trigger > 0.1) {
            transfer.setMotorState(Transfer.MotorState.REVERSE);
        } else {
            transfer.setMotorState(Transfer.MotorState.STOP);
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