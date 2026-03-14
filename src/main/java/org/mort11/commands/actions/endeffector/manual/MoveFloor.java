package org.mort11.commands.actions.endeffector.manual;
import edu.wpi.first.wpilibj2.command.Command;
import org.mort11.subsystems.Floor;
public class MoveFloor extends Command{
    public double speed;
    public Floor floor;
    public MoveFloor(double speed){
        floor=Floor.getInstance();
        this.speed=speed;
        addRequirements(floor);
    }
    @Override
    public void execute(){
        floor.setSpeed(speed);
    }
    @Override
    public boolean isFinished(){
        return false;
    }
    @Override
    public void end(boolean interrupted){
        floor.setSpeed(0);
    }
    @Override
    public void initialize(){
        
    }
}
