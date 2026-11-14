package org.firstinspires.ftc.teamcode.SubSystems.AutoTeleOpTransfer;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.skeletonarmy.marrow.prompts.OptionPrompt;
import com.skeletonarmy.marrow.prompts.Prompter;
import com.skeletonarmy.marrow.prompts.ValuePrompt;

import org.firstinspires.ftc.teamcode.Auto.SelectivePark;

@TeleOp(name = "Customize Transfer", group = "Transfer")
public class CustomizeTransfer extends OpMode {
    Prompter prompter = new Prompter(this);

    @Override
    public void init() {
        prompter.prompt("alliance", new OptionPrompt<>("Alliance ~ Current: " + TransferClass.alliance.toString(), TransferClass.Alliance.RED, TransferClass.Alliance.RED, null))
                .label("Alliance")
            .showSummary()
            .onComplete(this::save);
    }

    public void save() {
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
