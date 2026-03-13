package org.mort11.commands.actions.endeffector.pid;

import edu.wpi.first.wpilibj2.command.Command;

import java.util.function.DoubleSupplier;

import org.mort11.subsystems.EvanHood;

public class SetEvanHood extends Command {
    private EvanHood hood;
    private DoubleSupplier targetPositionDeg;

    public SetEvanHood(DoubleSupplier targetPositionDeg) {
        this.hood = EvanHood.getInstance();
        this.targetPositionDeg = targetPositionDeg;

        addRequirements(hood);
    }

    public SetEvanHood(double targetPositionDeg) {
        this.hood = EvanHood.getInstance();
        this.targetPositionDeg = () -> targetPositionDeg;

        addRequirements(hood);
    }

    @Override
    public void initialize() {
        hood.getPIDController().reset(hood.getHoodPositionDeg());
    }

    @Override
    public void execute() {
        hood.setHoodSpeed(
            -hood.getPIDController().calculate(
                hood.getHoodPositionDeg(), 
                targetPositionDeg.getAsDouble()
            )
        );
    }

    @Override
    public boolean isFinished() {
        return hood.getPIDController().atGoal();
    }

    @Override
    public void end(boolean interrupted) {
        hood.setHoodSpeed(0);
    }
}
