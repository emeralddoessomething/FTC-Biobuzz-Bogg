package org.firstinspires.ftc.teamcode.TurtleTracer;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.paths.Path;

public class GardenPaths {
    public static PoseFactory poseFactory = PoseFactory.degrees();

    public static Path ramRedGarden = line(
        poseFactory.of(12.000, 15.000, 0.0),
        poseFactory.of(12.000, 12.000, 0.0))
        .constant(Math.toRadians(-90)
    );

    public static Path ramBlueGarden = line(
        poseFactory.of(132.000, 129.000, 0.0),
        poseFactory.of(132.000, 132.000, 0.0))
        .constant(Math.toRadians(90)
    );

    public static Path redAudienceToRedGarden = line(
        poseFactory.of(60.000, 36.000, 0.0),
        poseFactory.of(12.000, 15.000, 0.0))
        .linear(Math.toRadians(90), Math.toRadians(-90)
    );

    public static Path redScoringToRedGarden = curve(
        poseFactory.of(60.000, 108.000, 0.0),
        poseFactory.of(13.353, 109.928, 0.0),
        poseFactory.of(32.757, 48.298, 0.0),
        poseFactory.of(12.000, 15.000, 0.0))
        .linear(Math.toRadians(-90), Math.toRadians(-90)
    );

    public static Path blueScoringToBlueGarden = line(
        poseFactory.of(84.000, 108.000, 0.0),
        poseFactory.of(132.000, 129.000, 0.0))
        .linear(Math.toRadians(-90), Math.toRadians(90)
    );

    public static Path blueShootingToBlueGarden = curve(
        poseFactory.of(84.000, 36.000, 0.0),
        poseFactory.of(126.961, 29.488, 0.0),
        poseFactory.of(112.558, 98.761, 0.0),
        poseFactory.of(132.000, 129.000, 0.0))
        .linear(Math.toRadians(90), Math.toRadians(90)
    );

    public static Path blueGardenToBlueAudience = curve(
        poseFactory.of(132.000, 132.000, 0.0),
        poseFactory.of(118.829, 100.144, 0.0),
        poseFactory.of(128.632, 25.206, 0.0),
        poseFactory.of(84.000, 36.000, 0.0))
        .linear(Math.toRadians(90), Math.toRadians(90)
    );

    // red garden to red scoring
     public static Path redGardenToRedScoring = curve(
        poseFactory.of(12.000, 12.000, 0.0),
        poseFactory.of(51.000, 82.000, 0.0),
        poseFactory.of(8.783, 120.876, 0.0),
        poseFactory.of(60.000, 108.000, 0.0))
        .linear(Math.toRadians(-90), Math.toRadians(-90)
    );
}
