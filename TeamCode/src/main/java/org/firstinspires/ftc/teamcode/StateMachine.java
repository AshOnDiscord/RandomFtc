package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class StateMachine<EnumType extends Enum<EnumType>> {
    EnumType state;
    Map<EnumType, State> stateMap = new LinkedHashMap<>();

    public StateMachine<EnumType> addState(State<EnumType> state) {
        stateMap.put(state.enumValue, state);
        return this;
    }}

class State<EnumType extends Enum<EnumType>> {
    public EnumType enumValue;
    // EnumType is the enum the machine is coming from or to, double is time till switch, 0 is instant, -1 is completely block.
//    public TriFunction<EnumType, Robot, Gamepad, Double> onEnter;
//    public TriFunction<EnumType, Robot, Gamepad, Double> onExit;
//    public TriFunction<StateMachine<EnumType>, Robot, Gamepad, EnumType> loop;

    StateCallBack<EnumType> onEnter;
    StateCallBack<EnumType> onExit;
    StateCallBack<EnumType> loop;

    public static class StateCallBack<StateType extends Enum<StateType>> {
        public TriFunction<StateType, Robot, Gamepad, Double> callback;

        // Robot -> Gamepad -> EnumType (Order of param truncation)
        public StateCallBack(TriFunction<StateType, Robot, Gamepad, Double> callback) {
            this.callback = callback;
        }
        public StateCallBack(TriConsumer<StateType, Robot, Gamepad> callback) {
            this.callback = (state, robot, gamepad) -> { callback.accept(state, robot, gamepad); return 0.0; };
        }
        public StateCallBack(BiFunction<Robot, Gamepad, Double> callback) {
            this.callback = (state, robot, gamepad) -> callback.apply(robot, gamepad);
        }
        public StateCallBack(BiConsumer<Robot, Gamepad> callback) {
            this.callback = (state, robot, gamepad) -> { callback.accept(robot, gamepad); return 0.0; };
        }
        public StateCallBack(Function<Robot, Double> callback) {
            this.callback = (state, robot, gamepad) -> callback.apply(robot);
        }
        public StateCallBack(Consumer<Robot> callback) {
            this.callback = (state, robot, gamepad) -> { callback.accept(robot); return 0.0; };
        }
        public StateCallBack(Supplier<Double> callback) {
            this.callback = (state, robot, gamepad) -> callback.get();
        }
        public StateCallBack(Runnable callback) {
            this.callback = (state, robot, gamepad) -> { callback.run(); return 0.0; };
        }

        // Gamepad -> Robot
        public StateCallBack<StateType> gamepad(BiFunction <Gamepad, Robot, Double> callback) {
            TriFunction<StateType, Robot, Gamepad, Double> triCallback = (state, robot, gamepad) -> callback.apply(gamepad, robot);
            return new StateCallBack<>(triCallback);
        }
        public StateCallBack<StateType> gamepad(BiConsumer<Gamepad, Robot> callback) {
            TriFunction<StateType, Robot, Gamepad, Double> triCallback = (state, robot, gamepad) -> { callback.accept(gamepad, robot); return 0.0; };
            return new StateCallBack<>(triCallback);
        }
        public StateCallBack<StateType> gamepad(Function<Gamepad, Double> callback) {
            TriFunction<StateType, Robot, Gamepad, Double> triCallback = (state, robot, gamepad) -> callback.apply(gamepad);
            return new StateCallBack<>(triCallback);
        }
        public StateCallBack<StateType> gamepad(Consumer<Gamepad> callback) {
            TriFunction<StateType, Robot, Gamepad, Double> triCallback = (state, robot, gamepad) -> { callback.accept(gamepad); return 0.0; };
            return new StateCallBack<>(triCallback);
        }
        // StateType -> Robot
        public StateCallBack<StateType> stateRobot(BiFunction<StateType, Robot, Double> callback) {
            TriFunction<StateType, Robot, Gamepad, Double> triCallback = (state, robot, gamepad) -> callback.apply(state, robot);
            return new StateCallBack<>(triCallback);
        }
        public StateCallBack<StateType> stateRobot(BiConsumer<StateType, Robot> callback) {
            TriFunction<StateType, Robot, Gamepad, Double> triCallback = (state, robot, gamepad) -> { callback.accept(state, robot); return 0.0; };
            return new StateCallBack<>(triCallback);
        }
        // StateType -> Gamepad
        public StateCallBack<StateType> stateGamepad(BiFunction<StateType, Gamepad, Double> callback) {
            TriFunction<StateType, Robot, Gamepad, Double> triCallback = (state, robot, gamepad) -> callback.apply(state, gamepad);
            return new StateCallBack<>(triCallback);
        }
        public StateCallBack<StateType> stateGamepad(BiConsumer<StateType, Gamepad> callback) {
            TriFunction<StateType, Robot, Gamepad, Double> triCallback = (state, robot, gamepad) -> { callback.accept(state, gamepad); return 0.0; };
            return new StateCallBack<>(triCallback);
        }
        // StateType
        public StateCallBack<StateType> state(Function<StateType, Double> callback) {
            TriFunction<StateType, Robot, Gamepad, Double> triCallback = (state, robot, gamepad) -> callback.apply(state);
            return new StateCallBack<>(triCallback);
        }
        public StateCallBack<StateType> state(Consumer<StateType> callback) {
            TriFunction<StateType, Robot, Gamepad, Double> triCallback = (state, robot, gamepad) -> { callback.accept(state); return 0.0; };
            return new StateCallBack<>(triCallback);
        }
    }

