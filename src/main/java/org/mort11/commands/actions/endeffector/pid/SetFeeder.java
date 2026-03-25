package org.mort11.commands.actions.endeffector.pid;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;

import java.util.function.DoubleSupplier;

import org.mort11.RobotContainer;
import org.mort11.subsystems.Feeder;
import org.mort11.subsystems.Floor;

public class SetFeeder extends Command {
    private static final double SPIN_OUT_DURATION = 0.5; // seconds

    private final Feeder feeder;
    private final Floor floor;
    private final DoubleSupplier rpmSupplier;
    private final double floorSpeed;
    private final Timer spinOutTimer;
    private boolean spinOutComplete;

    public SetFeeder(DoubleSupplier rpmSupplier, double floorSpeed) {
        this.feeder = Feeder.getInstance();
        this.floor = Floor.getInstance();
        this.rpmSupplier = rpmSupplier;
        this.floorSpeed = floorSpeed;
        this.spinOutTimer = new Timer();
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
    public void initialize() {
        spinOutComplete = false;
        spinOutTimer.reset();
        spinOutTimer.start();
    }

    @Override
    public void execute() {
        if (!spinOutComplete) {
            // Spin the feeder backwards, hold the floor
            feeder.setRPM(-rpmSupplier.getAsDouble());
            floor.setSpeed(0);

            if (spinOutTimer.hasElapsed(SPIN_OUT_DURATION)) {
                spinOutComplete = true;
            }
        } else {
            // Normal feeding
            feeder.setRPM(rpmSupplier.getAsDouble());
            floor.setSpeed(floorSpeed);
        }
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