package org.mort11.commands.actions.endeffector.manual;


import org.mort11.subsystems.IntakeRollerRight;

import edu.wpi.first.wpilibj2.command.Command;

public class moveRightRoller extends Command {
    private IntakeRollerRight intakeRollerRight;
    private double speed;

    public moveRightRoller(double speed){
        intakeRollerRight = IntakeRollerRight.getInstance();
        this.speed = speed;
        addRequirements(intakeRollerRight);
    }

    public void initialize(){
    }

    public void execute(){
        intakeRollerRight.setSpeed(speed);
    }

    public void end(boolean interrupted){
        intakeRollerRight.setSpeed(0);
    }
    
    public boolean isFinished(){
        return false;
    }
}