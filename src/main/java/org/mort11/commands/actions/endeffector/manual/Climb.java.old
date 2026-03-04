package org.mort11.commands.actions.endeffector.manual;

import edu.wpi.first.wpilibj2.command.Command;
import org.mort11.subsystems.Climber;

public class Climb extends Command {
    private Climber climber;
    private double speed;

    public Climb(double speed){
        climber = Climber.getInstance();
        this.speed = speed;
        addRequirements(climber);
    }

    public void initialize(){
    }

    public void execute(){
        climber.setSpeed(speed);
    }

    public void end(boolean interrupted){
        climber.setSpeed(0);
    }
    
    public boolean isFinished(){
        return false;
    }   
}



