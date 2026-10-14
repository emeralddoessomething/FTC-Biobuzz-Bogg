package org.firstinspires.ftc.teamcode.TurtleTracer;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;
import static com.pedropathing.api.Paths.path;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.paths.Path;

public class ParkingPaths {
    public static PoseFactory poseFactory = PoseFactory.degrees();
    public static Path redAudienceToExposedRedPark = curve(
        poseFactory.of(59.500, 10.250, 0.0),
        poseFactory.of(24.349, 52.748, 0.0),
        poseFactory.of(44.924, 20.643, 0.0),
        poseFactory.of(10.250, 96.000, 0.0))
        .linear(Math.toRadians(90), Math.toRadians(90)
    );

    public static Path redScoringToExposedRedPark = path(
        line(
            poseFactory.of(60.838, 133.817, 0.0),
            poseFactory.of(60.838, 126.433, 0.0))
            .constant(Math.toRadians(-90)),
        curve(
            poseFactory.of(60.838, 126.433, 0.0),
            poseFactory.of(57.129, 117.146, 0.0),
            poseFactory.of(10.250, 96.000, 0.0))
            .constant(Math.toRadians(-90))
    );

    public static Path redScoringToCornerRedPark = path(
        line(
            poseFactory.of(60.838, 133.817, 0.0),
            poseFactory.of(60.838, 126.433, 0.0))
            .constant(Math.toRadians(-90)),
        curve(
            poseFactory.of(60.838, 126.433, 0.0),
            poseFactory.of(56.890, 117.245, 0.0),
            poseFactory.of(10.250, 118.750, 0.0))
            .constant(Math.toRadians(-90))
    );

    public static Path redAudienceToCornerRedPark = curve(
        poseFactory.of(60.838, 10.250, 0.0),
        poseFactory.of(25.723, 39.263, 0.0),
        poseFactory.of(10.250, 118.750, 0.0))
        .constant(Math.toRadians(90)
    );

    public static Path blueAudienceToCornerBluePark = path(
        line(poseFactory.of(82.820, 10.169, 0.0),
            poseFactory.of(82.820, 18.561, 0.0))
            .constant(Math.toRadians(90)),
        curve(
            poseFactory.of(82.820, 18.561, 0.0),
            poseFactory.of(86.618, 27.812, 0.0),
            poseFactory.of(133.750, 25.250, 0.0))
            .constant(Math.toRadians(90))
    );

    public static Path blueAudienceToExposedBluePark = curve(
        poseFactory.of(82.820, 10.169, 0.0),
        poseFactory.of(82.772, 20.169, 0.0),
        poseFactory.of(133.750, 48.000, 0.0))
        .constant(Math.toRadians(90)
    );

    public static Path blueScoringToExposedBluePark = curve(
        poseFactory.of(85.000, 133.750, 0.0),
        poseFactory.of(120.034, 90.950, 0.0),
        poseFactory.of(99.850, 123.761, 0.0),
        poseFactory.of(133.750, 48.000, 0.0))
        .constant(Math.toRadians(-90)
    );

    public static Path blueScoringToCornerBluePark = curve(
        poseFactory.of(85.000, 133.750, 0.0),
        poseFactory.of(119.750, 96.647, 0.0),
        poseFactory.of(104.432, 113.036, 0.0),
        poseFactory.of(133.750, 25.250, 0.0))
        .constant(Math.toRadians(-90)
    );
}