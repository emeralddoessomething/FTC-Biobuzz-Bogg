package org.firstinspires.ftc.teamcode.Auto;

import static com.pedropathing.api.Paths.line;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.skeletonarmy.marrow.BulkCaching;
import com.skeletonarmy.marrow.LynxUtil;
import com.skeletonarmy.marrow.prompts.BooleanPrompt;
import com.skeletonarmy.marrow.prompts.OptionPrompt;
import com.skeletonarmy.marrow.prompts.Prompter;
import com.skeletonarmy.marrow.prompts.ValuePrompt;
import com.skeletonarmy.marrow.weaver.PathConfig;
import com.skeletonarmy.marrow.weaver.PathResult;
import com.skeletonarmy.marrow.weaver.PedroPathingConverter;
import com.skeletonarmy.marrow.weaver.Weaver;
import com.skeletonarmy.marrow.zones.Point;
import com.skeletonarmy.marrow.zones.PolygonZone;

import org.firstinspires.ftc.teamcode.Pedro.Constants;
import org.firstinspires.ftc.teamcode.SubSystems.AutoTeleOpTransfer.TransferClass;
import org.firstinspires.ftc.teamcode.SubSystems.Intake;
import org.firstinspires.ftc.teamcode.SubSystems.Shooter;
import org.firstinspires.ftc.teamcode.SubSystems.Transfer;
import org.firstinspires.ftc.teamcode.TurtleTracer.GardenPaths;
import org.firstinspires.ftc.teamcode.TurtleTracer.ParkingPaths;
import org.firstinspires.ftc.teamcode.Utils.HeadingLock;
import org.firstinspires.ftc.teamcode.Utils.HtmlTelemetryUtil;
import org.firstinspires.ftc.teamcode.Utils.Prism.GoBildaPrismDriver;
import org.firstinspires.ftc.teamcode.Utils.Round;
import org.firstinspires.ftc.teamcode.Utils.Wait;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

@BulkCaching(mode = LynxModule.BulkCachingMode.MANUAL)

@Autonomous(name = "Selective Shoot", group = "Auto")
public class SelectiveShoot extends OpMode {
    private Follower follower;
    private Weaver weaver;
    private Queue<Path> pathQueue = new ArrayDeque<>();
    private ElapsedTime delayTimer;
    private ElapsedTime autoTimer;
    private ElapsedTime stepTimer;
    private Wait wait = new Wait();
    private int step = 0;

    private boolean aprilTagWaitStarted = false;
    private boolean expectedAprilTagFound = false;

    Intake intake;
    Shooter shooter;
    Transfer transfer;
    HeadingLock headingLock;
    GoBildaPrismDriver prismDriver;

    private PoseFactory poseFactory = PoseFactory.degrees();
    private Pose redAudience = poseFactory.of(59.50, 10.25, 90); // up by default
    private Pose redScoring = poseFactory.of(59.50, 133.75, -90);
    private Pose blueAudience = poseFactory.of(85, 10.25, 90);
    private Pose blueScoring = poseFactory.of(85, 133.75, -90); // up by default
    private Pose cornerRedPark = poseFactory.of(10.25, 118.75, 0); // todo - heading doesn't really matter
    private Pose exposedRedPark = poseFactory.of(10.25, 96, 0); // todo - heading doesn't really matter
    private Pose cornerBluePark = poseFactory.of(133.75, 25.25, 0); // todo - heading doesn't really matter
    private Pose exposedBluePark = poseFactory.of(133.75, 48, 0); // todo - heading doesn't really matter
    private Pose redAudienceShoot = poseFactory.of(60, 36, 90); // up by default
    private Pose blueAudienceShoot = poseFactory.of(84, 36, 90);
    private Pose redScoringShoot = poseFactory.of(60, 108, -90);
    private Pose blueScoringShoot = poseFactory.of(84, 108, -90); // up by default

    private PolygonZone hiveRedSide = new PolygonZone(new Point(46.5, 92), new Point(46.5, 52), new Point(49, 52), new Point(49, 92));
    private PolygonZone hiveBlueSide = new PolygonZone(new Point(95, 92), new Point(95, 52), new Point(97.5, 52), new Point(97.5, 92));

    Prompter prompter;

