package org.firstinspires.ftc.teamcode.Utils;

import android.util.Size;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.utils.Angle;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
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
import java.util.concurrent.TimeUnit;

@Configurable
public class HeadingLock {
    private Follower follower;
    private TransferClass.Alliance alliance;
    public enum Status {
        INIT,
        ODO_LOCKING,
        ODO_LOCKED,
        VISION_LOCKING,
        VISION_LOCKED
    }

    Status status = Status.INIT;

    // Vision
    public static double visionP = 0.01;
    public static double visionD = 0.0;
    public static double visionTarget = 0.0;
    public static double visionTimeout = 0.25; // seconds
    private final ElapsedTime lastVisionTimer = new ElapsedTime();
    private double lastValidVisionOutput = Double.NaN;
    private ArrayList<Integer> trackableIds = new ArrayList<Integer>();
    private AprilTagProcessor aprilTagProcessor;
    private boolean aprilTagProcessorEnabled = true;
    private VisionPortal visionPortal;
    private PIDFController visionController = new PIDFController(visionP, 0, visionD, 0);
    private boolean visionPortalConfigured = false;

    // Odometry
    public static double odometryP = 0.01;
    public static double odometryD = 0.0005;
    public static double odometryTarget = 0.0;
    private Pose redAudience = new Pose(59.50, 55);
    private Pose redScoring = new Pose(59.50, 85);
    private Pose blueAudience = new Pose(85, 55);
    private Pose blueScoring = new Pose(85, 85);
    private PIDFController odometryController = new PIDFController(odometryP, 0, odometryD, 0);
    public HeadingLock(HardwareMap hardwareMap, Follower follower) {
        this.follower = follower;

        // Vision
        String hardwareMapName = "webcam";
        Position cameraPosition = new Position(DistanceUnit.INCH, 0, 0, 0, 0);
        YawPitchRollAngles cameraOrientation = new YawPitchRollAngles(AngleUnit.DEGREES, 0, 0, 0, 0);

        aprilTagProcessor = new AprilTagProcessor.Builder()
            .setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
            .setTagLibrary(AprilTagGameDatabase.getBioBuzzTagLibrary())
            .setOutputUnits(DistanceUnit.INCH, AngleUnit.DEGREES)
            .setCameraPose(cameraPosition, cameraOrientation)
            .setNumThreads(3)
            .setDrawAxes(false)
            .setDrawCubeProjection(false)
            .setDrawTagOutline(false)
            .setDrawTagID(false)
//                .setDrawAxes(true)
//                .setDrawCubeProjection(true)
//                .setDrawTagOutline(true)
//                .setDrawTagID(true)

            // If calibration isn't defined, sdk will automatically try to load one for you
            // https://ftc-docs.firstinspires.org/en/latest/programming_resources/vision/camera_calibration/camera-calibration.html
            .setLensIntrinsics(459.611, 459.611, 313.399, 205.276) // fx, fy, cx, cy

            .build();

        aprilTagProcessor.setDecimation(2); // Decimation is how compressed the image will become before processing. Higher is more compressed, one is lowest

        visionPortal = new VisionPortal.Builder()
            .addProcessor(aprilTagProcessor)
            .setCamera(hardwareMap.get(WebcamName.class, hardwareMapName))
            .setCameraResolution(new Size(640, 480))
            .setStreamFormat(VisionPortal.StreamFormat.YUY2) // YUY2 is uncompressed, MJPEG is compressed
            .enableLiveView(false)
            .build();

        alliance = TransferClass.alliance;
        if(alliance == null) {
            Collections.addAll(trackableIds, 30, 31, 32, 33, 34, 35, 36, 37);
            Collections.addAll(trackableIds, 38, 39, 40, 41, 42, 43, 44, 45);
        }

        if(alliance == TransferClass.Alliance.RED) {
            Collections.addAll(trackableIds, 30, 31, 32, 33, 34, 35, 36, 37);
        }

        if(alliance == TransferClass.Alliance.BLUE) {
            Collections.addAll(trackableIds, 38, 39, 40, 41, 42, 43, 44, 45);
        }
    }


