package org.mort11.commands.actions.endeffector.pid;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.Commands;
import org.mort11.subsystems.Hood;

public class ShootFar extends ParallelCommandGroup {

    public ShootFar(Hood hood, double rpm, double hoodPosition) {
        addCommands(
            new SetShooter(rpm),
            Commands.runOnce(() -> hood.setPosition(hoodPosition), hood));
    }
}