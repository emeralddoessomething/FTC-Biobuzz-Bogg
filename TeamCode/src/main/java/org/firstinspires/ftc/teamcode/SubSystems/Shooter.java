package org.firstinspires.ftc.teamcode.SubSystems;

import static com.pedropathing.utils.Utils.clamp;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Utils.PIDFController;

@Configurable
public class Shooter {
    private DcMotorEx flywheel;
    private PIDFController controller;
    private double targetRpm;
    private double currentRpm;
    private double pprAtOutputShaft;

    public static double p = 0.0;
    public static double i = 0.0;
    public static double d = 0.0;
    public static double f = 0.0;

    public Shooter(HardwareMap hardwareMap) {
        String hardwareMapName = "flywheel";
        DcMotorEx.Direction direction = DcMotorSimple.Direction.FORWARD;
        DcMotorEx.ZeroPowerBehavior zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE;
        pprAtOutputShaft = 28; // 6k rpm

        flywheel = hardwareMap.get(DcMotorEx.class, hardwareMapName);
        flywheel.setDirection(direction);
        flywheel.setZeroPowerBehavior(zeroPowerBehavior);

        controller = new PIDFController(p, i, d, f);
    }

    public void setTargetRpm(double targetRpm) {
        this.targetRpm = targetRpm;
    }

    public double getCurrentRpm() {
        calculateRpm();
        return currentRpm;
    }

    public void bumpRpm(double bumpAmount) {
        targetRpm += bumpAmount;
    }

    public boolean isReady() {
        return (Math.abs(getCurrentRpm() - targetRpm) <= 20) && targetRpm != 0;
    }

    public void update() {
        calculateRpm();
        double controlerOutput = controller.update(currentRpm, targetRpm);
        flywheel.setPower(clamp(controlerOutput, 0, 1));
    }

    private void calculateRpm() {
        currentRpm = (flywheel.getVelocity() / pprAtOutputShaft) * 60;
    }

    public void updateControllerConstants() {
        controller.setPIDF(p, i, d, f);
    }

    public DcMotorEx getMotorObject() {
        return flywheel;
    }
}
