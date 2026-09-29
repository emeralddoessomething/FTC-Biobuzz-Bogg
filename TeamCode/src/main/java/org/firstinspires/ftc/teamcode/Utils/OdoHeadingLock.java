package org.firstinspires.ftc.teamcode.Utils;

import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.utils.Angle;
import com.skeletonarmy.marrow.OpModeManager;

import org.firstinspires.ftc.teamcode.SubSystems.AutoTeleOpTransfer.TransferClass;

import java.util.ArrayList;

/**
 * Uses pedro pathing follower to calculate which hive to aim at via odometry
 */
public class OdoHeadingLock {
    public static double p = 0.0;
    public static double d = 0.0;
    public static double target = 0.0; // Target heading for the controller to hit

    TransferClass.Alliance alliance;
    Follower follower;
    Controller controller;

    Pose redAudience = new Pose(59.50, 55);
    Pose redScoring = new Pose(59.50, 85);
    Pose blueAudience = new Pose(85, 55);
    Pose blueScoring = new Pose(85, 85);

    /**
     * Constructor. USES TRANSFER CLASS, will track all hives if no alliance is found
     * @param follower Pedro Pathing follower
     */
    public OdoHeadingLock(Follower follower) {
        this.follower = follower;

        alliance = TransferClass.alliance;

        controller = Controller.pid(p, 0, d);

        if(alliance == null) {
            JoinedTelemetry joinedTelemetry = new JoinedTelemetry(PanelsTelemetry.INSTANCE.getFtcTelemetry(), OpModeManager.getTelemetry());
            joinedTelemetry.addLine(HtmlTelemetryUtil.constructString(HtmlTelemetryUtil.Size.h1, HtmlTelemetryUtil.Colors.red, "Tracking All Hives"));
        }
    }

    /**
     * Finds closest valid hive and runs a PD controller to turn to hive
     * @return Value from PD controller, add to turning joystick value
     */
    public double update() {
        // odo locks onto the closest point with regard to alliance. If no alliance, then lock onto nearest regardless of alliance

        Pose toTrack = findNearestPose(alliance);
        Pose current = follower.pose();

        double deltaX = toTrack.x() - current.x();
        double deltaY = toTrack.y() - current.y();

        // atan2 takes the distance from us to the point and returns the angle (vector) to get to it
        // Imagine two points on a 2d plane, atan2 is just the direction to travel
        double targetHeading = Math.atan2(deltaY, deltaX);
        double rawError = targetHeading = current.heading();

        // Angle.normalize just takes a big radian over 180 degrees and 'normalizes' it to on angle below 180 degrees
        // Eg. if raw error said 270 degrees normalizing it would return -90 degrees, making the robot turn 90 degrees left instead of 270 degrees right
        double angleError = Angle.normalize(rawError);

        return controller.calculate(targetHeading, angleError);
    }

    /**
     * Finds the nearest hive pose
     * @param alliance Alliance
     * @return Nearest hive pose
     */
    private Pose findNearestPose(TransferClass.Alliance alliance) {
        ArrayList<Pose> possiblePoses = new ArrayList<Pose>();

        switch(alliance) {
            case RED:
                possiblePoses.add(redAudience);
                possiblePoses.add(redScoring);
                break;
            case BLUE:
                possiblePoses.add(blueAudience);
                possiblePoses.add(blueScoring);
                break;
            default:
                possiblePoses.add(redAudience);
                possiblePoses.add(redScoring);
                possiblePoses.add(blueAudience);
                possiblePoses.add(blueScoring);
                break;
        }

        Pose current = follower.pose();
        Pose smallest = null;

        double smallestDistance = Double.MAX_VALUE;

        for(Pose selected : possiblePoses) {
            if(selected.distance(current) < smallestDistance) {
                smallestDistance = selected.distance(current);
                smallest = selected;
            }
        }

        return smallest;
    }

    /**
     * Returns the PD controller used by the heading lock
     * @return controller
     */
    public Controller getController() {
        return controller;
    }
}
