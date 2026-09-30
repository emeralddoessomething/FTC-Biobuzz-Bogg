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
//        // return new Follower(Drivetrain, Localizer, Foresight);
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
        c.xPodOffset.set(-5.9112849197988435);
        c.yPodOffset.set(1.1702307753675567);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });

    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.1919674965773457);
                Controller secondaryTranslationalForward = Controller.proportional(0.07092686051934996);
                Controller primaryTranslationalLateral = Controller.proportional(0.3168606131914094);
                Controller secondaryTranslationalLateral = Controller.proportional(0.11707152990270839);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.016514716540109338));
                c.brake.set(Controller.proportionalFeedforward(0.014037509059092937));

                c.headingFeedback.set(Controller.proportional(2.3565829384709174));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.05235323202355063, 0.005952293482571279));

                c.linearBrakeCoefficients.set(Matrix.diag(0.05030247322821219, 0.02582530465076312));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.001901920741887132, 0.0032694481441997714));

                c.maxAchievableForwardVelocity.set(61.2723543797649);
                c.maxAchievableStrafeVelocity.set(48.31256616455328);
                c.naturalForwardDeceleration.set(34.01989687907285);
                c.naturalStrafeDeceleration.set(42.29962777993556);
            }
    );
}