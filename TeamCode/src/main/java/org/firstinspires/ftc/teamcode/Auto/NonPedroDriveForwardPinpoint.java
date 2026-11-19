package org.firstinspires.ftc.teamcode.Auto;

import com.pedropathing.utils.Angle;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.skeletonarmy.marrow.prompts.OptionPrompt;
import com.skeletonarmy.marrow.prompts.Prompter;
import com.skeletonarmy.marrow.prompts.ValuePrompt;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.Utils.PIDFController;

@Autonomous(name = "Non Pedro Drive Forwards Pinpoint (Distance)", group = "Auto")
public class NonPedroDriveForwardPinpoint extends OpMode {
    GoBildaPinpointDriver pinpointDriver;
    DcMotor bl;
    DcMotor br;
    DcMotor fl;
    DcMotor fr;
    int step = 0;

    Prompter prompter = new Prompter(this);
    ElapsedTime delayTimer;
    ElapsedTime driveTimer;

    PIDFController headingController;

    @Override
    public void init() {
        bl = hardwareMap.get(DcMotor.class, "bl");
        br = hardwareMap.get(DcMotor.class, "br");
        fl = hardwareMap.get(DcMotor.class, "fl");
        fr = hardwareMap.get(DcMotor.class, "fr");

        bl.setDirection(DcMotorSimple.Direction.FORWARD);
        br.setDirection(DcMotorSimple.Direction.REVERSE);
        fl.setDirection(DcMotorSimple.Direction.FORWARD);
        fr.setDirection(DcMotorSimple.Direction.REVERSE);

        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.setDisplayFormat(Telemetry.DisplayFormat.HTML);

        pinpointDriver = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        pinpointDriver.resetPosAndIMU();
        pinpointDriver.setPosition(new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0));

        headingController = new PIDFController(1.2, 0, 0.05, 0);

        prompter.prompt("startDelay", new ValuePrompt<>("Start delay (seconds) before first action is taken", Double.class, 0.0, 20.0, 0.0, 0.5))
                .label("Start Delay (s)")
                .prompt("distance", new ValuePrompt<>("Distance (in) to travel forwards", Double.class, 0.0, 999.0, 0.0, 1.0))
                .label("Distance (in)")
                .showSummary();
    }

    @Override
    public void init_loop() {
        pinpointDriver.update();
        prompter.run();
    }

    @Override
    public void start() {
        delayTimer = new ElapsedTime();
        driveTimer = new ElapsedTime();
        delayTimer.reset();
        driveTimer.reset();
        pinpointDriver.setPosition(new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0));

        pinpointDriver.update();
    }

    @Override
    public void loop() {
        pinpointDriver.update();

        switch(step) {
            case -1:
                telemetry.addLine("Finished Auto");
                fl.setPower(0);
                bl.setPower(0);
                fr.setPower(0);
                br.setPower(0);
                break;
            case 0:
                telemetry.addLine("Waiting for start delay: " + delayTimer.seconds() + "s / " + prompter.get("startDelay") + "s");
                if(delayTimer.seconds() >= (double) prompter.get("startDelay")) {
                    telemetry.clearAll();
                    step++;
                    driveTimer.reset();
                }
                break;
            case 1:
                if(pinpointDriver.getPosX(DistanceUnit.INCH) >= (double) prompter.get("distance")-1) {
                    step = -1;
                }

//                double y = -gamepad1.left_stick_y;
//                double x = gamepad1.left_stick_x * 1.1;
//                double rx = gamepad1.right_stick_x;

                double currentHeading = pinpointDriver.getHeading(AngleUnit.RADIANS);
                double targetHeading = 0.0;
                double angleError = Angle.normalizeSigned(targetHeading - currentHeading);

                double correction = -headingController.update(currentHeading, targetHeading);

                double y = 0.25;
                double x = 0;
                double rx = correction;
//                double rx = 0;

                double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
                double frontLeftPower  = (y + x + rx) / denominator;
                double backLeftPower   = (y - x + rx) / denominator;
                double frontRightPower = (y - x - rx) / denominator;
                double backRightPower  = (y + x - rx) / denominator;

                fl.setPower(frontLeftPower);
                bl.setPower(backLeftPower);
                fr.setPower(frontRightPower);
                br.setPower(backRightPower);
                break;
        }
    }

    @Override
    public void stop() {
        fl.setPower(0);
        bl.setPower(0);
        fr.setPower(0);
        br.setPower(0);
    }
}
