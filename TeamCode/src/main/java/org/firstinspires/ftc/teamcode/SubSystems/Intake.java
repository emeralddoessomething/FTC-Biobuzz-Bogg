package org.firstinspires.ftc.teamcode.SubSystems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Configurable
public class Intake {
    DcMotor intake;
    State state = State.STOP;
    public static double intakePower = 0.7;
    public static double outtakePower = -0.7;
    public enum State{
        INTAKE,
        OUTTAKE,
        STOP
    }
    public Intake(HardwareMap hardwareMap) {
        String hardwareMapName = "lowerIntake";
        DcMotorEx.Direction direction = DcMotorSimple.Direction.REVERSE;
        DcMotorEx.ZeroPowerBehavior zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT;

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
}
