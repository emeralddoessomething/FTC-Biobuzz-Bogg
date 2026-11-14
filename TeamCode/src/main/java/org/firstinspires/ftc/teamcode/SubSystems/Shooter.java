package org.firstinspires.ftc.teamcode.SubSystems;

import static com.pedropathing.utils.Utils.clamp;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Utils.PIDFController;

import dev.frozenmilk.dairy.cachinghardware.CachingDcMotorEx;

@Configurable
public class Shooter {
    private DcMotorEx upperFlywheel;
    private DcMotorEx lowerFlywheel;
    private PIDFController controller;
    private double targetRpm;
    private double currentRpm;
    private double pprAtOutputShaft;

    public static double p = 0.1;
    public static double i = 0.0;
    public static double d = 0.0;
    public static double f = 0.0;

    public Shooter(HardwareMap hardwareMap) {
        String upperHardwareMapName = "upperFlywheel";
        DcMotorEx.Direction upperDirection = DcMotorSimple.Direction.FORWARD;

        String lowerHardwareMapName = "lowerFlywheel";
        DcMotorEx.Direction lowerDirection = DcMotorSimple.Direction.REVERSE;

        DcMotorEx.ZeroPowerBehavior zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT;
        pprAtOutputShaft = 28; // 6k rpm

//        upperFlywheel = hardwareMap.get(DcMotorEx.class, upperHardwareMapName);
        upperFlywheel = new CachingDcMotorEx(hardwareMap.get(DcMotorEx.class, upperHardwareMapName));
        upperFlywheel.setDirection(upperDirection);
        upperFlywheel.setZeroPowerBehavior(zeroPowerBehavior);

//        lowerFlywheel = hardwareMap.get(DcMotorEx.class, lowerHardwareMapName);
        lowerFlywheel = new CachingDcMotorEx(hardwareMap.get(DcMotorEx.class, lowerHardwareMapName));
        lowerFlywheel.setDirection(lowerDirection);
        lowerFlywheel.setZeroPowerBehavior(zeroPowerBehavior);

        controller = new PIDFController(p, i, d, f);
    }

    public void setTargetRpm(double targetRpm) {
        this.targetRpm = targetRpm;
    }

    public double getCurrentRpm() {
        calculateRpm();
        return currentRpm;
    }

    public double getTargetRpm() {
        return targetRpm;
    }

    public void bumpRpm(double bumpAmount) {
        targetRpm += bumpAmount;
    }

    public boolean isReady() {
        return (Math.abs(getCurrentRpm() - targetRpm) <= 20) && targetRpm != 0;
    }

    public void update() {
        calculateRpm();
        double controllerOutput = clamp(controller.update(currentRpm, targetRpm), 0, 1);
        upperFlywheel.setPower(controllerOutput);
        lowerFlywheel.setPower(controllerOutput);
    }

    private void calculateRpm() {
        double upperRpm = (upperFlywheel.getVelocity() / pprAtOutputShaft) * 60;
        double lowerRpm = (lowerFlywheel.getVelocity() / pprAtOutputShaft) * 60;

        currentRpm = (upperRpm + lowerRpm)/2;
    }

    public void updateControllerConstants() {
        controller.setPIDF(p, i, d, f);
    }

    public DcMotorEx getMotorObject(int index) {
        switch(index) {
            case 0:
                return upperFlywheel;
            case 1:
                return lowerFlywheel;
        }

        return null;
    }
    public PIDFController getControllerObject() {return controller;}
}
