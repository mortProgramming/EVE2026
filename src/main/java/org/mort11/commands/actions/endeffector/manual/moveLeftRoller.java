package org.mort11.commands.actions.endeffector.manual;


import org.mort11.subsystems.IntakeRollerLeft;

import edu.wpi.first.wpilibj2.command.Command;

public class moveLeftRoller extends Command {
    private IntakeRollerLeft intakeRollerLeft;
    private double speed;

    public moveLeftRoller(double speed){
        intakeRollerLeft = IntakeRollerLeft.getInstance();
        this.speed = speed;
        addRequirements(intakeRollerLeft);
    }

    public void initialize(){
    }

    public void execute(){
        intakeRollerLeft.setSpeed(speed);
    }

    public void end(boolean interrupted){
        intakeRollerLeft.setSpeed(0);
    }
    
    public boolean isFinished(){
        return false;
    }
}
