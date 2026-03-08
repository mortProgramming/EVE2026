package org.mort11.commands.actions.endeffector.manual;

import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

import org.mort11.subsystems.IntakeArmLeft;

import edu.wpi.first.wpilibj2.command.Command;

public class moveLeftIntake extends Command {
    private IntakeArmLeft intakeArmLeft;
    private double speed;
    private CommandXboxController xboxController;

    public moveLeftIntake(CommandXboxController xboxController){
        intakeArmLeft = IntakeArmLeft.getInstance();
        this.xboxController=xboxController;
        addRequirements(intakeArmLeft);
    }
    public moveLeftIntake(double speed){
        intakeArmLeft = IntakeArmLeft.getInstance();
        this.speed = speed;
        this.xboxController=null;
        addRequirements(intakeArmLeft);
    }

    public void initialize(){
    }

    public void execute(){
        if (xboxController==null)
            intakeArmLeft.setSpeed(speed);
        else
        intakeArmLeft.setSpeed(xboxController.getLeftY());
    }

    public void end(boolean interrupted){
        intakeArmLeft.setSpeed(0);
    }
    
    public boolean isFinished(){
        return false;
    }

    public static Command IntakeFeather(){
        
        return new moveLeftIntake(0.45).withTimeout(0.1).repeatedly();
    }
}
