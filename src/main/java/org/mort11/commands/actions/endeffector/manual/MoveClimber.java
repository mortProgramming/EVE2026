package org.mort11.commands.actions.endeffector.manual;

import org.mort11.subsystems.Climber;
import edu.wpi.first.wpilibj2.command.Command;


public class MoveClimber extends Command{

    public double speed;
    public Climber climber;
    
    public MoveClimber(double speed){
        climber = Climber.getInstance();
        this.speed=speed;
        addRequirements(climber);
    }

    @Override
    public void execute(){
        climber.setPercentOutput(speed);
    }

    @Override
    public boolean isFinished(){
        return false;
    }

    @Override
    public void end(boolean interrupted){
        climber.setPercentOutput(0);
    }
    
    @Override
    public void initialize(){
        
    }
    
}
