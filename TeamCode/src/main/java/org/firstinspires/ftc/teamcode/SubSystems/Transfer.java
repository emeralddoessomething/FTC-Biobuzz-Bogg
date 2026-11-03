package org.firstinspires.ftc.teamcode.SubSystems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@Configurable
public class Transfer {
    Servo transferServo;
    State state = State.DOWN;
    public static double upPosition = 0.8;
    public static double downPosition = 0.5;
    public enum State{
        UP,
        DOWN
    }
    public Transfer(HardwareMap hardwareMap) {
        String hardwareMapName = "transfer";
        Servo.Direction direction = Servo.Direction.FORWARD;
        double servoUpperLimit = 1;
        double servoLowerLimit = 0;

        transferServo = hardwareMap.get(Servo.class, hardwareMapName);
        transferServo.setDirection(direction);
        transferServo.scaleRange(servoLowerLimit, servoUpperLimit);
        setState(State.DOWN);
    }

    public void setState(State state) {
        switch(state) {
            case UP:
                transferServo.setPosition(upPosition);
                break;
            case DOWN:
                transferServo.setPosition(downPosition);
                break;
        }

        this.state = state;
    }

    public State getState() {
        return state;
    }

    public Servo getServoObject() {
        return transferServo;
    }
}
