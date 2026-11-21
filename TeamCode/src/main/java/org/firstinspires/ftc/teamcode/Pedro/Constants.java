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
        c.xPodOffset.set(3.9850727591927595);
        c.yPodOffset.set(2.0510337108702172);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });

    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.33492716325435185);
                Controller secondaryTranslationalForward = Controller.proportional(0.12374663740385705);
                Controller primaryTranslationalLateral = Controller.proportional(0.5175025674073868);
                Controller secondaryTranslationalLateral = Controller.proportional(0.1912033707337558);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.01250479679764623));
                c.brake.set(Controller.proportionalFeedforward(0.010629077277999296));

                c.headingFeedback.set(Controller.proportional(5.351840587231502));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.04249871153451954, 0.008666553861823724));

                c.linearBrakeCoefficients.set(Matrix.diag(0.08910654550801322, 0.04325098589085949));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.001480092767648996, 0.0023454107110415395));

                c.maxAchievableForwardVelocity.set(77.18235187193936);
                c.maxAchievableStrafeVelocity.set(58.490511583996714);
                c.naturalForwardDeceleration.set(34.78499666270489);
                c.naturalStrafeDeceleration.set(75.7931548421144);
            }
    );
}