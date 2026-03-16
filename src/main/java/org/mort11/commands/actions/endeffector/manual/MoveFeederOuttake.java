package org.mort11.commands.actions.endeffector.manual;

import edu.wpi.first.wpilibj2.command.Command;
import org.mort11.subsystems.Feeder;
import org.mort11.subsystems.Floor;

public class MoveFeederOuttake extends Command {

    private final Feeder feeder;
    private final Floor floor;

    public MoveFeederOuttake(Feeder feeder, Floor floor) {
        this.feeder = feeder;
        this.floor = floor;
        addRequirements(feeder, floor);
    }

    @Override
    public void execute() {
        feeder.set(Feeder.Speed.OUTTAKE);
        floor.setSpeed(-0.3);
    }

    @Override
    public void end(boolean interrupted) {
        feeder.stop();
        floor.setSpeed(0);
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}