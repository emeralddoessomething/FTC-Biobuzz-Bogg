package org.firstinspires.ftc.teamcode.Tests;

import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.SubSystems.Intake;

@TeleOp(name = "Intake Test", group = "Tests")
public class IntakeTest extends OpMode {
    Intake intake;
    JoinedTelemetry joinedTelemetry;

    @Override
    public void init() {
        intake = new Intake(hardwareMap);
        joinedTelemetry = new JoinedTelemetry(PanelsTelemetry.INSTANCE.getFtcTelemetry(), telemetry);
    }

    @Override
    public void loop() {
        if(gamepad1.a) {
            intake.setState(Intake.State.INTAKE);
        }

        if(gamepad1.b) {
            intake.setState(Intake.State.OUTTAKE);
        }

        if(gamepad1.x) {
            intake.setState(Intake.State.STOP);
        }

        joinedTelemetry.addLine("Intake state: " + intake.getState().toString());
    }
}
