package org.firstinspires.ftc.teamcode.SubSystems.AutoTeleOpTransfer;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Reset Transfer Vars", group = "Utils")
public class ResetTransfer extends OpMode {
    @Override
    public void init() {
        TransferClass.alliance = null;
        TransferClass.robotPose = null;

        telemetry.addLine("Reset Transfer Class Variables");
    }

    @Override
    public void loop() {

    }
}
