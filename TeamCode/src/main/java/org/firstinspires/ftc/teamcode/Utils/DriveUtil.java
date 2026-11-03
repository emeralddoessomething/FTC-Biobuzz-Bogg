package org.firstinspires.ftc.teamcode.Utils;

import com.pedropathing.drivetrain.DrivePowers;
import com.qualcomm.robotcore.hardware.Gamepad;

public class DriveUtil {
    public static HeadingLockMethod headingLockMethod = HeadingLockMethod.FUSION;

    public enum HeadingLockMethod {
        NONE,
        ODOMETRY,
        APRILTAG,
        FUSION;

        public HeadingLockMethod cycleUp() {
            HeadingLockMethod[] values = values();
            return values[(ordinal() + 1) % values.length];
        }

        public HeadingLockMethod cycleDown() {
            HeadingLockMethod[] values = values();
            return values[(ordinal() - 1 + values.length) % values.length];
        }
    }

    public static HeadingLockMethod cycleHeadingLockMethodUp() {
        headingLockMethod = headingLockMethod.cycleUp();
        return headingLockMethod;
    }

    public static HeadingLockMethod cycleHeadingLockMethodDown() {
        headingLockMethod = headingLockMethod.cycleDown();
        return headingLockMethod;
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

    public static DrivePowers getRobotCentricDrivePowers(Gamepad gamepad, HeadingLockV2 headingLockV2, Toggle headingLockToggle) {
        double rightStickXCorrection = 0.0;

        double drivePowerMultiplyFactor = 1;
        if (gamepad.left_trigger == 1) {
            drivePowerMultiplyFactor = 0.2;
        } else {
            drivePowerMultiplyFactor = Math.abs(gamepad.left_trigger - 1);
        }

        if(!headingLockToggle.isEnabled()) {
            return new DrivePowers(
                    -gamepad.left_stick_y * drivePowerMultiplyFactor,
                    -gamepad.left_stick_x * drivePowerMultiplyFactor,
                    -gamepad.right_stick_x * drivePowerMultiplyFactor
            );
        }

        switch (headingLockMethod) {
            case NONE:
                rightStickXCorrection = 0.0;
                break;
            case ODOMETRY:
                if (headingLockV2 == null) return new DrivePowers(-gamepad.left_stick_y * drivePowerMultiplyFactor, -gamepad.left_stick_x * drivePowerMultiplyFactor, -gamepad.right_stick_x * drivePowerMultiplyFactor);
                rightStickXCorrection = headingLockV2.odometryUpdate();
                break;
            case APRILTAG:
                if (headingLockV2 == null) return new DrivePowers(-gamepad.left_stick_y * drivePowerMultiplyFactor, -gamepad.left_stick_x * drivePowerMultiplyFactor, -gamepad.right_stick_x * drivePowerMultiplyFactor);
                rightStickXCorrection = headingLockV2.aprilTagUpdate();
                break;
            case FUSION:
                if (headingLockV2 == null) return new DrivePowers(-gamepad.left_stick_y * drivePowerMultiplyFactor, -gamepad.left_stick_x * drivePowerMultiplyFactor, -gamepad.right_stick_x * drivePowerMultiplyFactor);
                rightStickXCorrection = headingLockV2.fusionUpdate();
                break;
        }

        DrivePowers drivePowers = new DrivePowers(
                -gamepad.left_stick_y * drivePowerMultiplyFactor,
                -gamepad.left_stick_x * drivePowerMultiplyFactor,
                -gamepad.right_stick_x + rightStickXCorrection
        );

        return drivePowers;
    }
}