    enum Alliance {
        RED,
        BLUE
    }
    enum RedPositions {
        RED_AUDIENCE_DEFAULT_UP,
        RED_SCORING_DEFAULT_DOWN,
    }
    enum BluePositions {
        BLUE_AUDIENCE_DEFAULT_DOWN,
        BLUE_SCORING_DEFAULT_UP
    }
    enum RedParking {
        RED_CORNER_PARKING,
        RED_EXPOSED_PARKING
    }
    enum BlueParking {
        BLUE_CORNER_PARKING,
        BLUE_EXPOSED_PARKING
    }

    @Override
    public void init() {
        prompter = new Prompter(this);

        follower = Constants.create(hardwareMap);

        double robotWidth = 18; // Inches
        double robotLength = 18; // Inches
        double clearance = 2; // Inches

        Weaver.setConfig(new PathConfig()
            .robotWidth(robotWidth)
            .robotHeight(robotLength)
            .clearance(clearance));

        intake = new Intake(hardwareMap);
        shooter = new Shooter(hardwareMap);
        shooter.setTargetRpm(3000);
        transfer = new Transfer(hardwareMap);
        headingLock = new HeadingLock(hardwareMap, follower);
        prismDriver = hardwareMap.get(GoBildaPrismDriver.class, "prism");
        prismDriver.loadAnimationsFromArtboard(GoBildaPrismDriver.Artboard.ARTBOARD_4);

        // Reference https://marrow.skeletonarmyftc.com/dev-tools/prompter
        prompter.
                prompt("alliance", new OptionPrompt<>("Alliance", SelectiveShoot.Alliance.RED, SelectiveShoot.Alliance.BLUE))
                .label("Alliance")

                .prompt("position", new OptionPrompt<>("Position", SelectiveShoot.RedPositions.class))
                .showIf("alliance", SelectiveShoot.Alliance.RED)
                .label("Position")

                .prompt("position", new OptionPrompt<>("Position", SelectiveShoot.BluePositions.class))
                .showIf("alliance", SelectiveShoot.Alliance.BLUE)
                .label("Position")

                .prompt("parkingPosition", new OptionPrompt<>("Parking Position", SelectiveShoot.RedParking.class))
                .showIf("alliance", SelectiveShoot.Alliance.RED)
                .label("Parking Position")

                .prompt("parkingPosition", new OptionPrompt<>("Parking Position", SelectiveShoot.BlueParking.class))
                .showIf("alliance", SelectiveShoot.Alliance.BLUE)
                .label("Parking Position")

                .prompt("intakeFromGarden", new BooleanPrompt("Intake From Garden", true))
                .label("Intake from Garden")

                .prompt("startDelay", new ValuePrompt<>("Start delay (seconds) before first action is taken", Double.class, 0.0, 20.0, 0.0, 0.5))
                .label("Start Delay (s)")

                .prompt("aprilTagTimeout", new ValuePrompt<>("AprilTag Shooting Detection Timeout (s)", Double.class, 0.0, 20.0, 7.0, 1.0))
                .label("April Tag Timeout (s)")

                .prompt("timeAutoPark", new BooleanPrompt("Time Based Auto Park? (5s left)", true))
                .label("Time Based Auto Park (5s left)")

                .showSummary()
                .onComplete(this::pickRoute);
    }

