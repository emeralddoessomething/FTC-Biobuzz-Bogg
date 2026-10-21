package org.firstinspires.ftc.teamcode.TeleOp;

//import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.skeletonarmy.marrow.LynxUtil;

import org.firstinspires.ftc.teamcode.Pedro.Constants;

import java.util.ArrayList;

//@Config
@TeleOp(name = "Outreach", group = "TeleOp")
public class Outreach extends OpMode {
    Follower follower;
    ElapsedTime timer;
    public static double maxSpeed = 0.5;
    public static boolean drive = true;

    @Override
    public void init() {
        LynxUtil.setBulkCachingMode(hardwareMap, LynxModule.BulkCachingMode.AUTO);
        timer = new ElapsedTime();

        follower = Constants.create(hardwareMap);
    }

    @Override
    public void start() {
        timer.reset();
    }

    @Override
    public void loop() {
        telemetry.clear();
        telemetry.addLine(Math.round(timer.seconds() * 100.0)/100.0 + "s");

        if(gamepad1.squareWasPressed()) {
            timer.reset();
        }

        DrivePowers drivePowers = new DrivePowers(
                -gamepad1.left_stick_y * maxSpeed,
                -gamepad1.left_stick_x * maxSpeed,
                -gamepad1.right_stick_x * maxSpeed
        );

        if(drive) follower.manual(drivePowers);
        follower.update();
    }
}