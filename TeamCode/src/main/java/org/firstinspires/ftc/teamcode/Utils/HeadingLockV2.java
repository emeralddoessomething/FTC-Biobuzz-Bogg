package org.firstinspires.ftc.teamcode.Utils;

import android.util.Size;

import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.SubSystems.AutoTeleOpTransfer.TransferClass;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Combined heading locking using both odometry and apriltags. Uses OdoHeadingLock
 */
//@Configurable
public class HeadingLockV2 {
    public static double p = 0.01;
    public static double d = 0.0;
    public static double target = 0.0; // Target heading for the controller to hit

    private TransferClass.Alliance alliance;
    private ArrayList<Integer> trackableIds = new ArrayList<Integer>();
    private Controller controller;
    private AprilTagProcessor aprilTagProcessor;
    private VisionPortal visionPortal;
    private OdoHeadingLock odoHeadingLock;
    private double tx;

    /**
     * Constructor
     * @param hardwareMap hardwaremap
     * @param follower Current Pedro Pathing Follower
     */
    public HeadingLockV2(HardwareMap hardwareMap, Follower follower) {
        String hardwareMapName = "webcam";
        Position cameraPosition = new Position(DistanceUnit.INCH, 0, 0, 0, 0);
        YawPitchRollAngles cameraOrientation = new YawPitchRollAngles(AngleUnit.DEGREES, 0, 0, 0, 0);

        aprilTagProcessor = new AprilTagProcessor.Builder()
            .setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
            .setTagLibrary(AprilTagGameDatabase.getBioBuzzTagLibrary())
            .setOutputUnits(DistanceUnit.INCH, AngleUnit.DEGREES)
            .setCameraPose(cameraPosition, cameraOrientation)

            // If calibration isn't defined, sdk will automatically try to load one for you
            //.setLensIntrinsics(578.272, 578.272, 402.145, 221.506) // fx, fy, cx, cy

            .build();

        visionPortal = new VisionPortal.Builder()
            .addProcessor(aprilTagProcessor)
            .setCamera(hardwareMap.get(WebcamName.class, hardwareMapName))
            .setCameraResolution(new Size(640, 480))
            .setStreamFormat(VisionPortal.StreamFormat.YUY2) // YUY2 is uncompressed, MJPEG is compressed
            .build();

        alliance = TransferClass.alliance;
        if(alliance == null) {
//            JoinedTelemetry joinedTelemetry = new JoinedTelemetry(PanelsTelemetry.INSTANCE.getFtcTelemetry(), OpModeManager.getTelemetry());
//            joinedTelemetry.addLine(HtmlTelemetryUtil.constructString(HtmlTelemetryUtil.Size.h1, HtmlTelemetryUtil.Colors.red, "Tracking all Apriltags"));
            Collections.addAll(trackableIds, 30, 31, 32, 33, 34, 35, 36, 37);
            Collections.addAll(trackableIds, 38, 39, 40, 41, 42, 43, 44, 45);
        }

        if(alliance == TransferClass.Alliance.RED) {
            Collections.addAll(trackableIds, 30, 31, 32, 33, 34, 35, 36, 37);
        }

        if(alliance == TransferClass.Alliance.BLUE) {
            Collections.addAll(trackableIds, 38, 39, 40, 41, 42, 43, 44, 45);
        }

        controller = Controller.pid(p, 0, d);

        odoHeadingLock = new OdoHeadingLock(follower);
    }

    /**
     * Updates the PID controller for apriltags
     * @return controller correction
     */
    public double aprilTagUpdate() {
        controller = Controller.pid(p, 0, d); // Live tuning
        tx = Double.NaN;

        List<AprilTagDetection> detections = aprilTagProcessor.getDetections();

        // 'detections' is an arraylist of different detections that the processor found.
        // (sdk12) detections can contain either a single, standalone apriltag, known as 'AprilTagSingleDetection'
        // or a cluster of tags, known as a 'AprilTagClusterDetection'
        // Even if only a single tag of the cluster is detected, its still classified as a cluster
        // Reference https://ftc-docs.firstinspires.org/en/latest/tech_tips/tech-tips/tech-tip-apriltag-clusters/tech-tip-apriltag-clusters.html
        // Reference (pg 26) https://ftc-docs-cdn.ftclive.org/booklets/en/april_tags.pdf
        for(AprilTagDetection detection : detections) {
            if (detection instanceof AprilTagSingleDetection) {
                AprilTagSingleDetection single = (AprilTagSingleDetection) detection;

                if(trackableIds.contains(single.id) && single.ftcPose != null) {
                    tx = single.ftcPose.bearing;
                }
            }

            if (detection instanceof AprilTagClusterDetection) {
                AprilTagClusterDetection cluster = (AprilTagClusterDetection) detection;

                // Reference for apriltag & metadata tag positions: https://ftc-resources.firstinspires.org/ftc/game/cm-html/BIOBUZZ%20Competition%20Manual%20-%20TU02_files/image038.png
                if (cluster.ftcPose != null && cluster.metadata != null && cluster.metadata.name != null) {
                    if ((alliance == null || alliance == TransferClass.Alliance.RED) && cluster.metadata.name.contains("RED")) {
                        tx = cluster.ftcPose.bearing;
                    }

                    if ((alliance == null || alliance == TransferClass.Alliance.BLUE) && cluster.metadata.name.contains("BLUE")) {
                        tx = cluster.ftcPose.bearing;
                    }
                }
            }
        }

        if(!Double.isNaN(tx)) {
            return controller.calculate(target, tx);
        }

        return 0.0;
    }

    /**
     * Updates the controller for odometry
     * @return Controller correction
     */
    public double odometryUpdate() {
        return odoHeadingLock.update();
    }

    /**
     * Updates both controllers at the same time (apriltag and odometry). If no apriltag is found, it uses odometry to lock. If a tag is found, it uses that instead
     * @return Either apriltag or odometry controller correction
     */
    public double fusionUpdate() {
        double returnedApriltagController = this.aprilTagUpdate();
        double returnedOdoController = odoHeadingLock.update();

        if(Double.isNaN(tx)) {
            return returnedOdoController;
        } else {
            return returnedApriltagController;
        }

//        // Use biases to favor the apriltag rather than the odometry, as odometry could drift
//        // Biases must add to 100%, or 1
//        double apriltagBiasPercentage = 0.75;
//        double odometryBiasPercentage = 0.25;
//
//        double biasedAverage = (returnedApriltagController * apriltagBiasPercentage) + (returnedOdoController * odometryBiasPercentage);
//        return biasedAverage;
    }
}