    public State(EnumType enumValue) {
        this.enumValue = enumValue;
    }


//    public State<EnumType> onEnter(TriFunction<EnumType, Robot, Gamepad, Double> onEnter) {
//        this.onEnter = onEnter;
//        return this;
//    }
//
//    public State<EnumType> onExit(TriFunction<EnumType, Robot, Gamepad, Double> onExit) {
//        this.onExit = onExit;
//        return this;
//    }
//
//    public State<EnumType> loop(TriFunction<StateMachine<EnumType>, Robot, Gamepad, EnumType> loop) {
//        this.loop = loop;
//        return this;
//    }
}

enum LiftStates {
    OFF,
    FLOOR,
    FIRST,
    SECOND,
    THIRD,
    MANUAL
}

@FunctionalInterface
interface TriFunction<A, B, C, R> {

    R apply(A a, B b, C c);

    default <V> TriFunction<A, B, C, V> andThen(
            Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (A a, B b, C c) -> after.apply(apply(a, b, c));
    }
}

@FunctionalInterface
interface TriConsumer<A, B, C> {

    void accept(A a, B b, C c);

    default TriConsumer<A, B, C> andThen(TriConsumer<? super A, ? super B, ? super C> after) {
        Objects.requireNonNull(after);
        return (A a, B b, C c) -> {
            accept(a, b, c);
            after.accept(a, b, c);
        };
    }
}

class Robot {
    HardwareMap hwMap;
    Gamepad gamepad1;
    DriveTrain driveTrain;
    Lift lift;

    public Robot(HardwareMap hwMap, Gamepad gamepad1) {
        this.hwMap = hwMap;
        this.gamepad1 = gamepad1;
        this.driveTrain = new DriveTrain(hwMap);
        this.lift = new Lift(hwMap);
    }

    public static class DriveTrain {
        public DcMotorEx frontLeft;
        public DcMotorEx frontRight;
        public DcMotorEx backRight;
        public DcMotorEx backLeft;

        public DriveTrain(HardwareMap hwMap) {
            frontLeft = hwMap.get(DcMotorEx.class, "frontLeft");
            frontRight = hwMap.get(DcMotorEx.class, "frontRight");
            backRight = hwMap.get(DcMotorEx.class, "backRight");
            backLeft = hwMap.get(DcMotorEx.class, "backLeft");
        }

        public void setPower(double fl, double fr, double br, double bl) {
            frontLeft.setPower(fl);
            frontRight.setPower(fr);
            backRight.setPower(br);
            backLeft.setPower(bl);
        }
    }

    public static class Lift {
        public DcMotorEx left;
        public DcMotorEx right;
        public double gravity = 0.1;
        public double[] setLines = {300, 500, 700};

        public Lift(HardwareMap hwMap) {
            left = hwMap.get(DcMotorEx.class, "leftLift");
            right = hwMap.get(DcMotorEx.class, "rightLift");
        }

        public void setPower(double power) {
            left.setPower(power);
            right.setPower(power);
        }

        public void hold() {
            this.setPower(this.gravity);
        }

        public void setMode(DcMotor.RunMode mode) {
            left.setMode(mode);
            right.setMode(mode);
        }

        public void setZero(DcMotor.ZeroPowerBehavior mode) {
            left.setZeroPowerBehavior(mode);
            right.setZeroPowerBehavior(mode);
        }

        public void brake(boolean brake) {
            this.setZero(brake ? DcMotor.ZeroPowerBehavior.BRAKE : DcMotor.ZeroPowerBehavior.FLOAT);
        }
    }
}