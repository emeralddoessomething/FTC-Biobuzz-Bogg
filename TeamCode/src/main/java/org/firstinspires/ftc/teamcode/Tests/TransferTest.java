package org.firstinspires.ftc.teamcode.Tests;

import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.SubSystems.Transfer;
import org.firstinspires.ftc.teamcode.Utils.Toggle;

@TeleOp(name = "Transfer Test", group = "Tests")
public class TransferTest extends OpMode {
    Transfer transfer;
    JoinedTelemetry joinedTelemetry;
    Toggle transferToggle;

    @Override
    public void init() {
        transfer = new Transfer(hardwareMap);
        joinedTelemetry = new JoinedTelemetry(PanelsTelemetry.INSTANCE.getFtcTelemetry(), telemetry);
    }

    @Override
    public void loop() {
        transferToggle.update(gamepad1.a);

        if(transferToggle.isEnabled()) {
            transfer.setState(Transfer.State.UP);
        } else {
            transfer.setState(Transfer.State.DOWN);
        }

        joinedTelemetry.addLine("Transfer State: " + transfer.getState().toString());
    }
}
