package org.firstinspires.ftc.teamcode.Tests;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

@Disabled
@TeleOp(name = "Pinpoint Driver Test", group = "Tests")
public class PinpointDriverTest extends OpMode {
    GoBildaPinpointDriver pinpointDriver;

    @Override
    public void init() {
        pinpointDriver = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        pinpointDriver.setPosition(new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0));
    }

    @Override
    public void loop() {
        pinpointDriver.update();
        telemetry.addLine("X (In):" + pinpointDriver.getPosX(DistanceUnit.INCH));
        telemetry.addLine("Y (In):" + pinpointDriver.getPosY(DistanceUnit.INCH));
        telemetry.addLine("Heading (Deg):" + pinpointDriver.getHeading(AngleUnit.DEGREES));
    }
}
