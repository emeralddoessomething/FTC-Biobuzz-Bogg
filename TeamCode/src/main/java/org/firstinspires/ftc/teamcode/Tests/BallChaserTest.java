package org.firstinspires.ftc.teamcode.Tests;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.skeletonarmy.marrow.BulkCaching;
import com.skeletonarmy.marrow.weaver.PedroPathingConverter;

import org.firstinspires.ftc.teamcode.Pedro.Constants;
import org.firstinspires.ftc.teamcode.Utils.BallChaserUtil;
import org.firstinspires.ftc.teamcode.Utils.PanelsFieldDrawing;

@Autonomous(name = "Ball Chaser Test", group = "Tests")
@BulkCaching
public class BallChaserTest extends OpMode {
    Follower follower;
    BallChaserUtil ballChaser;
    int step = 0;

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
        ballChaser = new BallChaserUtil(hardwareMap, follower);
    }

    @Override
    public void loop() {
        switch(step) {
            case -1:
                telemetry.addLine("Finished");
                break;
            case 0:
                ballChaser.update();
                telemetry.addLine("Update");
                step++;
                break;
            case 1:
                telemetry.addLine("Following Path");
                follower.follow(PedroPathingConverter.toPath(ballChaser.weaverUpdate()));
                step++;
                break;
            case 2:
                if(!follower.isBusy()) {
                    step = -1;
                }
                break;
        }

        PanelsFieldDrawing.drawRobot(follower.pose());

        follower.update();
    }
}
