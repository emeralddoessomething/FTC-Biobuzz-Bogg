package org.firstinspires.ftc.teamcode.Tests;

import com.bylazar.camerastream.Plugin;
import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.skeletonarmy.marrow.prompts.OptionPrompt;
import com.skeletonarmy.marrow.prompts.Prompter;

import org.firstinspires.ftc.teamcode.Pedro.Constants;
import org.firstinspires.ftc.teamcode.SubSystems.AutoTeleOpTransfer.TransferClass;
import org.firstinspires.ftc.teamcode.Utils.HeadingLock;
import org.firstinspires.ftc.teamcode.Utils.Toggle;

@TeleOp(name = "HeadingLock Test (Newest)", group = "Tests")
public class HeadingLockTest extends OpMode {
    Follower follower;
    HeadingLock headingLock;
    Toggle headingLockToggle = new Toggle();
    enum Alliance {
        RED,
        BLUE,
        ALL
    }

    Prompter prompter;

    @Override
    public void init() {
        prompter = new Prompter(this);

        follower = Constants.create(hardwareMap);

        prompter.prompt("alliance", new OptionPrompt<>("Alliance", Alliance.RED, Alliance.BLUE))
            .label("Alliance")
            .showSummary()
            .onComplete(this::onPrompterComplete);
    }

    public void onPrompterComplete() {
        follower.setPose(new Pose(0, 0, 0));
        if(prompter.get("alliance") == Alliance.RED) {
            TransferClass.alliance = TransferClass.Alliance.RED;
        } else if(prompter.get("alliance") == Alliance.BLUE){
            TransferClass.alliance = TransferClass.Alliance.BLUE;
        }

        headingLock = new HeadingLock(hardwareMap, follower);

        Plugin.INSTANCE.startStream(headingLock.getVisionPortal(), 60);
    }

    @Override
    public void init_loop() {
        prompter.run();
    }

    @Override
    public void loop() {
        headingLockToggle.update(gamepad1.right_stick_button);

        headingLock.updateControllerCoefficients();

        double correction = 0;

        if(headingLockToggle.isEnabled()) {
            correction = headingLock.fusionUpdate();
        }

        double apriltagCorrection = headingLock.aprilTagUpdate();
        double odoCorrection = headingLock.odometryUpdate();

        DrivePowers drivePowers = new DrivePowers(
                -gamepad1.left_stick_y,
                -gamepad1.left_stick_x,
                -gamepad1.right_stick_x + correction
        );

        follower.manual(drivePowers);

        telemetry.addLine("Alliance: " + TransferClass.alliance.toString());
        telemetry.addLine("Apriltag output: " + apriltagCorrection);
        telemetry.addLine("Odometry output: " + odoCorrection);
        telemetry.addLine("Fusion output: " + correction);
        telemetry.addLine();
        telemetry.addLine("Vision Portal FPS: " + headingLock.getVisionPortal().getFps());
        telemetry.addLine("April Tag Pose Solve MS: " + headingLock.getAprilTagProcessor().getPerTagAvgPoseSolveTime());
        telemetry.addLine();
        telemetry.addLine("Heading Locking: " + headingLockToggle.isEnabled());

        follower.update();
    }
}
