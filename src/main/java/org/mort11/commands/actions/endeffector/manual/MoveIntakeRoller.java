package org.mort11.commands.actions.endeffector.manual;

import edu.wpi.first.wpilibj2.command.Command;
import org.mort11.subsystems.IntakeRoller;

public class MoveIntakeRoller extends Command {

    private final IntakeRoller roller;
    private final IntakeRoller.Speed speed;

    public MoveIntakeRoller(IntakeRoller roller, IntakeRoller.Speed speed) {
        this.roller = roller;
        this.speed = speed;
        addRequirements(roller);
    }

    @Override
    public void execute() {
        roller.setRoller(speed);
    }

    @Override
    public void end(boolean interrupted) {
        roller.stop();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}