    public boolean configureVisionPortal() {
        if (visionPortalConfigured) {
            return true;
        }

        if (visionPortal.getCameraState() == VisionPortal.CameraState.STREAMING) {
            ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
            GainControl gainControl = visionPortal.getCameraControl(GainControl.class);

            if (exposureControl != null && gainControl != null) {
                exposureControl.setMode(ExposureControl.Mode.Manual);
                exposureControl.setExposure(10, TimeUnit.MILLISECONDS);
                gainControl.setGain(250);
                visionPortalConfigured = true;
                return true;
            }
        }

        return false;
    }
    public double aprilTagUpdate() {
        if (!visionPortalConfigured && visionPortal.getCameraState() == VisionPortal.CameraState.STREAMING && !visionPortalConfigured) {
            ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
            GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
            exposureControl.setMode(ExposureControl.Mode.Manual);
            exposureControl.setExposure(10, TimeUnit.MILLISECONDS);
            gainControl.setGain(250);
            visionPortalConfigured = true;
        }

        double tx = Double.NaN;
        double smallestRange = Double.MAX_VALUE;

        List<AprilTagDetection> detections = aprilTagProcessor.getFreshDetections();

        if (detections == null) {
            return Double.NaN;
        }

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
                    if (single.ftcPose.range < smallestRange) {
                        smallestRange = single.ftcPose.range;
                        tx = single.ftcPose.bearing;
                    }
                }
            }

            if (detection instanceof AprilTagClusterDetection) {
                AprilTagClusterDetection cluster = (AprilTagClusterDetection) detection;

                // Reference for apriltag & metadata tag positions: https://ftc-resources.firstinspires.org/ftc/game/cm-html/BIOBUZZ%20Competition%20Manual%20-%20TU02_files/image038.png
                if (cluster.ftcPose != null && cluster.metadata != null && cluster.metadata.name != null) {
                    if ((alliance == null || alliance == TransferClass.Alliance.RED) && cluster.metadata.name.contains("RED")) {
                        if (cluster.ftcPose.range < smallestRange) {
                            smallestRange = cluster.ftcPose.range;
                            tx = cluster.ftcPose.bearing;
                        }
                    }

                    if ((alliance == null || alliance == TransferClass.Alliance.BLUE) && cluster.metadata.name.contains("BLUE")) {
                        if (cluster.ftcPose.range < smallestRange) {
                            smallestRange = cluster.ftcPose.range;
                            tx = cluster.ftcPose.bearing;
                        }
                    }
                }
            }
        }

        if(!Double.isNaN(tx)) {
            double toReturn = -visionController.update(tx, visionTarget);
            if(Math.abs(toReturn) > 0.1) {
                status = Status.VISION_LOCKING;
            } else {
                status = Status.VISION_LOCKED;
            }

            return toReturn;
        }

        return Double.NaN; // todo - at some point, gotta make this return something like 0 while still keeping it safe if atag actually reads zero. Currently, if running just atag then in DriveUtil I have to add a check
    }

    public double odometryUpdate() {
        // odo locks onto the closest point with regard to alliance. If no alliance, then lock onto nearest regardless of alliance
        double angleError = Double.NaN;

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

        double toReturn = -odometryController.update(angleError, odometryTarget);
        if(Math.abs(toReturn) > 0.1) {
            status = Status.ODO_LOCKING;
        } else {
            status = Status.ODO_LOCKED;
        }

        return toReturn;
    }
    public Pose findNearestPose(TransferClass.Alliance alliance) {
        ArrayList<Pose> possiblePoses = new ArrayList<Pose>();

        if (alliance == TransferClass.Alliance.RED) {
            possiblePoses.add(redAudience);
            possiblePoses.add(redScoring);
        } else if (alliance == TransferClass.Alliance.BLUE) {
            possiblePoses.add(blueAudience);
            possiblePoses.add(blueScoring);
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

    public double fusionUpdate() {
        double returnedApriltagController = aprilTagUpdate();
        double returnedOdoController = odometryUpdate();

        if (!Double.isNaN(returnedApriltagController)) {
            lastValidVisionOutput = returnedApriltagController; // cache the old controller output so if the cluster decides to not be detected for a sec it won't snap to odo and jitter all over the place
            lastVisionTimer.reset();
            return returnedApriltagController;
        } else if (!Double.isNaN(lastValidVisionOutput) && lastVisionTimer.seconds() < visionTimeout) {
            return lastValidVisionOutput;
        } else {
            return returnedOdoController;
        }
    }

    public void disableApriltagProcesser() {
        if(aprilTagProcessorEnabled) {
            visionPortal.setProcessorEnabled(aprilTagProcessor, false);
            aprilTagProcessorEnabled = false;
        }
    }

    public void enableApriltagProcesser() {
        if(!aprilTagProcessorEnabled) {
            visionPortal.setProcessorEnabled(aprilTagProcessor, true);
            aprilTagProcessorEnabled = true;
        }
    }

    public boolean isAprilTagProcessorEnabled() {
        return aprilTagProcessorEnabled;
    }

    public AprilTagProcessor getAprilTagProcessor() {
        return aprilTagProcessor;
    }

    public VisionPortal getVisionPortal() {
        return visionPortal;
    }

    public PIDFController getVisionController() {
        return visionController;
    }

    public PIDFController getOdometryController() {
        return odometryController;
    }

    public void updateControllerCoefficients() {
        visionController.setPIDF(visionP, 0, visionD, 0);
        odometryController.setPIDF(odometryP, 0, odometryD, 0);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
