package org.firstinspires.ftc.teamcode.SubSystems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Configurable
public class Transfer {
    DcMotor transferMotor;
    MotorState motorState = MotorState.STOP;
    public static double motorForwardsSpeed = 1;
    public static double motorBackwardsSpeed = -1;
    public static double motorStoppedSpeed = 0;

    public enum MotorState {
        FORWARD,
        REVERSE,
        STOP
    }

    public Transfer(HardwareMap hardwareMap) {
        String motorHardwareMapName = "transfer";
        DcMotorSimple.Direction motorDirection = DcMotorSimple.Direction.FORWARD;
        DcMotorEx.ZeroPowerBehavior motorZeroPowerBehaviour = DcMotor.ZeroPowerBehavior.BRAKE;
        transferMotor = hardwareMap.get(DcMotor.class, motorHardwareMapName);
        transferMotor.setDirection(motorDirection);
        transferMotor.setZeroPowerBehavior(motorZeroPowerBehaviour);
    }

    public void setMotorState(MotorState motorState) {
        this.motorState = motorState;

        switch(motorState) {
            case FORWARD:
                transferMotor.setPower(motorForwardsSpeed);
                break;
            case REVERSE:
                transferMotor.setPower(motorBackwardsSpeed);
                break;
            case STOP:
                transferMotor.setPower(motorStoppedSpeed);
                break;
        }
    }

    public MotorState getMotorState() {
        return motorState;
    }
}
