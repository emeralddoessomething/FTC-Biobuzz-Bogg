package org.firstinspires.ftc.teamcode.Utils;

import com.pedropathing.drivetrain.DrivePowers;
import com.qualcomm.robotcore.hardware.Gamepad;

public class DriveUtil {
    public static HeadingLockMethod headingLockMethod = HeadingLockMethod.FUSION;
    public enum HeadingLockMethod {
        NONE,
        ODOMETRY,
        APRILTAG,
        FUSION
    }
//    public static DrivePowers getFieldCentricDrivePowers(Gamepad gamepad, HeadingLockV2 headingLockV2, HeadingLockMethod headingLockMethod) {
//        DrivePowers
//
//        switch (headingLockMethod) {
//            case NONE:
//                break;
//            case ODOMETRY:
//                break;
//            case APRILTAG:
//                break;
//            case FUSION:
//                break;
//        }
//    }

    public static DrivePowers getRobotCentricDrivePowers(Gamepad gamepad, HeadingLockV2 headingLockV2) {
        double rightStickXCorrection = 0.0;

        switch (headingLockMethod) {
            case NONE:
                rightStickXCorrection = 0.0;
                break;
            case ODOMETRY:
                if (headingLockV2 == null) return new DrivePowers(-gamepad.left_stick_y, -gamepad.left_stick_x, -gamepad.right_stick_x);
                rightStickXCorrection = headingLockV2.odometryUpdate();
                break;
            case APRILTAG:
                if (headingLockV2 == null) return new DrivePowers(-gamepad.left_stick_y, -gamepad.left_stick_x, -gamepad.right_stick_x);
                rightStickXCorrection = headingLockV2.aprilTagUpdate();
                break;
            case FUSION:
                if (headingLockV2 == null) return new DrivePowers(-gamepad.left_stick_y, -gamepad.left_stick_x, -gamepad.right_stick_x);
                rightStickXCorrection = headingLockV2.fusionUpdate();
                break;
        }

        DrivePowers drivePowers = new DrivePowers(
                -gamepad.left_stick_y,
                -gamepad.left_stick_x,
                -gamepad.right_stick_x + rightStickXCorrection
        );

        return drivePowers;
    }
}
