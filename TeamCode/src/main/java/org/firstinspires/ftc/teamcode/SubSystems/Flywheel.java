package org.firstinspires.ftc.teamcode.SubSystems;

import static com.pedropathing.utils.Utils.clamp;

//import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Flywheel {
    private final DcMotorEx flywheel;
    private flywheelState state;
    private final ElapsedTime elapsedTime = new ElapsedTime();

    public static double p = 0;
    public static double i = 0;
    public static double d = 0;
    public static double f = 0;


    private double integral = 0;
    private double lastError = 0;
    private double flywheelTargetVelocity = 0;

    /**
     * Constructor for flywheel subsystem. More customization is inside the class.
     * @param hardwareMap hardwareMap
     */
    public Flywheel(HardwareMap hardwareMap) {
        String hardwareMapName = "flywheel";
        DcMotorEx.Direction direction = DcMotorSimple.Direction.REVERSE;
        DcMotorEx.ZeroPowerBehavior zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT;

        flywheel = hardwareMap.get(DcMotorEx.class, hardwareMapName);
        flywheel.setDirection(direction);
        flywheel.setZeroPowerBehavior(zeroPowerBehavior);
    }

    /**
     * Update method for the flywheel. Call every loop. Calculates flywheel output power based on pidf controller
     */
    public void update() {
        double error = flywheelTargetVelocity - flywheel.getVelocity();
        double deltaTime = elapsedTime.seconds();
        elapsedTime.reset();

        if (deltaTime <= 0) deltaTime = 0.0001;

        if(flywheelTargetVelocity == 0) {
            state = flywheelState.STOP;
        } else if(Math.abs(error) > 20) {
            state = flywheelState.SPIN_UP;
        } else {
            state = flywheelState.READY;
        }

        if (state != flywheelState.STOP) {
            integral += error * deltaTime;
            integral = clamp(integral, -0.5, 0.5);

            double derivative = (error - lastError) / deltaTime;
            lastError = error;

            double ffOutput = f * flywheelTargetVelocity;
            double output = (p * error) + (i * integral) + (d * derivative) + ffOutput;

            flywheel.setPower(clamp(output, -1, 1));
        } else {
            integral = 0;
            lastError = 0;
            flywheel.setPower(0);
        }
    }

    /**
     * Set flywheel target velocity
     * @param velocity Velocity
     */
    public void setFlywheelTargetVelocity(double velocity) {
        flywheelTargetVelocity = velocity;
    }

    public double getFlywheelTargetVelocity() {
        return flywheelTargetVelocity;
    }

    /**
     * Bump flywheel target velocity by a certain amount
     * @param bumpAmount amount to bump (can be negative)
     */
    public void bumpFlywheelTargetVelocity(double bumpAmount) {
        flywheelTargetVelocity += bumpAmount;
    }

    /**
     * Resets deltatime so the integral isn't super messed up. Call in start()
     */
    public void resetTimer() {
        elapsedTime.reset();
    }

    /**
     * Returns flywheel DcMotorEx object
     * @return DcMotorEx object
     */
    public DcMotorEx getObject() {
        return flywheel;
    }

    /**
     * Returns the state of the flywheel subsystem
     * @return Enum flywheelState
     */
    public Flywheel.flywheelState getState() {
        return this.state;
    }

    /**
     * Set flywheel state
     */
    public void setState(flywheelState state) {
        this.state = state;
    }

    public enum flywheelState {
        STOP,
        SPIN_UP,
        READY
    }
}
