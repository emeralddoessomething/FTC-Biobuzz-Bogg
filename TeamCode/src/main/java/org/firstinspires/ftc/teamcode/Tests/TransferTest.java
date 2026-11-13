package org.firstinspires.ftc.teamcode.Tests;

import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.SubSystems.Transfer;
import org.firstinspires.ftc.teamcode.Utils.Toggle;

@Disabled
@TeleOp(name = "Transfer Test", group = "Tests")
public class TransferTest extends OpMode {
    Transfer transfer;
    JoinedTelemetry joinedTelemetry;

    @Override
    public void init() {
        transfer = new Transfer(hardwareMap);
        joinedTelemetry = new JoinedTelemetry(PanelsTelemetry.INSTANCE.getFtcTelemetry(), telemetry);
    }

    @Override
    public void loop() {
        joinedTelemetry.addLine("(Motor) Up/Down/Right - Forwards/Reverse/Stop");

        if(gamepad1.dpad_up) {
            transfer.setMotorState(Transfer.MotorState.FORWARD);
        } else if(gamepad1.dpad_down) {
            transfer.setMotorState(Transfer.MotorState.REVERSE);
        } else if(gamepad1.right_bumper) {
            transfer.setMotorState(Transfer.MotorState.STOP);
        }

        joinedTelemetry.addLine("Transfer Motor State: " + transfer.getMotorState().toString());

        joinedTelemetry.addLine();

        joinedTelemetry.addLine("(Servo) Y/A/B - Forwards/Reverse/Stop");

        if(gamepad1.y) {
            transfer.setServoState(Transfer.ServoState.FORWARD);
        } else if(gamepad1.b) {
            transfer.setServoState(Transfer.ServoState.REVERSE);
        } else if(gamepad1.a) {
            transfer.setServoState(Transfer.ServoState.STOP);
        }

        joinedTelemetry.addLine("Transfer Servo State: " + transfer.getServoState().toString());

        joinedTelemetry.update();
    }
}
