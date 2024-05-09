package org.firstinspires.ftc.teamcode;

import java.util.function.Consumer;

public class State<EnumType extends Enum<EnumType>> {
    public EnumType enumValue;
    Consumer<EnumType> onEnter;
    Consumer<EnumType> onExit;
    Consumer<StateMachine<EnumType>> loop;

    public State(EnumType enumValue) {
        this.enumValue = enumValue;
    }

    public State<EnumType> onEnter(Consumer<EnumType> onEnter) {
        this.onEnter = onEnter;
        return this;
    }
    public State<EnumType> onEnter(Runnable onEnter) {
        this.onEnter = (e) -> onEnter.run();
        return this;
    }
    public State<EnumType> onExit(Consumer<EnumType> onExit) {
        this.onExit = onExit;
        return this;
    }
    public State<EnumType> onExit(Runnable onExit) {
        this.onExit = (e) -> onExit.run();
        return this;
    }
    public State<EnumType> loop(Consumer<StateMachine<EnumType>> loop) {
        this.loop = loop;
        return this;
    }
    public State<EnumType> loop(Runnable loop) {
        this.loop = (s) -> loop.run();
        return this;
    }
}
