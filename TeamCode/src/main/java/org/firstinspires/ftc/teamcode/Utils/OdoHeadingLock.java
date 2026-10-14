package org.firstinspires.ftc.teamcode.Utils;

//import com.bylazar.telemetry.JoinedTelemetry;
//import com.bylazar.telemetry.PanelsTelemetry;
import com.pedropathing.controllers.Controller;
import com.pedropathing.controllers.PIDController;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.utils.Angle;
import com.skeletonarmy.marrow.OpModeManager;

import org.firstinspires.ftc.teamcode.SubSystems.AutoTeleOpTransfer.TransferClass;

import java.util.ArrayList;

/**
 * Uses pedro pathing follower to calculate which hive to aim at via odometry
 */
//@Configurable
public class OdoHeadingLock {
    public static double p = 0.01;
    public static double d = 0.0;
    public static double target = 0.0; // Target heading for the controller to hit
    private double angleError;

    private TransferClass.Alliance alliance;
    private Follower follower;
    private Controller controller;

    private Pose redAudience = new Pose(59.50, 55);
    private Pose redScoring = new Pose(59.50, 85);
    private Pose blueAudience = new Pose(85, 55);
    private Pose blueScoring = new Pose(85, 85);

    /**
     * Constructor. USES TRANSFER CLASS, will track all hives if no alliance is found
     * @param follower Pedro Pathing follower
     */
    public OdoHeadingLock(Follower follower) {
        this.follower = follower;

        alliance = TransferClass.alliance;

        controller = Controller.pid(p, 0, d);

        if(alliance == null) {
//            JoinedTelemetry joinedTelemetry = new JoinedTelemetry(PanelsTelemetry.INSTANCE.getFtcTelemetry(), OpModeManager.getTelemetry());
//            joinedTelemetry.addLine(HtmlTelemetryUtil.constructString(HtmlTelemetryUtil.Size.h1, HtmlTelemetryUtil.Colors.red, "Tracking All Hives"));
        }
    }

    /**
     * Finds closest valid hive and runs a PD controller to turn to hive
     * @return Value from PD controller, add to turning joystick value
     */
    public double update() {
        if (controller instanceof PIDController) { // Change values of controller for live tuning
            PIDController pid = (PIDController) controller;
            pid.kP = p;
            pid.kD = d;
        }

        // odo locks onto the closest point with regard to alliance. If no alliance, then lock onto nearest regardless of alliance
        angleError = Double.NaN;

        Pose toTrack = findNearestPose(alliance);
        Pose current = follower.pose();

        double deltaX = toTrack.x() - current.x();
        double deltaY = toTrack.y() - current.y();

        // atan2 takes the distance from us to the point and returns the angle (vector) to get to it
        // Imagine two points on a 2d plane, atan2 is just the direction to travel
        double targetHeading = Math.atan2(deltaY, deltaX);
        double rawError = targetHeading - current.heading();

        // Angle.normalizeSigned normalizes the angle to [-PI, PI] (-180 to +180 degrees)
        angleError = Math.toDegrees(Angle.normalizeSigned(rawError));

        return controller.calculate(target, angleError);
    }

    /**
     * Finds the nearest hive pose
     * @param alliance Alliance
     * @return Nearest hive pose
     */
    public Pose findNearestPose(TransferClass.Alliance alliance) {
        ArrayList<Pose> possiblePoses = new ArrayList<Pose>();

        if(alliance != null) { // switch statements can't comprehend null and explodes cause we're on old java
            switch (alliance) {
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
        } else {
            possiblePoses.add(redAudience);
            possiblePoses.add(redScoring);
            possiblePoses.add(blueAudience);
            possiblePoses.add(blueScoring);
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

    public double getAngleError() {
        return angleError;
    }
}
