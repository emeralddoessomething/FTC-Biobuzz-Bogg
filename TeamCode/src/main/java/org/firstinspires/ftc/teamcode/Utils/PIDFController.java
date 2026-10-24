package org.firstinspires.ftc.teamcode.Utils;

import com.qualcomm.robotcore.util.ElapsedTime;

public class PIDFController {
    private double p;
    private double i;
    private double d;
    private double f;

    private double target;
    private double current;

    private double lastError;
    private double integralSum;
    private double maxIntegralSum = Double.POSITIVE_INFINITY;

    private final ElapsedTime timer;
    private double lastTime;
    private boolean isFirstUpdate = true;

    public PIDFController(double p, double i, double d, double f) {
        this.p = p;
        this.i = i;
        this.d = d;
        this.f = f;
        this.timer = new ElapsedTime();
        reset();
    }

    /**
     * @return Calculated controller output
     */
    public double update() {
        double currentTime = timer.seconds();
        double deltaTime = isFirstUpdate ? 0 : (currentTime - lastTime);
        if (isFirstUpdate) {
            isFirstUpdate = false;
        }

        double error = target - current;

        double proportional = p * error;

        // has anti windup
        integralSum += error * deltaTime;
        if (maxIntegralSum != Double.POSITIVE_INFINITY && Math.abs(integralSum) > maxIntegralSum) {
            integralSum = Math.copySign(maxIntegralSum, integralSum);
        }
        double integral = i * integralSum;

        double derivative = 0;
        if (deltaTime > 0) {
            derivative = d * ((error - lastError) / deltaTime);
        }

        double feedforward = f * target;

        lastError = error;
        lastTime = currentTime;

        return proportional + integral + derivative + feedforward;
    }

    /**
     * @param currentValue Current value (NEEDS target to be set!)
     * @return Calculated controller output
     */
    public double update(double currentValue) {
        this.current = currentValue;
        return update();
    }

    /**
     * @param currentValue Current value
     * @param target Target value
     * @return Calculated controller output
     */
    public double update(double currentValue, double target) {
        this.current = currentValue;
        this.target = target;
        return update();
    }

    /**
     * Resets everything (integral, error, timer)
     */
    public void reset() {
        integralSum = 0;
        lastError = 0;
        lastTime = timer.seconds();
        isFirstUpdate = true;
    }

    public double getP() {
        return p;
    }

    public void setP(double p) {
        this.p = p;
    }

    public double getI() {
        return i;
    }

    public void setI(double i) {
        this.i = i;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public double getF() {
        return f;
    }

    public void setF(double f) {
        this.f = f;
    }

    public void setPIDF(double p, double i, double d, double f) {
        this.p = p;
        this.i = i;
        this.d = d;
        this.f = f;
    }

    public double getTarget() {
        return target;
    }

    public void setTarget(double target) {
        this.target = target;
    }

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public double getError() {
        return target - current;
    }

    // https://www.ctrlaltftc.com/the-pid-controller/practical-improvements-to-pid#integral-windup-and-mitigation-methods
    public double getMaxIntegralSum() {
        return maxIntegralSum;
    }

    public void setMaxIntegralSum(double maxIntegralSum) {
        this.maxIntegralSum = Math.abs(maxIntegralSum);
    }
}
