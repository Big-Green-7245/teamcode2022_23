package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Intake implements Modulable, Tickable {
    private static final int GROUND_LEVEL_POSITION = 0;
    private static final int LOW_LEVEL_POSITION = 0;
    private static final int MID_LEVEL_POSITION = 0;
    private static final int HIGH_LEVEL_POSITION = 0;

    private static final int[] LEVELS = new int[]{0, 6343 + 3108, 11808 + 3108, 18084 + 3108};
    private static final int SAFE_ROT_LEVEL = 0;

    public static final int GROUND = 0;
    public static final int LOW = 1;
    public static final int MID = 2;
    public static final int HIGH = 3;

    private final ElapsedTime runtime = new ElapsedTime();

    public HardwareMap hwMap;
    public Claw claw;
    public Pivot pivot;
    public Elevator elevator;

    @Override
    public void init(HardwareMap map) {
        hwMap = map;
        elevator = new Elevator();
        pivot = new Pivot();
        claw = new Claw();
        elevator.init(hwMap);
        pivot.init(hwMap);
        claw.init(hwMap);

        // Additional Initializations
    }

    public void placeCone(int level)
    {
        elevator.moveToPos(SAFE_ROT_LEVEL);
        pivot.setIntakeOrientation(true);
        elevator.moveToPos(LEVELS[level]);
        claw.clawOpen(true);
        elevator.moveToPos(SAFE_ROT_LEVEL);
        pivot.setIntakeOrientation(false);
        claw.clawOpen(false);

    }

    @Override
    public void tickBeforeStart() {
        elevator.tickBeforeStart();
        pivot.tickBeforeStart();
    }

    @Override
    public void tick() {
        pivot.tick();
    }
}

