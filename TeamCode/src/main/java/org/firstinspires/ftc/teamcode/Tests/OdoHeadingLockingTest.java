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
import org.firstinspires.ftc.teamcode.Utils.OdoHeadingLock;

@Disabled
@TeleOp(name = "Odometry Heading Lock Test", group = "Test")
public class OdoHeadingLockingTest extends OpMode {
    Follower follower;
    OdoHeadingLock odoHeadingLock;
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
        if(prompter.get("alliance") == Alliance.RED) {
            TransferClass.alliance = TransferClass.Alliance.RED;
        } else {
            TransferClass.alliance = TransferClass.Alliance.BLUE;
        }

        odoHeadingLock = new OdoHeadingLock(follower);
    }

    @Override
    public void loop() {
        double correction = odoHeadingLock.update();

        DrivePowers drivePowers = new DrivePowers(
                -gamepad1.left_stick_y,
                -gamepad1.left_stick_x,
                -gamepad1.right_stick_x + correction
        );

        follower.manual(drivePowers);

        telemetry.addLine("Alliance: " + TransferClass.alliance.toString());
        telemetry.addLine("Angle Error: " + odoHeadingLock.getAngleError());
        telemetry.addLine("Correction output: " + correction);
        telemetry.addLine("Tracking Point: " + odoHeadingLock.findNearestPose(TransferClass.alliance));

        follower.update();
    }
}
