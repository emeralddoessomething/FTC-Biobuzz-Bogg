package org.firstinspires.ftc.teamcode.Pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
//    public static Follower create(HardwareMap h) {
////         return new Follower(Drivetrain, Localizer, Foresight);
//        return null;
//    }

    public static Follower create(HardwareMap h) {
        return new Follower(
            new PinpointLocalizer(h, localizerConfig),
            new Mecanum(h, drivetrainConfig),
            new Foresight(foresightConfig)
        );
    }

    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set("fl");
        c.frontRightName.set("fr");
        c.backLeftName.set("bl");
        c.backRightName.set("br");
        c.frontLeftDirection.set(DcMotorSimple.Direction.FORWARD);
        c.frontRightDirection.set(DcMotorSimple.Direction.REVERSE);
        c.backLeftDirection.set(DcMotorSimple.Direction.FORWARD);
        c.backRightDirection.set(DcMotorSimple.Direction.REVERSE);
    });

    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        c.xPodOffset.set(-0.007649563311591862);
        c.yPodOffset.set(-4.882705568328618);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });

    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.22926136044823425);
                Controller secondaryTranslationalForward = Controller.proportional(0.08470594670924766);
                Controller primaryTranslationalLateral = Controller.proportional(0.45575883149627633);
                Controller secondaryTranslationalLateral = Controller.proportional(0.16839071013760926);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.016752784726467927));
                c.brake.set(Controller.proportionalFeedforward(0.014239867017497737));

                c.headingFeedback.set(Controller.proportional(3.463803958328009));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.045013062175888026, 0.006466367737711944));

                c.linearBrakeCoefficients.set(Matrix.diag(0.06032157898411338, 0.0655214460591008));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.0020773922035335537, 0.001442447186540971));

                c.maxAchievableForwardVelocity.set(59.14101219140135);
                c.maxAchievableStrafeVelocity.set(38.216811641993665);
                c.naturalForwardDeceleration.set(32.41664152878648);
                c.naturalStrafeDeceleration.set(50.50712138416251);
            }
    );
}