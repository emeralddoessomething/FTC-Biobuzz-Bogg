package org.firstinspires.ftc.teamcode.SubSystems.AutoTeleOpTransfer;

import com.pedropathing.math.Pose;

public class TransferClass {
    public static Pose robotPose = null;

    /**
     * Set the robot pose to the transfer class
     * @param pose Pose to set the class pose to (Pose to transfer)
     * @return If the pose was set successfully
     */
    public static boolean setRobotPose(Pose pose) {
        // Check if pose is not all zeros to address a pedro pathing 2.1 bug
        /*

        We are aware of a bug in Pedro 2.1 causing follower.getPose() to return (0, 0, 0)
        at the very end of an OpMode, which can cause transferring poses between OpModes to not work.
        We are working to fix the issue. In the meantime, you can add a check before you save the pose
        to check if it is (0, 0, 0).

         */
        if(pose.x() != 0 || pose.y() != 0 || pose.heading() != 0) {
            robotPose = pose;
            return true;
        } else {
            return false;
        }
    }


    public static Alliance alliance = null;
    public static enum Alliance {
        RED,
        BLUE
    }
}
