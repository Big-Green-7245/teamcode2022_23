package org.firstinspires.ftc.teamcode.util;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The running actions on each major module
 */
public class MacroState {

    /**
     * Whether or not the action been completed
     */
    public boolean isFinished = false;

    /**
     * Instructions to terminate the state
     */
    private Function end;
    public void setEnd(Function endFunction){
        end = endFunction;
    }
    public void terminateAllProcesses(){
        end.call(this);
        isFinished = true;
    }

    /**
     * The sub-states which the larger state iterates through in sequence
     */
    private ArrayList<MicroState> microStates = new ArrayList<MicroState>(){};

    /**
     * Adds a sub-state
     * @param runMethod The action that the state performs each iteration
     */
    public void addMicroState(Function runMethod){
        microStates.add(new MicroState(runMethod));
    }

    /**
     * Adds multiple sub-states
     * @param runMethods The actions that the state performs each iteration
     */
    public void addMicroState(Function[] runMethods){
        for (int i = 0; i < runMethods.length; i++){
            addMicroState(runMethods[i]);
        }
    }

//    public MacroState(Function[] runMethods, Function endMethod, Component[] componentsInUse){
//        addMicroState(runMethods);
//        end = endMethod;
//        inUse = new ArrayList<>(Arrays.asList(componentsInUse));
//    }

    /**
     * An integer for the index in the list of the current sub-state
     */
    private int currentState = 0;
    public MicroState getCurrentState(){return microStates.get(currentState);}

    /**
     * Moves to next state in sequence
     */
    public void nextState(){currentState++;}

    /**
     * Information to be printed in Telemetry by EventHandler
     */
    private String[] status = new String[]{};
    public String[] getCurrentStatus(){return status;}
    public void setCurrentStatus(String[] newStatus){status = newStatus;}

    /**
     * The components that this state makes use of
     */
    private ArrayList<Component> inUse ;

    /**
     * Checks if a specific component is inUse
     * @param comp The Component to check
     * @return Returns true if the state uses that Component
     */
    public boolean isInUse(Component comp){
        if (inUse.contains(comp)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Checks if two states interfere with one another by using the same component
     * @param state The MacroState to compare
     * @return Returns true if they do not use any of the same components
     */
    public boolean canParallel(MacroState state){
        boolean canParallel = true;
        for (int i = 0; i < inUse.size(); i ++){
            if (state.isInUse(inUse.get(i))){
                canParallel = false;
                break;
            }
        }
        return canParallel;
    }

    /**
     * Runs the current sub-state each iteration until all of them are completed
     */
    public void runEachIteration(){
        getCurrentState().action.call(this);
        if (currentState == microStates.size()){
            isFinished = true;
        }
    }

}
