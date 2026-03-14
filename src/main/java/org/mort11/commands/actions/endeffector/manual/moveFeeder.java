package org.mort11.commands.actions.endeffector.manual;

import edu.wpi.first.wpilibj2.command.Command;
import org.mort11.subsystems.Feeder;

public class MoveFeeder extends Command {

    private final Feeder feeder;

    public MoveFeeder(Feeder feeder) {
        this.feeder = feeder;
        addRequirements(feeder);
    }

    @Override
    public void execute() {
        feeder.set(Feeder.Speed.FEED);
    }

    @Override
    public void end(boolean interrupted) {
        feeder.stop();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}