package org.firstinspires.ftc.teamcode;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class StateMachine<EnumType extends Enum<EnumType>> {
    EnumType state;
    LinkedHashMap<EnumType, State<EnumType>> stateMap = new LinkedHashMap<>();

    public StateMachine<EnumType> addState(State<EnumType> state) {
        stateMap.put(state.enumValue, state);
        return this;
    }

    public StateMachine<EnumType> start(EnumType state) {
        if (stateMap.containsKey(state)) {
            State<EnumType> stateObj = stateMap.get(state);
            assert stateObj != null;
            Consumer<EnumType> onEnter = stateObj.onEnter;
            if (onEnter != null) {
                onEnter.accept(this.state);
            }
            this.state = state; // update state after onEnter so that onEnter can reference the previous state
        } else {
            throw new IllegalArgumentException("State " + state + " not found in state machine. Cannot start state machine.");
        }
        return this;
    }

    public StateMachine<EnumType> update() {
        if (stateMap.containsKey(state)) {
            Consumer<StateMachine<EnumType>> loop = Objects.requireNonNull(stateMap.get(state)).loop;
            if (loop != null) {
                loop.accept(this);
            }
        } else {
            throw new IllegalArgumentException("State " + state + " not found in state machine. Cannot update state machine.");
        }
        return this;
    }

    public StateMachine<EnumType> changeState(EnumType newState) {
        Consumer<EnumType> onExit = Objects.requireNonNull(stateMap.get(state)).onExit;
        if (onExit != null) {
            onExit.accept(newState); // pass in the new state as an argument to onExit so that it can reference the new state
        }
        start(newState);
        return this;
    }

    // QOL to remove nested chaining
    public StateMachine<EnumType> addState(EnumType enumValue) {
        State<EnumType> state = new State<>(enumValue);
        stateMap.put(enumValue, state);
        return this;
    }

    public StateMachine<EnumType> onEnter(Consumer<EnumType> onEnter) {
        State<EnumType> lastAddedState = getLastAddedState();
        lastAddedState = lastAddedState.onEnter(onEnter);
        return this;
    }
    public StateMachine<EnumType> onEnter(Runnable onEnter) {
        State<EnumType> lastAddedState = getLastAddedState();
        lastAddedState = lastAddedState.onEnter(onEnter);
        return this;
    }

    public StateMachine<EnumType> onExit(Consumer<EnumType> onExit) {
        State<EnumType> lastAddedState = getLastAddedState();
        lastAddedState = lastAddedState.onExit(onExit);
        return this;
    }
    public StateMachine<EnumType> onExit(Runnable onExit) {
        State<EnumType> lastAddedState = getLastAddedState();
        lastAddedState = lastAddedState.onExit(onExit);
        return this;
    }

    public StateMachine<EnumType> loop(Consumer<StateMachine<EnumType>> loop) {
        State<EnumType> lastAddedState = getLastAddedState();
        lastAddedState = lastAddedState.loop(loop);
        return this;
    }
    public StateMachine<EnumType> loop(Runnable loop) {
        State<EnumType> lastAddedState = getLastAddedState();
        lastAddedState = lastAddedState.loop(loop);
        return this;
    }

    private State<EnumType> getLastAddedState() {
        Iterator<Map.Entry<EnumType, State<EnumType>>> iterator = stateMap.entrySet().iterator();
        State<EnumType> lastAddedState = null;
        while (iterator.hasNext()) {
            lastAddedState = iterator.next().getValue();
        }
        return lastAddedState;
    }
}

