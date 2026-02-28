package org.mort11.commands.actions.endeffector.manual;

import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

import org.mort11.subsystems.IntakeArmRight;

import edu.wpi.first.wpilibj2.command.Command;

public class moveRightIntake extends Command {
    private IntakeArmRight intakeArmRight;
    private double speed;
    private CommandXboxController xboxController;

    public moveRightIntake(CommandXboxController xboxController){
        intakeArmRight = IntakeArmRight.getInstance();
        this.xboxController=xboxController;
        addRequirements(intakeArmRight);
    }

    public moveRightIntake(double speed){
        intakeArmRight = IntakeArmRight.getInstance();
        this.speed = speed;
        this.xboxController=null;
        addRequirements(intakeArmRight);
    }

    public void initialize(){
    }

    public void execute(){
        if (xboxController==null)
            intakeArmRight.setSpeed(speed);
        else
        intakeArmRight.setSpeed(xboxController.getRightY());
    }

    public void end(boolean interrupted){
        intakeArmRight.setSpeed(0);
    }
    
    public boolean isFinished(){
        return false;
    }
}
