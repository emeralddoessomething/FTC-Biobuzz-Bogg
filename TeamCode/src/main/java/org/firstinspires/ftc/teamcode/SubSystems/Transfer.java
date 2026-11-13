package org.firstinspires.ftc.teamcode.SubSystems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.hardware.motors.Motor;

@Configurable
public class Transfer {
    DcMotor transferMotor;
    MotorState motorState = MotorState.STOP;
    public static double motorForwardsSpeed = 1;
    public static double motorBackwardsSpeed = -1;
    public static double motorStoppedSpeed = 0;
    CRServo transferServo;
    ServoState servoState = ServoState.STOP;
    public static double servoForwardsSpeed = 1;
    public static double servoBackwardsSpeed = -1;
    public static double servoStoppedSpeed = 0;
    public enum MotorState {
        FORWARD,
        REVERSE,
        STOP
    }

    public enum ServoState {
        FORWARD,
        REVERSE,
        STOP
    }

    public Transfer(HardwareMap hardwareMap) {
        String kickerHardwareMapName = "kicker";
        DcMotorSimple.Direction kickerDirection = DcMotorSimple.Direction.FORWARD;
        transferServo = hardwareMap.get(CRServo.class, kickerHardwareMapName);
        transferServo.setDirection(kickerDirection);

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

    public void setServoState(ServoState servoState) {
        this.servoState = servoState;

        switch(servoState) {
            case FORWARD:
                transferServo.setPower(servoForwardsSpeed);
                break;
            case REVERSE:
                transferServo.setPower(motorBackwardsSpeed);
                break;
            case STOP:
                transferServo.setPower(servoStoppedSpeed);
                break;
        }
    }

    public MotorState getMotorState() {
        return motorState;
    }

    public ServoState getServoState() {
        return servoState;
    }
}
