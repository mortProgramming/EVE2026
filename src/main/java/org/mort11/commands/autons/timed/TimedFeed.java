package org.mort11.commands.autons.timed;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import org.mort11.subsystems.Feeder;
import org.mort11.subsystems.Floor;

/**
 * Runs the feeder and floor for a fixed duration.
 * Mirrors SetFeeder's spin-out logic: briefly runs the feeder backwards first
 * to clear any jammed notes, then feeds normally.
 */
public class TimedFeed extends Command {

    private static final double SPIN_OUT_DURATION = 0.2; // seconds — matches SetFeeder

    private final Feeder feeder;
    private final Floor floor;
    private final Timer timer;
    private final Timer spinOutTimer;
    private final double time;
    private final double rpm;
    private final double floorSpeed;
    private boolean spinOutComplete;

    /**
     * @param time       How long to feed total (seconds), including spin-out phase
     * @param rpm        Feeder RPM (positive = feed toward shooter)
     * @param floorSpeed Floor motor speed (0.0 to 1.0)
     */
    public TimedFeed(double time, double rpm, double floorSpeed) {
        this.feeder = Feeder.getInstance();
        this.floor = Floor.getInstance();
        this.time = time;
        this.rpm = rpm;
        this.floorSpeed = floorSpeed;
        this.timer = new Timer();
        this.spinOutTimer = new Timer();
        addRequirements(feeder, floor);
    }

    /** Convenience constructor — floor defaults to 0.83 (same as SetFeeder). */
    public TimedFeed(double time, double rpm) {
        this(time, rpm, 0.83);
    }

    @Override
    public void initialize() {
        spinOutComplete = false;
        timer.reset();
        timer.start();
        spinOutTimer.reset();
        spinOutTimer.start();
    }

    @Override
    public void execute() {
        if (!spinOutComplete) {
            feeder.setRPM(-rpm);
            floor.setSpeed(0);
            if (spinOutTimer.hasElapsed(SPIN_OUT_DURATION)) {
                spinOutComplete = true;
            }
        } else {
            feeder.setRPM(rpm);
            floor.setSpeed(floorSpeed);
        }
    }

    @Override
    public void end(boolean interrupted) {
        feeder.stop();
        floor.setSpeed(0);
    }

    @Override
    public boolean isFinished() {
        return timer.get() > time;
    }
}