    private void pickRoute() {
        SelectiveShoot.Alliance alliance = prompter.get("alliance");

        if (alliance == SelectiveShoot.Alliance.RED) {
            TransferClass.alliance = TransferClass.Alliance.RED;

            SelectiveShoot.RedPositions pos = prompter.get("position");
            SelectiveShoot.RedParking park = prompter.get("parkingPosition");
            boolean garden = prompter.get("intakeFromGarden");

            if (pos == RedPositions.RED_AUDIENCE_DEFAULT_UP) {
                follower.setPose(redAudience);
                pathQueue.offer(line(redAudience, redAudienceShoot).linear(redAudience, redAudienceShoot));
            } else if (pos == RedPositions.RED_SCORING_DEFAULT_DOWN) {
                follower.setPose(redScoring);
                pathQueue.offer(line(redScoring, redScoringShoot).linear(redScoring, redScoringShoot));
            }

            if (garden) {
                Pose redGardenEnd = poseFactory.of(12.0, 10.0, -90);

                if (pos == RedPositions.RED_AUDIENCE_DEFAULT_UP) {
                    pathQueue.offer(GardenPaths.redAudienceToRedGarden);
                    pathQueue.offer(GardenPaths.ramRedGarden);
                    pathQueue.offer(line(redGardenEnd, redAudienceShoot).linear(redGardenEnd, redAudienceShoot));
                    pathQueue.offer(park == RedParking.RED_CORNER_PARKING ? ParkingPaths.redAudienceToCornerRedPark : ParkingPaths.redAudienceToExposedRedPark);
                } else if (pos == RedPositions.RED_SCORING_DEFAULT_DOWN) {
                    pathQueue.offer(GardenPaths.redScoringToRedGarden);
                    pathQueue.offer(GardenPaths.ramRedGarden);
//                    pathQueue.offer(line(redGardenEnd, redScoringShoot).linear(redGardenEnd, redScoringShoot)); // todo - broken FIX
                    pathQueue.offer(GardenPaths.redGardenToRedScoring);
                    pathQueue.offer(park == RedParking.RED_CORNER_PARKING ? ParkingPaths.redScoringToCornerRedPark : ParkingPaths.redScoringToExposedRedPark);
                }
            } else {
                if (pos == RedPositions.RED_AUDIENCE_DEFAULT_UP) {
                    pathQueue.offer(park == RedParking.RED_CORNER_PARKING ? ParkingPaths.redAudienceToCornerRedPark : ParkingPaths.redAudienceToExposedRedPark);
                } else if (pos == RedPositions.RED_SCORING_DEFAULT_DOWN) {
                    pathQueue.offer(park == RedParking.RED_CORNER_PARKING ? ParkingPaths.redScoringToCornerRedPark : ParkingPaths.redScoringToExposedRedPark);
                }
            }

        } else if (alliance == SelectiveShoot.Alliance.BLUE) {
            TransferClass.alliance = TransferClass.Alliance.BLUE;

            SelectiveShoot.BluePositions pos = prompter.get("position");
            SelectiveShoot.BlueParking park = prompter.get("parkingPosition");
            boolean garden = prompter.get("intakeFromGarden");

            if (pos == BluePositions.BLUE_AUDIENCE_DEFAULT_DOWN) {
                follower.setPose(blueAudience);
                pathQueue.offer(line(blueAudience, blueAudienceShoot).linear(blueAudience, blueAudienceShoot));
            } else if (pos == BluePositions.BLUE_SCORING_DEFAULT_UP) {
                follower.setPose(blueScoring);
                pathQueue.offer(line(blueScoring, blueScoringShoot).linear(blueScoring, blueScoringShoot));
            }

            if (garden) {
                Pose blueGardenEnd = poseFactory.of(132.0, 134.0, 90);

                if (pos == BluePositions.BLUE_AUDIENCE_DEFAULT_DOWN) {
                    pathQueue.offer(GardenPaths.blueShootingToBlueGarden);
                    pathQueue.offer(GardenPaths.ramBlueGarden);
//                    pathQueue.offer(line(blueGardenEnd, blueAudienceShoot).linear(blueGardenEnd, blueAudienceShoot)); // todo - broken FIX
                    pathQueue.offer(GardenPaths.blueGardenToBlueAudience);
                    pathQueue.offer(park == BlueParking.BLUE_CORNER_PARKING ? ParkingPaths.blueAudienceToCornerBluePark : ParkingPaths.blueAudienceToExposedBluePark);
                } else if (pos == BluePositions.BLUE_SCORING_DEFAULT_UP) {
                    pathQueue.offer(GardenPaths.blueScoringToBlueGarden);
                    pathQueue.offer(GardenPaths.ramBlueGarden);
                    pathQueue.offer(line(blueGardenEnd, blueScoringShoot).linear(blueGardenEnd, blueScoringShoot));
                    pathQueue.offer(park == BlueParking.BLUE_CORNER_PARKING ? ParkingPaths.blueScoringToCornerBluePark : ParkingPaths.blueScoringToExposedBluePark);
                }
            } else {
                if (pos == BluePositions.BLUE_AUDIENCE_DEFAULT_DOWN) {
                    pathQueue.offer(park == BlueParking.BLUE_CORNER_PARKING ? ParkingPaths.blueAudienceToCornerBluePark : ParkingPaths.blueAudienceToExposedBluePark);
                } else if (pos == BluePositions.BLUE_SCORING_DEFAULT_UP) {
                    pathQueue.offer(park == BlueParking.BLUE_CORNER_PARKING ? ParkingPaths.blueScoringToCornerBluePark : ParkingPaths.blueScoringToExposedBluePark);
                }
            }
        }
    }

