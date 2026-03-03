package org.mort11.commands.actions.endeffector.manual;

import edu.wpi.first.wpilibj2.command.Command;
import org.mort11.subsystems.Shooter;

public class PercentShoot extends Command {
    private final Shooter shooter;
    private double percent;

        public PercentShoot(double percent) {
            shooter = Shooter.getInstance();
            this.percent = percent;
            addRequirements(shooter);
        }
    
    @Override
    public void initialize() {
        
    }

    @Override
    public void execute() {
        shooter.setShooterPercent(percent);
    }

    @Override
    public void end(boolean interrupted) {
        shooter.setShooterPercent(0);
    }

    @Override
    public boolean isFinished() {
        return false; // Change this to true when the shooting action is complete
    }
    
}
