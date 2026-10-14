package org.firstinspires.ftc.teamcode.TeleOp;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.skeletonarmy.marrow.LynxUtil;

import org.firstinspires.ftc.teamcode.Pedro.Constants;

@TeleOp(name = "Robot Centric Drive", group = "TeleOp")
public class BasicRobotCentricDrive extends OpMode {
    Follower follower;

    @Override
    public void init() {
        LynxUtil.setBulkCachingMode(hardwareMap, LynxModule.BulkCachingMode.AUTO);

        follower = Constants.create(hardwareMap);
    }

    @Override
    public void loop() {
        DrivePowers drivePowers = new DrivePowers(
                -gamepad1.left_stick_y,
                -gamepad1.left_stick_x,
                -gamepad1.right_stick_x
        );

        follower.manual(drivePowers);

        follower.update();
    }
}