    @Override
    public void init_loop() {
        prompter.run();
        headingLock.configureVisionPortal();
    }

    @Override
    public void start() {
        if (pathQueue.isEmpty()) {
            pickRoute();
        }

        delayTimer = new ElapsedTime();
        autoTimer = new ElapsedTime();
        stepTimer = new ElapsedTime();
        delayTimer.reset();
        autoTimer.reset();
        wait.reset();

        aprilTagWaitStarted = false;
        expectedAprilTagFound = false;

        shooter.getControllerObject().reset();
    }

    @Override
    public void loop() {
        LynxUtil.clearBulkCache(hardwareMap);

        follower.update();
        TransferClass.setRobotPose(follower.pose());

        shooter.update();

//        telemetry.addLine("Target RPM: " + shooter.getTargetRpm());
//        telemetry.addLine("Current RPM: " + shooter.getCurrentRpm());
//        telemetry.addLine("Motor Power: " + shooter.getMotorObject().getPower());
//        telemetry.addLine("Motor Enabled: " + shooter.getMotorObject().isMotorEnabled());

        switch (step) {
            case -2:
                telemetry.addLine("Autoparking...");
                transfer.setMotorState(Transfer.MotorState.STOP);
                intake.setState(Intake.State.STOP);
                shooter.setTargetRpm(0);

                if(!follower.isBusy()) {
                    step = -1;
                }
                break;
            case -1:
                transfer.setMotorState(Transfer.MotorState.STOP);
                intake.setState(Intake.State.STOP);
                shooter.setTargetRpm(0);

                telemetry.addLine("Finished Auto");
                TransferClass.setRobotPose(follower.pose());
                break;
            case 0:
                telemetry.addLine("Waiting for start delay: " + Round.round(delayTimer.seconds(), 2) + "s / " + prompter.get("startDelay") + "s");
                if(delayTimer.seconds() >= (double) prompter.get("startDelay")) {
                    telemetry.clearAll();
                    telemetry.addLine("Navigating to shoot pose");
                    nextStep();
                }
                break;
            case 1:
                if (!aprilTagWaitStarted) {
                    stepTimer.reset();
                    aprilTagWaitStarted = true;
                }

                List<AprilTagDetection> detections = headingLock.getAprilTagProcessor().getDetections();
                String expectedClusterName = getExpectedAprilTagClusterName();

                if (detections != null) {
                    for (AprilTagDetection detection : detections) {
                        if (detection instanceof AprilTagClusterDetection) {
                            AprilTagClusterDetection cluster = (AprilTagClusterDetection) detection;

                            if (cluster.metadata != null && expectedClusterName.equalsIgnoreCase(cluster.metadata.name) && detection.ftcPose != null && Math.abs(detection.ftcPose.roll) < 90.0) { // todo - CHANGE, maybe get rid of the roll test???
                                expectedAprilTagFound = true;
                                break;
                            } // l o n g
                        }
                    }
                }

                double aprilTagTimeout = prompter.get("aprilTagTimeout");
                boolean aprilTagTimedOut = stepTimer.seconds() >= aprilTagTimeout;

                telemetry.addLine("Expected AprilTag cluster: " + expectedClusterName);
                telemetry.addLine("Scorable target found: " + expectedAprilTagFound);
                telemetry.addLine("AprilTag wait: " + Round.round(stepTimer.seconds(), 2) + "s / " + aprilTagTimeout + "s");

                if (expectedAprilTagFound || aprilTagTimedOut) {
                    follower.follow(pathQueue.poll());
                    telemetry.addLine("Shooting... (Navigating to shoot pose)");
                    nextStep();
                } else {
                    telemetry.addLine("Waiting for AprilTag detection at start position...");
                }
                break;
            case 2:
                if (!follower.isBusy()) {
                    if (shooter.isReady()) {
                        telemetry.addLine("Shooting... (Running transfer and intake)");
                        transfer.setMotorState(Transfer.MotorState.FORWARD);
                        intake.setState(Intake.State.INTAKE);
                        telemetry.addLine("Shooting... (Waiting 2s)");
                        nextStep();
                    } else {
                        telemetry.addLine("Shooting... (Waiting for speed)");
                    }
                } else {
                    telemetry.addLine("Navigating to shoot pose...");
                }
                break;
            case 3:
                if(wait.waitSeconds(2.0)) {
                    telemetry.addLine("Picking next path...");
                    nextStep();
                }
                break;
            case 4:
                follower.follow(pathQueue.poll());
                transfer.setMotorState(Transfer.MotorState.STOP);

                if(!(boolean) prompter.get("intakeFromGarden")) {
                    telemetry.addLine("Parking");
                    intake.setState(Intake.State.STOP);
                    shooter.setTargetRpm(0);
                } else {
                    telemetry.addLine("Navigating to garden");
                }

                nextStep();
                break;
            case 5:
                if(!follower.isBusy()) {
                    if(prompter.get("intakeFromGarden")) {
                        telemetry.addLine("Ramming to garden");
                        follower.follow(pathQueue.poll());
                        nextStep();
                    } else {
                        nextStep(-1);
                    }
                }
                break;
            case 6:
                if(!follower.isBusy() || stepTimer.seconds() >= 2) {
                    telemetry.addLine("Navigating back to shooting position");
                    follower.follow(pathQueue.poll());
                    nextStep();
                }
                break;
            case 7:
                if (!follower.isBusy()) {
                    if (shooter.isReady()) {
                        telemetry.addLine("Shooting... (Running transfer and intake)");
                        transfer.setMotorState(Transfer.MotorState.FORWARD);
                        intake.setState(Intake.State.INTAKE);
                        nextStep();
                    }
                }
                break;
            case 8:
                if(wait.waitSeconds(2.0)) {
                    telemetry.addLine("Parking");
                    nextStep();
                }
                break;
            case 9:
                follower.follow(pathQueue.poll());
                nextStep();
                break;
            case 10:
                if(!follower.isBusy()) {
                    nextStep(-1);
                }
                break;
        }

        if(autoTimer.seconds() >= 25 && (boolean) prompter.get("timeAutoPark") && step != -2 && getSelectedParkingPose().distance(follower.pose()) > 5) {
            PathResult result = Weaver.builder()
                .start(new Point(follower.pose().x(), follower.pose().y(), follower.pose().heading()))
                .addTarget(new Point(getSelectedParkingPose().x(), getSelectedParkingPose().y()))
                .addObstacle(hiveRedSide)
                .addObstacle(hiveBlueSide)
                .build();

            follower.follow(PedroPathingConverter.toPath(result));
            nextStep(-2);
        }
    }

