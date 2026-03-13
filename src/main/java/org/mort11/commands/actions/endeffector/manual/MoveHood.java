package org.mort11.commands.actions.endeffector.manual;

import edu.wpi.first.wpilibj2.command.Command;
import org.mort11.subsystems.Hood;

public class MoveHood extends Command {

    private final Hood hood;
    private final double position;

    public MoveHood(Hood hood, double position) {
        this.hood = hood;
        this.position = position;
        addRequirements(hood);
    }

    @Override
    public void initialize() {
        hood.setPosition(position);
    }

    @Override
    public boolean isFinished() {
        return hood.isPositionWithinTolerance();
    }
}