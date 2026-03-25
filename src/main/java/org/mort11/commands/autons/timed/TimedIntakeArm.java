package org.mort11.commands.autons.timed;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import org.mort11.subsystems.IntakeArm;

public class TimedIntakeArm extends Command {

    private final IntakeArm arm;
    private final Timer timer;
    private final double time;
    private final IntakeArm.Position position;

    /**
     * Moves the intake arm to a position and holds it for a set duration.
     * Unlike SetArm, this will NOT exit early when tolerance is reached —
     * it always runs for the full time, which is safer in auton sequences.
     *
     * @param time     How long to run (seconds)
     * @param arm      The IntakeArm subsystem instance (passed in from RobotContainer)
     * @param position The target arm position (HOMED, STOWED, INTAKE, or AGITATE)
     */
    public TimedIntakeArm(double time, IntakeArm arm, IntakeArm.Position position) {
        this.arm = arm;
        this.time = time;
        this.position = position;
        this.timer = new Timer();
        addRequirements(arm);
    }

    @Override
    public void initialize() {
        timer.reset();
        timer.start();
        arm.setPivot(position);
    }

    @Override
    public void execute() {
        // arm.setPivot() is set in initialize(); MotionMagic runs continuously in the subsystem
    }

    @Override
    public void end(boolean interrupted) {
        // Intentionally do NOT stop the arm — hold position after command ends
    }

    @Override
    public boolean isFinished() {
        return timer.get() > time;
    }
}