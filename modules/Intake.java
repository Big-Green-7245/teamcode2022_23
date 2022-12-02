package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Intake implements Modulable
{
    private ElapsedTime runtime = new ElapsedTime();

    public HardwareMap hwMap;
    public Claw claw;
    public Pivot pivot;
    public Elevator elevator;

    private final int GROUND_LEVEL_POSITION = 0;
    private final int LOW_LEVEL_POSITION = 0;
    private final int MID_LEVEL_POSITION = 0;
    private final int HIGH_LEVEL_POSITION = 0;

    private final int[] LEVELS = new int[]{0, 0, 0, 0};
    private final int SAFE_ROT_LEVEL = 0;

    public final int GROUND = 0;
    public final int LOW = 1;
    public final int MID = 2;
    public final int HIGH = 3;

    @Override
    public void init(HardwareMap map)
    {
        hwMap = map;

        // Additional Initializations
    }

    public void placeCone(int level)
    {
        elevator.moveToPos(SAFE_ROT_LEVEL);
        pivot.setIntakeOrientation(true); //Make sure function do not wait. TODO
        elevator.moveToPos(LEVELS[level]);
        claw.clawOpen(true);
        elevator.moveToPos(SAFE_ROT_LEVEL);
        pivot.setIntakeOrientation(false); //Make sure function do not wait. TODO
        claw.clawOpen(false);

    }

    // Additional methods for functionality
}
