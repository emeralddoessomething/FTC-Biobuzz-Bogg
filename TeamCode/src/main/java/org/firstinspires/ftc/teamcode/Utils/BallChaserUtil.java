package org.firstinspires.ftc.teamcode.Utils;

//import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.skeletonarmy.marrow.weaver.PathConfig;
import com.skeletonarmy.marrow.weaver.PathResult;
import com.skeletonarmy.marrow.weaver.Weaver;
import com.skeletonarmy.marrow.zones.Point;
import com.skeletonarmy.marrow.zones.PolygonZone;

import java.util.ArrayList;
import java.util.List;

//@Config
public class BallChaserUtil {
    private double limelightHeight = 4.25; // Inches - distance limelight lens is off the ground
    private double detectionHeight = 0.0; // Inches - height of the detection
    private double limelightYaw = 0.0; // Degrees - tilt left or right
    private double limelightPitch = 0; // Degrees - tilt forwards or backwards
    private double limelightForward = 7; // Inches - distance limelight is forward of the robot
    private PolygonZone hiveRedSide = new PolygonZone(new Point(46.5, 92), new Point(46.5, 52), new Point(49, 52), new Point(49, 92));
    private PolygonZone hiveBlueSide = new PolygonZone(new Point(95, 92), new Point(95, 52), new Point(97.5, 52), new Point(97.5, 92));
    private PolygonZone redSide = new PolygonZone(new Point(0, 0), new Point(0, 144), new Point(72, 144), new Point(72, 0));
    private PolygonZone blueSide = new PolygonZone(new Point(72, 0), new Point(72, 144), new Point(144, 144), new Point(144, 0));
    private Limelight3A limelight;
    private Follower follower;
    private ArrayList<Pose> detections = new ArrayList<Pose>();

    private enum Alliance {
        RED,
        BLUE
    }
    public BallChaserUtil(HardwareMap hardwareMap, Follower follower) {
        String hardwareMapName = "limelight";
        int limelightPollRate = 100; // Hz
        int limelightPipeline = 2;

        double intakeWidth = 5; // Inches - HALF your actual intake width
        double robotWidth = 16; // Inches
        double robotLength = 16; // Inches
        double clearance = 1; // Inches

        limelight = hardwareMap.get(Limelight3A.class, hardwareMapName);
        limelight.setPollRateHz(limelightPollRate);
        limelight.pipelineSwitch(limelightPipeline);
        limelight.start();

        this.follower = follower;

        Weaver.setConfig(new PathConfig()
            .intakeWidth(intakeWidth)
            .robotWidth(robotWidth)
            .robotHeight(robotLength)
            .clearance(clearance));
    }

    public void update() {
        /*
        Update limelight results
        find cords for each ball
         */

        LLResult result = limelight.getLatestResult();

        if(result != null && result.isValid()) {
            List<LLResultTypes.DetectorResult> detectorDetections = result.getDetectorResults();
            detections.clear();

            for (LLResultTypes.DetectorResult selectedDetection : detectorDetections) {
                detections.add(findBallPose(selectedDetection));
            }
        }
    }

    public PathResult weaverUpdate() {
        PathResult pathResult = Weaver.builder()
            .start(new Point(follower.pose().x(), follower.pose().y(), follower.pose().heading()))
//            .addObstacle(hiveRedSide)
//            .addObstacle(hiveBlueSide)
            .targets(detections, selectedPose -> new Point(selectedPose.x(), selectedPose.y()))
            .maxTargets(4)
            .build();

        return pathResult;
    }

    public PathResult weaverUpdate(Alliance alliance) {
        if(alliance == Alliance.RED) {
            PathResult pathResult = Weaver.builder()
                .start(new Point(follower.pose().x(), follower.pose().y(), follower.pose().heading()))
                .addObstacle(hiveRedSide)
                .addObstacle(hiveBlueSide)
                .addObstacle(blueSide)
                .targets(detections, selectedPose -> new Point(selectedPose.x(), selectedPose.y()))
                .maxTargets(4)
                .build();

            return pathResult;
        } else if(alliance == Alliance.BLUE){
            PathResult pathResult = Weaver.builder()
                .start(new Point(follower.pose().x(), follower.pose().y(), follower.pose().heading()))
                .addObstacle(hiveRedSide)
                .addObstacle(hiveBlueSide)
                .addObstacle(redSide)
                .targets(detections, selectedPose -> new Point(selectedPose.x(), selectedPose.y()))
                .maxTargets(4)
                .build();
            return pathResult;
        }
        return null;
    }

    private Pose findBallPose(LLResultTypes.DetectorResult selected) {
        double botX = follower.pose().x(); // inches
        double botY = follower.pose().y(); // inches
        double botHeading = follower.pose().heading(); // radians

        double tx = selected.getTargetXDegrees();
        double ty = selected.getTargetYDegrees();

        double angle = ty + limelightPitch;
        double forwardDistance = (limelightHeight / Math.tan(-Math.toRadians(angle))) - limelightForward;
        double sideDistance = Math.tan(Math.toRadians(tx + limelightYaw)) * forwardDistance;

        double detectionX = botX + (forwardDistance * Math.cos(botHeading)) + (sideDistance * Math.sin(botHeading));
        double detectionY = botY + (forwardDistance * Math.sin(botHeading)) - (sideDistance * Math.cos(botHeading));

        return new Pose(detectionX, detectionY);
    }

    public void sendDashboardUpdate() {
        /*
        robot position on field
        ball positions on field
        projected path
         */
    }
}
