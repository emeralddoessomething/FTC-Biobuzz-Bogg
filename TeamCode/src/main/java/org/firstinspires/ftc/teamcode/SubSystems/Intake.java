package org.firstinspires.ftc.teamcode.SubSystems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Configurable
public class Intake {
    DcMotor intake;
    DcMotor upperIntake;
    State state = State.STOP;
    public static double intakePower = 0.7;
    public static double outtakePower = -0.7;
    public static double upperIntakePower = 0.6;
    public static double upperOuttakePower = -0.6;
    public enum State{
        INTAKE,
        OUTTAKE,
        STOP
    }
    public Intake(HardwareMap hardwareMap) {
        String hardwareMapName = "lowerIntake";
        DcMotorEx.Direction direction = DcMotorSimple.Direction.REVERSE;
        DcMotorEx.ZeroPowerBehavior zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT;

        String otherHardwareMapName = "upperIntake";
        DcMotorEx.Direction otherDirection = DcMotorSimple.Direction.FORWARD;
        DcMotorEx.ZeroPowerBehavior otherZeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT;

        intake = hardwareMap.get(DcMotorEx.class, hardwareMapName);
        intake.setDirection(direction);
        intake.setZeroPowerBehavior(zeroPowerBehavior);

        upperIntake = hardwareMap.get(DcMotorEx.class, otherHardwareMapName);
        upperIntake.setDirection(otherDirection);
        upperIntake.setZeroPowerBehavior(otherZeroPowerBehavior);
    }

    public void setState(State state) {
        switch(state) {
            case INTAKE:
                intake.setPower(intakePower);
                upperIntake.setPower(upperIntakePower);
                break;
            case OUTTAKE:
                intake.setPower(outtakePower);
                upperIntake.setPower(upperOuttakePower);
                break;
            case STOP:
                intake.setPower(0);
                upperIntake.setPower(0);
                break;
        }

        this.state = state;
    }

    public State getState() {
        return state;
    }

    public DcMotor getMotorObject(int index) {
        switch(index) {
            case 1:
                return intake;
            case 2:
                return upperIntake;
            default:
                return intake;
        }
    }
}
