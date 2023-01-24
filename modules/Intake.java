package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.util.ButtonHelper;
import org.firstinspires.ftc.teamcode.util.EventHandler;
import org.firstinspires.ftc.teamcode.util.Function;
import org.firstinspires.ftc.teamcode.util.MacroState;
import org.firstinspires.ftc.teamcode.util.MicroState;

public class Intake implements Modulable, Tickable {
    private static final int[] LEVELS = new int[]{0, 1824, 2878, 4090};
    public static final int GROUND = 0;
    public static final int LOW = 1;
    public static final int MID = 2;
    public static final int HIGH = 3;

    private Claw claw;
    public Pivot pivot;
    public Elevator elevator;

    public static MacroState pickingUpCone = new MacroState();


    private State currentState = State.IDLE;
    /**
     * Whether the cone should be placed with the pivot at the intake position.
     */
    private boolean placeInFront;
    public int level;
    private long time = 0;

    public MacroState PLACE_CONE;
    public MacroState MANUAL_CONTROL;

    public State getCurrentState() {
        return currentState;
    }

    public void manualOverride() {
        currentState = State.IDLE;
    }

    public void initPlaceConeState()
    {
        PLACE_CONE = new MacroState();
        ButtonHelper intakeButtons = new ButtonHelper(EventHandler.instance.controls[1]);
        Function INITIALIZE_MOVE_TO_PLACE = (obj) -> {
            MacroState macroState = (MacroState) obj;
            macroState.setCurrentStatus(new String[]{});
            claw.setClawOpen(false);
            elevator.startMoveToPos(LEVELS[level]);
            macroState.nextState();
        };

        Function ELEVATOR_MOVING_TO_PLACE_ORIENTATION = (obj) -> {
            MacroState macroState = (MacroState) obj;
            String stateInfo = "ELEVATOR_MOVING_TO_PLACE_ORIENTATION";
            String currentInfo = "Current: " + String.valueOf(elevator.getCurrent());
            String encoderInfo = "Enc Pos: " + String.valueOf(elevator.getEncPos()) + "| Enc Target: " + String.valueOf(elevator.getCurrentTarget());
            macroState.setCurrentStatus(new String[]{stateInfo, currentInfo, encoderInfo});
            if (elevator.isAtTargetPos()) {
                macroState.nextState();
            }
        };

        Function PIVOT_MOVING_TO_PLACE_ORIENTATION = (obj) -> {
            MacroState macroState = (MacroState) obj;
            String stateInfo = "PIVOT_MOVING_TO_PLACE_ORIENTATION";
            macroState.setCurrentStatus(new String[]{stateInfo});
            if (pivot.isAtTargetPos()) {
                macroState.nextState();
            }
        };
        Function WAITING_FOR_PLACE_INPUT = (obj) -> {
            MacroState macroState = (MacroState) obj;
            String stateInfo = "WAITING_FOR_PLACE_INPUT";
            macroState.setCurrentStatus(new String[]{stateInfo});
            if (intakeButtons.pressing(ButtonHelper.dpad_up)) {
                macroState.nextState();
            }
        };

        Function OPENING_CLAW = (obj) -> {
            MacroState macroState = (MacroState) obj;
            String stateInfo = "OPENING_CLAW";
            macroState.setCurrentStatus(new String[]{stateInfo});
            claw.setClawOpen(true);
            macroState.nextState();
        };

        Function PIVOT_MOVING_TO_INTAKE_ORIENTATION = (obj) -> {
            MacroState macroState = (MacroState) obj;
            String stateInfo = "PIVOT_MOVING_TO_INTAKE_ORIENTATION";
            macroState.setCurrentStatus(new String[]{stateInfo});
            pivot.setTargetOrientation(Pivot.INTAKE_ORIENTATION);
            if (pivot.isAtTargetPos()) {
                elevator.startMoveToGround();
                macroState.nextState();
            }
        };

        Function ELEVATOR_MOVING_TO_GROUND = (obj) -> {
            MacroState macroState = (MacroState) obj;
            String stateInfo = "ELEVATOR_MOVING_TO_GROUND";
            String currentInfo = "Current: " + String.valueOf(elevator.getCurrent());
            String encoderInfo = "Enc Pos: " + String.valueOf(elevator.getEncPos()) + "| Enc Target: " + String.valueOf(elevator.getCurrentTarget());
            macroState.setCurrentStatus(new String[]{stateInfo, currentInfo, encoderInfo});
            if (elevator.isAtTargetPos()) {
                macroState.nextState();
            }
            macroState.nextState();
        };

        PLACE_CONE.addMicroState(new Function[]{
                INITIALIZE_MOVE_TO_PLACE, ELEVATOR_MOVING_TO_PLACE_ORIENTATION, PIVOT_MOVING_TO_PLACE_ORIENTATION,
                WAITING_FOR_PLACE_INPUT, OPENING_CLAW, PIVOT_MOVING_TO_INTAKE_ORIENTATION, ELEVATOR_MOVING_TO_GROUND});

        Function endFunction = (obj) -> {
            elevator.moveUsingEncoder(0);
            setClawOpen(false);
            MacroState macroState = (MacroState) obj;
            macroState.isFinished = true;
        };
        PLACE_CONE.setEnd(endFunction);
    }

