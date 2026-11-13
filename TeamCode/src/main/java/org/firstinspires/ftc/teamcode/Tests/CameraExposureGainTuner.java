package org.firstinspires.ftc.teamcode.Tests;

import android.util.Size;

import com.bylazar.camerastream.Plugin;
import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Utility;

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

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configurable
@Utility(name = "Camera Exposure Gain Tuner")
public class CameraExposureGainTuner extends OpMode {
    private AprilTagProcessor aprilTagProcessor;
    private VisionPortal visionPortal;
    private ExposureControl exposureControl;
    private GainControl gainControl;

    public static boolean streamToPanels = true;
    public static int exposureMs = 10;
    private int oldExposure = 10;
    public static int gain = 250;
    private int oldGain = 250;
    public static int decimation = 3;
    private int oldDecimation = 3;
    public static AprilTagProcessor.PoseSolver poseSolver = AprilTagProcessor.PoseSolver.APRILTAG_BUILTIN;
    private AprilTagProcessor.PoseSolver oldPoseSolver = AprilTagProcessor.PoseSolver.APRILTAG_BUILTIN;
    private boolean setStuff = false;

    @Override
    public void init() {
        String hardwareMapName = "webcam";
        Position cameraPosition = new Position(DistanceUnit.INCH, 0, 0, 0, 0);
        YawPitchRollAngles cameraOrientation = new YawPitchRollAngles(AngleUnit.DEGREES, 0, 0, 0, 0);

        aprilTagProcessor = new AprilTagProcessor.Builder()
                .setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
                .setTagLibrary(AprilTagGameDatabase.getBioBuzzTagLibrary())
                .setOutputUnits(DistanceUnit.INCH, AngleUnit.DEGREES)
                .setCameraPose(cameraPosition, cameraOrientation)
                .setNumThreads(3)
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .setDrawTagOutline(true)
                .setDrawTagID(true)

                // If calibration isn't defined, sdk will automatically try to load one for you
                .setLensIntrinsics(459.611, 459.611, 313.399, 205.276) // fx, fy, cx, cy

                .build();

        aprilTagProcessor.setDecimation(decimation); // Decimation is how compressed the image will become before processing. Higher is more compressed, one is lowest

        visionPortal = new VisionPortal.Builder()
                .addProcessor(aprilTagProcessor)
                .setCamera(hardwareMap.get(WebcamName.class, hardwareMapName))
                .setCameraResolution(new Size(640, 480))
                .setStreamFormat(VisionPortal.StreamFormat.YUY2) // YUY2 is uncompressed, MJPEG is compressed
//                .enableLiveView(false)
                .build();

        Plugin.INSTANCE.startStream(visionPortal, 120);
    }

    @Override
    public void init_loop() {
        if(visionPortal.getCameraState() == VisionPortal.CameraState.STREAMING && !setStuff) {
            exposureControl = visionPortal.getCameraControl(ExposureControl.class);
            gainControl = visionPortal.getCameraControl(GainControl.class);
            exposureControl.setMode(ExposureControl.Mode.Manual);
            exposureControl.setExposure(6, TimeUnit.MILLISECONDS);
            gainControl.setGain(250);
            setStuff = true;
        }
    }

    @Override
    public void loop() {
//        if(streamToPanels) {
//            Plugin.INSTANCE.startStream(visionPortal, 30);
//        } else {
//            Plugin.INSTANCE.stopStream();
//        }

        if(oldExposure != exposureMs) {
            exposureControl.setExposure(exposureMs, TimeUnit.MILLISECONDS);
            oldExposure = exposureMs;
        }

        if(oldGain != gain) {
            gainControl.setGain(gain);
            oldGain = gain;
        }

        if(oldDecimation != decimation) {
            aprilTagProcessor.setDecimation(decimation);
            oldDecimation = decimation;
        }

        if(oldPoseSolver != poseSolver) {
            aprilTagProcessor.setPoseSolver(poseSolver);
            oldPoseSolver = poseSolver;
        }

        telemetry.addLine("Pose solve time (ms): " + aprilTagProcessor.getPerTagAvgPoseSolveTime());
        telemetry.addLine("Vision Portal FPS: " + visionPortal.getFps());

        List<AprilTagDetection> detections = aprilTagProcessor.getDetections();

        for(AprilTagDetection detection : detections) {
            if (detection instanceof AprilTagClusterDetection) {
                AprilTagClusterDetection cluster = (AprilTagClusterDetection) detection;

                telemetry.addLine("Cluster Tx: " + cluster.ftcPose.bearing);
                telemetry.addLine("Cluster Range (ft): " + cluster.ftcPose.range);
                telemetry.addLine();
            }
        }
    }
    @Override
    public void stop() {
        visionPortal.close();
    }
}
