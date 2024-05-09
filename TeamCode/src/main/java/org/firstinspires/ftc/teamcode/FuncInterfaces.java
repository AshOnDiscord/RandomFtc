package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

// Don't use, this was purely for testing lol
class StateLambda<LambdaType> {
    LambdaType lambda;
    public StateLambda(LambdaType lambda) {
        boolean isFunction = lambda instanceof Function;
        boolean isSupplier = lambda instanceof Supplier;
        boolean isConsumer = lambda instanceof Consumer;
        boolean isBiFunction = lambda instanceof BiFunction;
        if (!isFunction && !isSupplier && !isConsumer && !isBiFunction) {
            throw new IllegalArgumentException("Lambda must be a Function, Supplier, Predicate, or BiFunction");
        }
        this.lambda = lambda;
    }

    public Double exec(State prev, HardwareMap hwMap, Gamepad gamepad) {
        try {
            if (lambda instanceof Function) {
                return ((Function<State, Double>) lambda).apply(prev);
            } else if (lambda instanceof Supplier) {
                return ((Supplier<Double>) lambda).get();
            } else if (lambda instanceof Consumer) {
                ((Consumer<State>) lambda).accept(prev);
            } else if (lambda instanceof BiFunction) {
                return ((BiFunction<State, Gamepad, Double>) lambda).apply(prev, gamepad);
            }
            return 0.0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing lambda, make sure lambda takes the correct arguments and returns a Double or nothing");
        }
    }
}

public class FuncInterfaces {
    public static void main(String[] args) {
        StateLambda<Supplier<Integer>> lambda = new StateLambda<>(() -> 0);
    }
}