    public void initManualControlState()
    {
        ButtonHelper intakeButtons = new ButtonHelper(EventHandler.instance.controls[1]);
        Function CONTROL_INTAKE = (obj) -> {
            String stateInfo = "";
            String elevatorInfo = "Elevator current: " + String.valueOf(elevator.getCurrent()) + " | ";
            elevatorInfo += String.valueOf(elevator.getEncPos());
            double moveValue = -EventHandler.instance.controls[1].left_stick_y;
            if (!elevator.elevatorBtn.isPressed() || moveValue > 0)
            {
                elevator.moveUsingEncoder(moveValue);
            }
            if (intakeButtons.pressing(ButtonHelper.dpad_up)){
                claw.toggleClaw();
            }
            if(intakeButtons.pressing(ButtonHelper.dpad_left)){
                togglePivot();
            }
        };
    }

    public void initStartingBehaviour(){}

    @Override
    public void init(HardwareMap map) {
        elevator = new Elevator();
        pivot = new Pivot();
        claw = new Claw();
        elevator.init(map);
        pivot.init(map);
        claw.init(map);

        initPlaceConeState();


    }

    /**
     * Start the pickup process with the pivot at the intake position.
     */
    public void startPickUp() {
    }

    public void startPlaceCone(int level) {
        startPlaceCone(level, false);
    }

    /**
     * Start to place the cone with the pivot at either position.
     *
     * @param level        the level to place the cone at
     * @param placeInFront place the cone with the pivot in the front position
     */
    public void startPlaceCone(int level, boolean placeInFront) {
        this.placeInFront = placeInFront;
        this.level = level;
        elevator.startMoveToPos(LEVELS[level]);
        currentState = State.ELEVATOR_MOVING_TO_PLACE_ORIENTATION;
    }

    public void togglePivot() {
        pivot.toggleTargetOrientation();
    }

    public void setClawOpen(boolean open) {
        claw.setClawOpen(open);
    }

    public void toggleClaw() {
        claw.toggleClaw();
    }

    @Override
    public void tickBeforeStart() {
        elevator.tickBeforeStart();
        pivot.tickBeforeStart();
    }

    @Override
    public void tick() {
        pivot.tick();
        elevator.tick();
        // Place the cone
        if (currentState == State.ELEVATOR_MOVING_TO_PLACE_ORIENTATION && elevator.isAtTargetPos()) {
            pivot.setTargetOrientation(!placeInFront);
            currentState = State.PIVOT_MOVING_TO_PLACE_ORIENTATION;
        } else if (currentState == State.PIVOT_MOVING_TO_PLACE_ORIENTATION && pivot.isAtTargetPos()) {
            currentState = State.WAITING_FOR_PLACE_INPUT;
        } else if (currentState == State.OPENING_CLAW && System.currentTimeMillis() > time + 500) {
            claw.toggleClaw();
            pivot.setTargetOrientation(Pivot.INTAKE_ORIENTATION);
            currentState = State.PIVOT_MOVING_TO_INTAKE_ORIENTATION;
        } else if (currentState == State.PIVOT_MOVING_TO_INTAKE_ORIENTATION && pivot.isAtTargetPos()) {
            elevator.startMoveToGround();
            currentState = State.ELEVATOR_MOVING_TO_GROUND;
        } else if (currentState == State.ELEVATOR_MOVING_TO_GROUND && elevator.isAtTargetPos()) {
            claw.toggleClaw();
            currentState = State.IDLE;
        }
    }

    /**
     * Advances to from {@link State#WAITING_FOR_PLACE_INPUT} to {@link State#OPENING_CLAW}.
     * This should be called when the position of the claw is aligned correctly to drop the cone.
     */
    public void confirmPlacePosition() {
        if (currentState == State.WAITING_FOR_PLACE_INPUT) {
            claw.setClawOpen(true);
            time = System.currentTimeMillis();
            currentState = State.OPENING_CLAW;
        }
    }

    public enum State {
        /**
         * The intake is currently idle. Use this to check whether the intake is moving.
         *
         * @see #isIdle()
         */
        IDLE,
        ELEVATOR_MOVING_TO_PLACE_ORIENTATION,
        PIVOT_MOVING_TO_PLACE_ORIENTATION,
        /**
         * Waiting for the user to confirm the place position before continuing.
         */
        WAITING_FOR_PLACE_INPUT,
        OPENING_CLAW,
        PIVOT_MOVING_TO_INTAKE_ORIENTATION,
        ELEVATOR_MOVING_TO_GROUND;

        public boolean isIdle() {
            return this == IDLE;
        }
    }
}

