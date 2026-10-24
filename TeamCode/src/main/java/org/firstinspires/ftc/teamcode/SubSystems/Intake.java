package org.firstinspires.ftc.teamcode.SubSystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {
    DcMotor intake;
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

        intake = hardwareMap.get(DcMotorEx.class, hardwareMapName);
        intake.setDirection(direction);
        intake.setZeroPowerBehavior(zeroPowerBehavior);
    }

    public void setState(State state) {
        switch(state) {
            case INTAKE:
                intake.setPower(intakePower);
                break;
            case OUTTAKE:
                intake.setPower(outtakePower);
                break;
            case STOP:
                intake.setPower(0);
                break;
        }

        this.state = state;
    }

    public State getState() {
        return state;
    }

    public DcMotor getMotorObject() {
        return intake;
    }
}
