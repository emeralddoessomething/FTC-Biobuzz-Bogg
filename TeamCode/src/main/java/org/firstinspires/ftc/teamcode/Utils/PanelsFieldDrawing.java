package org.firstinspires.ftc.teamcode.Utils;

import com.bylazar.field.FieldManager;
import com.bylazar.field.PanelsField;
import com.bylazar.field.Style;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.math.Vector2D;
import com.pedropathing.paths.CompoundPath;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.curves.Curve;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the Drawing class. It handles the drawing of stuff on Panels Dashboard, like the robot.
 *
 * @author Lazar - 19234
 * @version 1.1, 5/19/2025
 */
public class PanelsFieldDrawing {
    public static final double ROBOT_RADIUS = 9; // woah
    private static final FieldManager panelsField = PanelsField.INSTANCE.getField();

    private static final Style robotLook = new Style(
            "", "#3F51B5", 0.75
    );
    private static final Style historyLook = new Style(
            "", "#4CAF50", 0.75
    );

    private static final List<Pose> poseHistory = new ArrayList<>();
    private static final int MAX_HISTORY_SIZE = 100;

    /**
     * This prepares Panels Field for using Pedro Offsets
     */
    public static void init() {
        panelsField.setOffsets(PanelsField.INSTANCE.getPresets().getPEDRO_PATHING());
        poseHistory.clear();
    }

    /**
     * This draws everything that will be used in the Follower's telemetryDebug() method. This takes
     * a Follower as an input, so an instance of the DashboardDrawingHandler class is not needed.
     *
     * @param follower Pedro Follower instance.
     */
    public static void drawDebug(Follower follower) {
        if (follower == null) return;

        if (follower.currentSegment() != null) {
            drawCurve(follower.currentSegment().curve, robotLook);
            if (follower.algorithm() != null) {
                Pose closestPoint = follower.algorithm().closestPose();
                if (closestPoint != null) {
                    drawRobot(closestPoint, robotLook);
                }
            }
        }

        Pose currentPose = follower.pose();
        if (currentPose != null && !Double.isNaN(currentPose.x()) && !Double.isNaN(currentPose.y())) {
            poseHistory.add(currentPose);
            if (poseHistory.size() > MAX_HISTORY_SIZE) {
                poseHistory.remove(0);
            }
        }

        drawPoseHistory(poseHistory, historyLook);
        drawRobot(currentPose, historyLook);

        sendPacket();
    }

    /**
     * This draws a robot at a specified Pose with a specified
     * look. The heading is represented as a line.
     *
     * @param pose  the Pose to draw the robot at
     * @param style the parameters used to draw the robot with
     */
    public static void drawRobot(Pose pose, Style style) {
        if (pose == null || Double.isNaN(pose.x()) || Double.isNaN(pose.y()) || Double.isNaN(pose.heading())) {
            return;
        }

        panelsField.setStyle(style);
        panelsField.moveCursor(pose.x(), pose.y());
        panelsField.circle(ROBOT_RADIUS);

        Vector2D v = Vector2D.polar(ROBOT_RADIUS, pose.heading());
        double x1 = pose.x() + v.x() / 2.0, y1 = pose.y() + v.y() / 2.0;
        double x2 = pose.x() + v.x(), y2 = pose.y() + v.y();

        panelsField.setStyle(style);
        panelsField.moveCursor(x1, y1);
        panelsField.line(x2, y2);
    }

    /**
     * This draws a robot at a specified Pose. The heading is represented as a line.
     *
     * @param pose the Pose to draw the robot at
     */
    public static void drawRobot(Pose pose) {
        drawRobot(pose, robotLook);
    }

    /**
     * This draws a Curve with a specified look.
     *
     * @param curve the Curve to draw
     * @param style the parameters used to draw the Curve with
     */
    public static void drawCurve(Curve curve, Style style) {
        if (curve == null) return;

        panelsField.setStyle(style);

        int resolution = 50;
        Vector2D prev = curve.get(0.0);
        if (prev == null || Double.isNaN(prev.x()) || Double.isNaN(prev.y())) {
            return;
        }

        panelsField.moveCursor(prev.x(), prev.y());
        for (int i = 1; i <= resolution; i++) {
            double t = (double) i / resolution;
            Vector2D current = curve.get(t);
            if (current == null || Double.isNaN(current.x()) || Double.isNaN(current.y())) {
                continue;
            }
            panelsField.line(current.x(), current.y());
            panelsField.moveCursor(current.x(), current.y());
        }
    }

    /**
     * This draws a Path with a specified look.
     *
     * @param path  the Path to draw
     * @param style the parameters used to draw the Path with
     */
    public static void drawPath(Path path, Style style) {
        if (path != null) {
            drawCurve(path.curve, style);
        }
    }

    /**
     * This draws a CompoundPath with a specified look.
     *
     * @param compoundPath the CompoundPath to draw
     * @param style        the parameters used to draw the CompoundPath with
     */
    public static void drawPath(CompoundPath compoundPath, Style style) {
        if (compoundPath != null) {
            drawCurve(compoundPath.curve, style);
        }
    }

    /**
     * This draws the pose history of the robot.
     *
     * @param poseTracker the List of Poses to get the pose history from
     * @param style       the parameters used to draw the pose history with
     */
    public static void drawPoseHistory(List<Pose> poseTracker, Style style) {
        if (poseTracker == null || poseTracker.size() < 2) return;

        panelsField.setStyle(style);

        for (int i = 0; i < poseTracker.size() - 1; i++) {
            Pose p1 = poseTracker.get(i);
            Pose p2 = poseTracker.get(i + 1);
            if (p1 == null || p2 == null || Double.isNaN(p1.x()) || Double.isNaN(p1.y()) || Double.isNaN(p2.x()) || Double.isNaN(p2.y())) {
                continue;
            }
            panelsField.moveCursor(p1.x(), p1.y());
            panelsField.line(p2.x(), p2.y());
        }
    }

    /**
     * This draws the pose history of the robot.
     *
     * @param poseTracker the List of Poses to get the pose history from
     */
    public static void drawPoseHistory(List<Pose> poseTracker) {
        drawPoseHistory(poseTracker, historyLook);
    }

    /**
     * Draw a circle on the field
     *
     * @param style   Style to use
     * @param cursorX Where to place the cursor X
     * @param cursorY Where to place the cursor Y
     * @param radius  Radius of the circle (Ref. robot pose is 9)
     */
    public static void drawCircle(Style style, double cursorX, double cursorY, double radius) {
        panelsField.setStyle(style);
        panelsField.moveCursor(cursorX, cursorY);
        panelsField.circle(radius);
    }

    /**
     * This tries to send the current packet to FTControl Panels.
     */
    public static void sendPacket() {
        panelsField.update();
    }
}
