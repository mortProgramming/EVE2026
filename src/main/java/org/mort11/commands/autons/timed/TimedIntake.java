package org.mort11.commands.autons.timed;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import org.mort11.subsystems.IntakeRoller;

public class TimedIntake extends Command {

    private final IntakeRoller roller;
    private final Timer timer;
    private final double time;
    private final IntakeRoller.Speed speed;

    /**
     * Runs the intake roller at a given Speed enum for a set duration.
     *
     * @param time  How long to run (seconds)
     * @param roller The IntakeRoller subsystem instance
     * @param speed IntakeRoller.Speed.INTAKE or OUTTAKE
     */
    public TimedIntake(double time, IntakeRoller roller, IntakeRoller.Speed speed) {
        this.roller = roller;
        this.time = time;
        this.speed = speed;
        this.timer = new Timer();
        addRequirements(roller);
    }

    @Override
    public void initialize() {
        timer.reset();
        timer.start();
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
        return timer.get() > time;
    }
}