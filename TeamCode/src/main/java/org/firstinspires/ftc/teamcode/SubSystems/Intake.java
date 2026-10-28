package org.firstinspires.ftc.teamcode.SubSystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {
    DcMotor intake;
    DcMotor upperIntake;
    State state = State.STOP;
    double intakePower = 1;
    double outtakePower = -1;
    public enum State{
        INTAKE,
        OUTTAKE,
        STOP
    }
    public Intake(HardwareMap hardwareMap) {
        String hardwareMapName = "intake";
        DcMotorEx.Direction direction = DcMotorSimple.Direction.FORWARD;
        DcMotorEx.ZeroPowerBehavior zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE;

        String otherHardwareMapName = "upperIntake";
        DcMotorEx.Direction otherDirection = DcMotorSimple.Direction.FORWARD;
        DcMotorEx.ZeroPowerBehavior otherZeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE;

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
                upperIntake.setPower(intakePower);
                break;
            case OUTTAKE:
                intake.setPower(outtakePower);
                upperIntake.setPower(intakePower);
                break;
            case STOP:
                intake.setPower(0);
                upperIntake.setPower(intakePower);
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
