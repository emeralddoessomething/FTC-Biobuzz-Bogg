package org.firstinspires.ftc.teamcode.Tests;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.skeletonarmy.marrow.prompts.OptionPrompt;
import com.skeletonarmy.marrow.prompts.Prompter;

import org.firstinspires.ftc.teamcode.Pedro.Constants;
import org.firstinspires.ftc.teamcode.SubSystems.AutoTeleOpTransfer.TransferClass;
import org.firstinspires.ftc.teamcode.Utils.HeadingLockV2;

@Disabled
@TeleOp(name = "HeadingLockV2 Test", group = "Tests")
public class HeadingLockV2Test extends OpMode {
    Follower follower;
    HeadingLockV2 headingLockV2;
    Servo servo;

    enum Alliance {
        RED,
        BLUE
    }

    Prompter prompter = new Prompter(this);

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
        servo = hardwareMap.get(Servo.class, "armLeft");
        servo.setPosition(0.50);

        prompter.prompt("alliance", new OptionPrompt<>("Alliance", Alliance.RED, Alliance.BLUE))
                .label("Alliance")
                .showSummary()
                .onComplete(this::onPrompterComplete);
    }

    @Override
    public void init_loop() {
        prompter.run();
    }

    public void onPrompterComplete() {
        follower.setPose(new Pose(0, 0, 0));
        if(prompter.get("alliance") == HeadingLockV2Test.Alliance.RED) {
            TransferClass.alliance = TransferClass.Alliance.RED;
        } else {
            TransferClass.alliance = TransferClass.Alliance.BLUE;
        }

        headingLockV2 = new HeadingLockV2(hardwareMap, follower);
    }

    @Override
    public void loop() {
        double correction = headingLockV2.fusionUpdate();
        double apriltagCorrection = headingLockV2.aprilTagUpdate();
        double odoCorrection = headingLockV2.odometryUpdate();

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

        follower.update();
    }
}
