package org.mort11.subsystems;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Auto + manual rewind recording policy:
 * - Auto record on Enabled->Disabled if (FMS attached OR dashboard armed) AND enabled long enough.
 * - Manual "Record Now" dashboard button saves last (preRoll + afterRoll) seconds while enabled.
 */
public final class LimelightRewindManager {
    private static final String KEY_ARM_AUTO = "LL Rewind/Arm Auto Record";
    private static final String KEY_MANUAL = "LL Rewind/Record Now";

    private static final int MATCH_SECONDS = 160; // 2m40s
    private static final int MIN_ENABLED_SECONDS = 25; // ignore quick cart checks
    private static final int MANUAL_PRE_ROLL_SECONDS = 10;
    private static final int MANUAL_AFTER_ROLL_SECONDS = 5;

    private final LimelightRewindNT rewind;
    private boolean wasEnabled = false;
    private double enabledStartSec = 0.0;

    private boolean lastManualButton = false;

    public LimelightRewindManager(LimelightRewindNT rewind) {
        this.rewind = rewind;

        SmartDashboard.setDefaultBoolean(KEY_ARM_AUTO, false);
        SmartDashboard.setDefaultBoolean(KEY_MANUAL, false);
    }

    public void robotInit() {
        rewind.setRewindEnabled(true);
    }

    /** Call from robotPeriodic(). */
    public void periodic() {
        boolean enabled = DriverStation.isEnabled();

        // Track enabled duration (for spam prevention)
        if (!wasEnabled && enabled) {
            enabledStartSec = Timer.getFPGATimestamp();
        }

        // Manual button: rising edge only
        boolean manualBtn = SmartDashboard.getBoolean(KEY_MANUAL, false);
        if (!lastManualButton && manualBtn) {
            if (enabled) {
                int duration = MANUAL_PRE_ROLL_SECONDS + MANUAL_AFTER_ROLL_SECONDS;
                rewind.captureLastSeconds(duration);
            }
            // auto-reset the button so it acts like a momentary press in Elastic
            SmartDashboard.putBoolean(KEY_MANUAL, false);
        }
        lastManualButton = manualBtn;

        // Auto capture at match end: Enabled -> Disabled
        if (wasEnabled && !enabled) {
            double enabledDuration = Timer.getFPGATimestamp() - enabledStartSec;

            boolean armed = SmartDashboard.getBoolean(KEY_ARM_AUTO, false);
            boolean shouldAutoRecord = DriverStation.isFMSAttached() || armed;

            if (shouldAutoRecord && enabledDuration >= MIN_ENABLED_SECONDS) {
                rewind.captureLastSeconds(MATCH_SECONDS);
            }
        }

        wasEnabled = enabled;
    }
}