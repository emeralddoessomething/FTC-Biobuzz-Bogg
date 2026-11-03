//package org.firstinspires.ftc.teamcode.Auto;
//
//import com.pedropathing.api.PoseFactory;
//import com.pedropathing.follower.Follower;
//import com.pedropathing.math.Pose;
//import com.pedropathing.paths.Path;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//import com.qualcomm.robotcore.util.ElapsedTime;
//import com.skeletonarmy.marrow.prompts.OptionPrompt;
//import com.skeletonarmy.marrow.prompts.Prompter;
//import com.skeletonarmy.marrow.prompts.ValuePrompt;
//
//import org.firstinspires.ftc.teamcode.Pedro.Constants;
//import org.firstinspires.ftc.teamcode.TurtleTracer.ParkingPaths;
//import org.firstinspires.ftc.teamcode.Utils.HtmlTelemetryUtil;
//
//@Autonomous(name = "Short Selective Park", group = "Auto")
//public class ShortSelectivePark extends OpMode {
//    private Follower follower;
//    private ElapsedTime delayTimer;
//    private ElapsedTime autoTimer;
//    private int step = 0;
//
//    private PoseFactory poseFactory = PoseFactory.degrees();
//    private Pose redCornerStart = poseFactory.of();
//    private Pose redExposedStart = poseFactory.of();
//    private Pose blueCornerStart = poseFactory.of();
//    private Pose blueExposedStart = poseFactory.of();
//    private Pose cornerRedPark = poseFactory.of(10.25, 118.75, 0); // todo - heading doesn't really matter
//    private Pose exposedRedPark = poseFactory.of(10.25, 96, 0); // todo - heading doesn't really matter
//    private Pose cornerBluePark = poseFactory.of(133.75, 25.25, 0); // todo - heading doesn't really matter
//    private Pose exposedBluePark = poseFactory.of(133.75, 48, 0); // todo - heading doesn't really matter
//
//    private Path selectedPath;
//
//    Prompter prompter = new Prompter(this);
//
//    enum Alliance {
//        RED,
//        BLUE
//    }
//    enum RedPositions {
//        RED_PARK_CORNER,
//        RED_PARK_EXPOSED
//    }
//    enum BluePositions {
//        BLUE_PARK_CORNER,
//        BLUE_PARK_EXPOSED
//    }
//    enum RedParking {
//        RED_CORNER_PARKING,
//        RED_EXPOSED_PARKING
//    }
//    enum BlueParking {
//        BLUE_CORNER_PARKING,
//        BLUE_EXPOSED_PARKING
//    }
//
//    @Override
//    public void init() {
//        follower = Constants.create(hardwareMap);
//
////        telemetry.setDisplayFormat(Telemetry.DisplayFormat.HTML);
//        telemetry.setMsTransmissionInterval(500); // To not drag loop times
//        telemetry.addLine("Starting init...");
////        telemetry.addLine(HtmlTelemetryUtil.constructString(HtmlTelemetryUtil.Size.h1, "This auto will go and park based on selection"));
//        telemetry.update();
//
//        // Reference https://marrow.skeletonarmyftc.com/dev-tools/prompter
//        prompter.prompt("alliance", new OptionPrompt<>("Alliance", SelectivePark.Alliance.RED, SelectivePark.Alliance.BLUE))
//                .label("Alliance")
//                .prompt("position", new OptionPrompt<>("Position", SelectivePark.RedPositions.class))
//                .showIf("alliance", SelectivePark.Alliance.RED)
//                .label("Position")
//                .prompt("position", new OptionPrompt<>("Position", SelectivePark.BluePositions.class))
//                .showIf("alliance", SelectivePark.Alliance.BLUE)
//                .label("Position")
//                .prompt("parkingPosition", new OptionPrompt<>("Parking Position", SelectivePark.RedParking.class))
//                .showIf("alliance", SelectivePark.Alliance.RED)
//                .label("Parking Position")
//                .prompt("parkingPosition", new OptionPrompt<>("Parking Position", SelectivePark.BlueParking.class))
//                .showIf("alliance", SelectivePark.Alliance.BLUE)
//                .label("Parking Position")
//                .prompt("startDelay", new ValuePrompt<>("Start delay (seconds) before first action is taken", Double.class, 0.0, 20.0, 0.0, 0.5))
//                .label("Start Delay (s)")
//                .showSummary()
//                .onComplete(this::pickPath);
//    }
//
//    private void pickPath() {
//        SelectivePark.Alliance alliance = prompter.get("alliance");
//
//        if (alliance == SelectivePark.Alliance.RED) {
//            SelectivePark.RedPositions pos = prompter.get("position");
//            SelectivePark.RedParking park = prompter.get("parkingPosition");
//
//            if (pos == SelectivePark.RedPositions.RED_AUDIENCE_DEFAULT_UP) {
//                selectedPath = (park == SelectivePark.RedParking.RED_CORNER_PARKING) ? ParkingPaths.redAudienceToCornerRedPark : ParkingPaths.redAudienceToExposedRedPark;
//            } else if (pos == SelectivePark.RedPositions.RED_SCORING_DEFAULT_DOWN) {
//                selectedPath = (park == SelectivePark.RedParking.RED_CORNER_PARKING) ? ParkingPaths.redScoringToCornerRedPark : ParkingPaths.redScoringToExposedRedPark;
//            }
//        } else if (alliance == SelectivePark.Alliance.BLUE) {
//            SelectivePark.BluePositions pos = prompter.get("position");
//            SelectivePark.BlueParking park = prompter.get("parkingPosition");
//
//            if (pos == SelectivePark.BluePositions.BLUE_AUDIENCE_DEFAULT_DOWN) {
//                selectedPath = (park == SelectivePark.BlueParking.BLUE_CORNER_PARKING) ? ParkingPaths.blueAudienceToCornerBluePark : ParkingPaths.blueAudienceToExposedBluePark;
//            } else if (pos == SelectivePark.BluePositions.BLUE_SCORING_DEFAULT_UP) {
//                selectedPath = (park == SelectivePark.BlueParking.BLUE_CORNER_PARKING) ? ParkingPaths.blueScoringToCornerBluePark : ParkingPaths.blueScoringToExposedBluePark;
//            }
//        }
//
//        follower.setPose(selectedPath.curve.startPoint().toPose(selectedPath.heading(0.0)));
//    }
//
//    @Override
//    public void init_loop() {
//        prompter.run();
//    }
//
//    @Override
//    public void start() {
//        delayTimer = new ElapsedTime();
//        autoTimer = new ElapsedTime();
//        delayTimer.reset();
//        autoTimer.reset();
//    }
//
//    @Override
//    public void loop() {
//        switch(step) {
//            case -1:
//                telemetry.addLine("Finished Auto");
//                break;
//            case 0:
//                telemetry.addLine("Waiting for start delay: " + delayTimer.seconds() + "s / " + prompter.get("startDelay") + "s");
//                if(delayTimer.seconds() >= (double) prompter.get("startDelay")) {
//                    telemetry.clearAll();
//                    step++;
//                }
//                break;
//            case 1:
//                follower.follow(selectedPath);
//                step++;
//                break;
//            case 2:
//                if(!follower.isBusy()) {
//                    // todo - add marrow zones park double check
//                    step = -1;
//                }
//                break;
//        }
//
//        if(autoTimer.seconds() >= 25 && follower.idle() && selectedPath.curve.startPoint().distance(follower.pose().toVector2D()) < 5) {
//            telemetry.addLine(HtmlTelemetryUtil.constructString(HtmlTelemetryUtil.Size.h1, HtmlTelemetryUtil.Colors.red, "Time Based Auto Park"));
//            step = 1;
//        }
//
//        follower.update();
//    }
//}
