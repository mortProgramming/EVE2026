package org.mort11.commands.actions.endeffector.pid;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj2.command.Command;

import java.util.function.DoubleSupplier;

import org.mort11.RobotContainer;
import org.mort11.subsystems.Shooter;

import static org.mort11.configs.constants.PhysicalConstants.Shooter.*;
import static org.mort11.configs.constants.PortConstants.Controller.*;

public class SetShooter extends Command {
    private final Shooter shooter;
    private DoubleSupplier RPM;

    public SetShooter(DoubleSupplier RPM) {
        shooter = Shooter.getInstance();
        this.RPM = RPM;

        addRequirements(shooter);
    }
    
    public SetShooter(double RPM) {
        shooter = Shooter.getInstance();
        this.RPM = () -> RPM;
            
        addRequirements(shooter);
    }
    
    @Override
    public void initialize() {
        
    }

    @Override
    public void execute() {
        shooter.setShooterRPM(RPM.getAsDouble());

        if (Math.abs(shooter.getShooterRPM() - RPM.getAsDouble()) / RPM.getAsDouble() < SHOOTER_SPEED_BUZZ_TOLERANCE) {
            RobotContainer.getEndeffectorController().setRumble(RumbleType.kBothRumble, RUMBLE_AMOUNT);
        }   else {
            RobotContainer.getEndeffectorController().setRumble(RumbleType.kBothRumble, 0);
        }
        
    }

    @Override
    public void end(boolean interrupted) {
        shooter.setShooterRPM(0);
        RobotContainer.getEndeffectorController().setRumble(RumbleType.kBothRumble, 0);
    }

    @Override
    public boolean isFinished() {
        return false; 
    }
    
}
