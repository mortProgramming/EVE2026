package org.mort11.commands.actions.endeffector.pid;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj2.command.Command;

import java.util.function.DoubleSupplier;

import org.mort11.RobotContainer;
import org.mort11.subsystems.Feeder;
import org.mort11.subsystems.Floor;

public class SetFeeder extends Command {
    private final Feeder feeder;
    private final Floor floor;
    private final DoubleSupplier rpmSupplier;
    private final double floorSpeed;

    public SetFeeder(DoubleSupplier rpmSupplier, double floorSpeed) {
        this.feeder = Feeder.getInstance();
        this.floor = Floor.getInstance();
        this.rpmSupplier = rpmSupplier;
        this.floorSpeed = floorSpeed;
        addRequirements(feeder, floor);
    }

    public SetFeeder(double rpm, double floorSpeed) {
        this(() -> rpm, floorSpeed);
    }

    // Backwards-compatible constructor — floor defaults to 0.8 like MoveFeeder
    public SetFeeder(DoubleSupplier rpmSupplier) {
        this(rpmSupplier, 0.83);
    }

    public SetFeeder(double rpm) {
        this(() -> rpm, 0.83);
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {
        feeder.setRPM(rpmSupplier.getAsDouble());
        floor.setSpeed(floorSpeed);
    }

    @Override
    public void end(boolean interrupted) {
        feeder.stop();
        floor.setSpeed(0);
        RobotContainer.getEndeffectorController().setRumble(RumbleType.kBothRumble, 0);
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}