package org.firstinspires.ftc.teamcode.Auto;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.Pedro.Constants;

@Autonomous // wow new annotation
public class ExampleAutoMovement extends OpMode {
    // I highly recommend you read the pedro pathing docs, https://pedropathing.com

    // The follower is basically pedro's idea of the robot. It creates itself from the Constants class, which is filled out by information from tuning
    private Follower follower;

    // The poseFactory is pedro's way of easily making poses. Poses are cordinates and heading, so X Y and heading.
    // Refer to https://pedropathing.com/docs/pathing/guide/pose-creation
    private PoseFactory poseFactory = PoseFactory.degrees(); // In this case, the pose factory will use degrees. Pedro usually uses radians.
    // Lets create three poses for the robot
    private Pose startingPose = poseFactory.of(0, 0, 0);
    private Pose poseOne = poseFactory.of(10, 10, 90); // In pedro, a positive number for heading makes the robot turn left.
    private Pose poseTwo = poseFactory.of(0, 20, 0); // The values for X and Y are in inches (so y = 20 = 20 inches)

    // Now, we can create the lines for the robot (follower) to follow (wow, I wonder why its called the follower)
    // There are two main types of lines in pedro pathing, a straight line and a curved line
    // for a straight line, you can define two poses and create a line between them
    // for a curved line, you have a start and end pose, and then control points. Control points affect the line by pulling the line closer to it. You can check this out in the pedro pathing visualizer, https://visualizer.pedropathing.com.

    // Lets make three paths for the follower
    // One will be a straight line from the starting pose to pose one
    // Another will be another line from poseOne to pose two
    // The last one will be a curve from poseTwo to the origional starting position, with poseOne as the control point
    // You can refer to https://pedropathing.com/docs/pathing/guide/path-creation

    private Path startToPoseOne = line(startingPose, poseOne).linear(startingPose, poseOne);
    // In this case, the first bit, `line(startingPose, poseOne)` creates a line between the two poses. The second bit, `.linear(startingPose, poseOne)` tells the line to change heading between the two linearly

    private Path poseOneToPoseTwo = line(poseOne, poseTwo).linear(poseOne, poseTwo);

    // Now, lets create the curve from poseTwo to the starting position
    private Path poseTwoToStart = curve(poseTwo, poseOne, startingPose).linear(poseTwo, startingPose);
    // In this case, poseTwo and startingPose represent the starting and ending poses of this curve
    // ANY poses in between are counted as control points. So I could do `curve(poseTwo, poseOne, poseThree, poseFour, poseFive, startingPose)`
    // the ending bit still represents changing heading linearly between the points.

    @Override
    public void init() {
        follower = Constants.create(hardwareMap); // Create the follower from the Constants file. The constants file is full of the tuning values from pedro. Refer to https://pedropathing.com/docs/pathing/tuning/constants

        follower.setPose(startingPose); // by default, the starting pose is (0, 0, 0)
    }

    int stateMachineStep = 0; // refer to this later

    @Override
    public void loop() {
        follower.update(); // This is very important! You must update the follower, or pedro's interpretation of the robot, every loop!

        // Now, lets follow the paths we just made
        // One will be a straight line from the starting pose to pose one
        // Another will be another line from poseOne to pose two
        // The last one will be a curve from poseTwo to the origional starting position, with poseOne as the control point

        // If you're looking at the docs, this is where it changes from them
        // I am not going to be using Ivy. This is literally because I don't want to learn it right now, maybe later...
        // Instead, I will be using a finite state machine.
        // The pedro docs for that are on pedro pathing v2, which is different from pedro pathing v3. However, you can still look at them: https://web.archive.org/web/20251206175411/https://pedropathing.com/docs/pathing/examples/auto

        // Because this is loop, we will be running all the code in here many times per second. Thus, we want to make sure we don't run though all the code too fast, cause stuff is still happening in the real world
        // For example, we don't want to do this action if we're not at the right place to do it. We have to wait for the robot to finish moving and get to the correct pose

        // The finite state machine fixes this for us. It splits up our auto into states, and makes sure everything runs in order while waiting for everything before it to finish

        // Remember the `stateMachineStep` integer from earlier? We will be using it to track the step the state machine is in (eg. Step 1, step 2, step 3, etc)
        // Below is a switch-case statement. This is basically just a bunch of if-else statements stacked on top of eachother

        switch(stateMachineStep) {
            case -1: // if `stateMachineStep` == -1...
                telemetry.addLine("Finished Auto");
                break; // switch-case statements use `break` to separate cases.
            case 0: // else, if `stateMachineStep` == 0...
                follower.follow(startToPoseOne); // Follow this path...
                stateMachineStep++; // and increment the step counter up by one
                break; // end
            case 1: // else, if `stateMachineStep` == 1...
                // follower.isBusy() tells you if the follower is busy. 'Busy' means that its actively following a path. At the very end of the path, it tries to correct fully to be exactly at the designated X Y and heading, so it may still be 'busy' at the end of the path
                if(!follower.isBusy()) { // So, once the follow is NOT busy (we just told it to become busy in the last step. Remember that this switch-case statement is running many times per second)
                    stateMachineStep++;
                }
                break; // end
            case 2:
                follower.follow(poseOneToPoseTwo);
                stateMachineStep++;
                break;
            case 3:
                if(!follower.isBusy()) {
                    stateMachineStep++;
                }
                break;
            case 4:
                follower.follow(poseTwoToStart);
                stateMachineStep++;
                break;
            case 5:
                if(!follower.isBusy()) {
                    stateMachineStep = -1; // This will cause the state machine to jump back to the -1 case.
                }
                break;
        }

        telemetry.addLine("Step: " + stateMachineStep); // Send which step of the state machine we're on to the driver station telemetry
    }
}
