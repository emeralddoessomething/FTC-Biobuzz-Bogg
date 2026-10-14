package org.firstinspires.ftc.teamcode.TeleOp;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.utils.Utils;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.ManualDrive;
import com.skeletonarmy.marrow.LynxUtil;

import org.firstinspires.ftc.teamcode.Pedro.Constants;

@TeleOp(name = "Field Centric Drive", group = "TeleOp")
public class BasicFieldCentricDrive extends OpMode {
    Follower follower;

    @Override
    public void init() {
        LynxUtil.setBulkCachingMode(hardwareMap, LynxModule.BulkCachingMode.AUTO);

        follower = Constants.create(hardwareMap);
    }

    @Override
    public void loop() {
        DrivePowers drivePowers = ManualDrive.fieldCentric(
                -gamepad1.left_stick_y,
                -gamepad1.left_stick_x,
                -gamepad1.right_stick_x,
//                follower.pose().heading()
//                Math.PI/-4
                Math.toRadians(-45)
        );

        follower.manual(drivePowers);

        follower.update();
    }
}
