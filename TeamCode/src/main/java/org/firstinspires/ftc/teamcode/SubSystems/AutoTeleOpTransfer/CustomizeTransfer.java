package org.firstinspires.ftc.teamcode.SubSystems.AutoTeleOpTransfer;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.skeletonarmy.marrow.prompts.OptionPrompt;
import com.skeletonarmy.marrow.prompts.Prompter;
import com.skeletonarmy.marrow.prompts.ValuePrompt;

import org.firstinspires.ftc.teamcode.Auto.SelectivePark;

import java.util.Objects;

@TeleOp(name = "Customize Transfer", group = "Transfer")
public class CustomizeTransfer extends OpMode {
    Prompter prompter;

    @Override
    public void init() {
        prompter = new Prompter(this);

        prompter.prompt("alliance", new OptionPrompt<>("Alliance ~ Current: " + Objects.toString(TransferClass.alliance, "None"), TransferClass.Alliance.RED, TransferClass.Alliance.BLUE, TransferClass.Alliance.UNKNOWN))
            .label("Alliance")
            .showSummary()
            .onComplete(this::save);
    }

    public void save() {
        if(prompter.get("alliance") == TransferClass.Alliance.UNKNOWN) {
            TransferClass.alliance = null;
            return;
        }

        TransferClass.alliance = prompter.get("alliance");
    }

    @Override
    public void init_loop() {
        prompter.run();
    }

    @Override
    public void loop() {

    }
}