    @Override
    public void stop() {
        headingLock.getVisionPortal().close();
    }

    private String getExpectedAprilTagClusterName() {
        Alliance alliance = prompter.get("alliance");

        if (alliance == Alliance.RED) {
            RedPositions position = prompter.get("position");
            return position == RedPositions.RED_AUDIENCE_DEFAULT_UP ? "RED AUDIENCE" : "RED SCORING";
        } else if(alliance == Alliance.BLUE) {
            BluePositions position = prompter.get("position");
            return position == BluePositions.BLUE_AUDIENCE_DEFAULT_DOWN ? "BLUE AUDIENCE" : "BLUE SCORING";
        }

        return ""; // todo - ??? change?
    }

    private Pose getSelectedParkingPose() {
        Alliance alliance = prompter.get("alliance");

        if (alliance == Alliance.RED) {
            RedParking parking = prompter.get("parkingPosition");
            return parking == RedParking.RED_CORNER_PARKING ? cornerRedPark : exposedRedPark;
        } else {
            BlueParking parking = prompter.get("parkingPosition");
            return parking == BlueParking.BLUE_CORNER_PARKING ? cornerBluePark : exposedBluePark;
        }
    }

    private void nextStep() {
        stepTimer.reset();
        step++;
    }

    private void nextStep(int step) {
        stepTimer.reset();
        this.step = step;
    }
}
