package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.concurrent.atomic.AtomicReference;

@TeleOp(name = "Teleop", group = "Linear Opmode")
public class Teleop extends LinearOpMode {

    @Override
    public void runOpMode() {
        Robot robot = new Robot(hardwareMap, gamepad1);

        ElapsedTime tillBottom = new ElapsedTime();
        AtomicReference<Double> timeToBottom = new AtomicReference<>((double) 0); // fuck java lambdas
        PID liftPID = new PID(1, 0, 0.1);

        StateMachine<LiftStates> liftMachine = new StateMachine<LiftStates>().addState(LiftStates.OFF).onEnter(() -> {
                    robot.lift.brake(false);
                    robot.lift.setPower(0);
                    // rotate claw down
                }).addState(LiftStates.MANUAL).loop(() -> {
                    double power = gamepad1.right_trigger - gamepad1.left_trigger;
                    robot.lift.setPower(power);
                    if (robot.lift.getHeight() > robot.lift.setLines[0] - 100) {
                        // rotate claw up
                    } else {
                        // rotate claw down
                    }
                }).addState(LiftStates.FLOOR).onEnter(() -> {
                    tillBottom.reset();
                    timeToBottom.set(robot.lift.getHeight() * 300);
                    robot.lift.setPower(0.5);
                }).loop((stateMachine) -> {
                    if (tillBottom.milliseconds() > timeToBottom.get()) {
                        stateMachine.changeState(LiftStates.OFF);
                    }
                })
                // Maybe find a less repetitive way to do this besides just a mini function
                .addState(LiftStates.FIRST).onEnter(() -> {
                    // rotate claw up
                }).loop(() -> {
                    double power = liftPID.calc(robot.lift.setLines[0], robot.lift.getHeight());
                    robot.lift.setPower(power);
                }).addState(LiftStates.SECOND).onEnter(() -> {
                    // rotate claw up
                }).loop(() -> {
                    double power = liftPID.calc(robot.lift.setLines[1], robot.lift.getHeight());
                    robot.lift.setPower(power);
                }).addState(LiftStates.THIRD).onEnter(() -> {
                    // rotate claw up
                }).loop(() -> {
                    double power = liftPID.calc(robot.lift.setLines[2], robot.lift.getHeight());
                    robot.lift.setPower(power);
                });

        liftMachine.start(LiftStates.OFF);

        waitForStart();

        while (opModeIsActive() && !isStopRequested()) {
            if (liftMachine.state != LiftStates.MANUAL) {
                // if bumpers are pressed, change state to manual
                // you could do this with the other state callbacks but this is less repetitive
                liftMachine.changeState(LiftStates.MANUAL);
            }
            // I need to find a proper like way to do global transitions ngl
            if (gamepad1.a) {
                liftMachine.changeState(LiftStates.FLOOR);
            } else if (gamepad1.b) {
                liftMachine.changeState(LiftStates.FIRST);
            } else if (gamepad1.x) {
                liftMachine.changeState(LiftStates.SECOND);
            } else if (gamepad1.y) {
                liftMachine.changeState(LiftStates.THIRD);
            }
            liftMachine.update();
        }
    }
}

enum LiftStates {
    OFF, MANUAL, FLOOR, FIRST, SECOND, THIRD,
}

