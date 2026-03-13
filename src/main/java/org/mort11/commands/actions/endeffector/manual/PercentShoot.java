package org.mort11.commands.actions.endeffector.manual;

import edu.wpi.first.wpilibj2.command.Command;
import org.mort11.subsystems.Shooter;

public class PercentShoot extends Command {
    private final Shooter shooter;
    private final double percent;

    public PercentShoot(Shooter shooter, double percent) {
        this.shooter = shooter;
        this.percent = percent;
        addRequirements(shooter);
    }

    @Override
    public void execute() {
        shooter.setPercentOutput(percent);
    }

    @Override
    public void end(boolean interrupted) {
        shooter.stop();
    }

    @Override
    public boolean isFinished() { 
        return false;
     }
}