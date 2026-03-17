package org.mort11.commands.actions.endeffector.manual;

import edu.wpi.first.wpilibj2.command.Command;
import org.mort11.subsystems.Hood;

public class MoveHood extends Command {

    private static final double SPEED_STEP = 0.02; // how much to increment per loop (~50hz)

    private final Hood hood;
    private final double direction; // +1.0 to move up, -1.0 to move down

    public MoveHood(Hood hood, double direction) {
        this.hood = hood;
        this.direction = direction;
        addRequirements(hood);
    }

    @Override
    public void execute() {
        hood.adjustPosition(direction * SPEED_STEP);
    }

    @Override
    public boolean isFinished() {
        return false; // runs until button released
    }

    @Override
    public void end(boolean interrupted) {
        // stop moving — hold current position
        hood.setPosition(hood.getCurrentPosition());
    }
}