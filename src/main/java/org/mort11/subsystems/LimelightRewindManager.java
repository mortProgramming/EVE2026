package org.mort11.subsystems;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

public final class LimelightRewindManager {
    private static final String FRONT_CAMERA = "limelight-three";
    private static final String BACK_CAMERA = "limelight-back";
    private static final String[] CAMERAS = {FRONT_CAMERA, BACK_CAMERA};

    private static final double MAX_CLIP_SECONDS = 165.0;
    private static final double PRACTICE_ARM_SECONDS = 20.0;
    private static final double MIN_SECONDS_BETWEEN_CAPTURES = 2.1;

    private final NetworkTable controlTable = NetworkTableInstance.getDefault().getTable("Rewind");
    private final NetworkTableEntry manualSaveNowEntry = controlTable.getEntry("SaveNow");
    private final NetworkTableEntry practiceAutoSaveEnabledEntry = controlTable.getEntry("PracticeAutoSaveEnabled");
    private final NetworkTableEntry clipLengthSecondsEntry = controlTable.getEntry("ClipLengthSeconds");
    private final NetworkTableEntry armedEntry = controlTable.getEntry("Armed");
    private final NetworkTableEntry statusEntry = controlTable.getEntry("Status");
    private final NetworkTableEntry lastReasonEntry = controlTable.getEntry("LastReason");
    private final NetworkTableEntry lastSaveTimestampEntry = controlTable.getEntry("LastSaveTimestampSec");

    private boolean wasEnabled = false;
    private boolean previousManualSaveRequest = false;
    private boolean sawFmsWhileEnabled = false;
    private boolean practiceAutoSaveArmed = false;

    private double enabledStartTimeSec = 0.0;
    private double lastCaptureTimeSec = -1e9;
    private long captureCounter = 0L;

    public LimelightRewindManager() {
        manualSaveNowEntry.setBoolean(false);
        practiceAutoSaveEnabledEntry.setBoolean(true);
        clipLengthSecondsEntry.setDouble(MAX_CLIP_SECONDS);
        armedEntry.setBoolean(false);
        statusEntry.setString("Idle");
        lastReasonEntry.setString("None");
        lastSaveTimestampEntry.setDouble(0.0);

        for (String camera : CAMERAS) {
            setRewindEnabled(camera, true);
        }

        NetworkTableInstance.getDefault().flush();
    }

    public void periodic() {
        final boolean enabled = DriverStation.isEnabled();
        final boolean fmsAttached = DriverStation.isFMSAttached();
        final double nowSec = Timer.getFPGATimestamp();

        handleManualSave(nowSec);

        if (enabled && !wasEnabled) {
            enabledStartTimeSec = nowSec;
            sawFmsWhileEnabled = fmsAttached;
            practiceAutoSaveArmed = false;
            setStatus(fmsAttached ? "Enabled on FMS" : "Enabled off FMS");
        }

        if (enabled) {
            sawFmsWhileEnabled |= fmsAttached;

            final boolean practiceAutoSaveEnabled = practiceAutoSaveEnabledEntry.getBoolean(true);
            if (!sawFmsWhileEnabled
                    && practiceAutoSaveEnabled
                    && (nowSec - enabledStartTimeSec) >= PRACTICE_ARM_SECONDS) {
                practiceAutoSaveArmed = true;
            }
        }

        if (!enabled && wasEnabled) {
            if (sawFmsWhileEnabled) {
                captureBoth("FMS_MATCH_END", nowSec);
            } else if (practiceAutoSaveArmed) {
                captureBoth("PRACTICE_END", nowSec);
            } else {
                setStatus("Disabled - no rewind save requested");
            }

            sawFmsWhileEnabled = false;
            practiceAutoSaveArmed = false;
        }

        armedEntry.setBoolean(sawFmsWhileEnabled || practiceAutoSaveArmed);
        wasEnabled = enabled;
    }

    private void handleManualSave(double nowSec) {
        final boolean manualSaveRequest = manualSaveNowEntry.getBoolean(false);
        final boolean risingEdge = manualSaveRequest && !previousManualSaveRequest;
        previousManualSaveRequest = manualSaveRequest;

        if (risingEdge) {
            captureBoth("MANUAL", nowSec);
            manualSaveNowEntry.setBoolean(false);
            previousManualSaveRequest = false;
        }
    }

    private void captureBoth(String reason, double nowSec) {
        if ((nowSec - lastCaptureTimeSec) < MIN_SECONDS_BETWEEN_CAPTURES) {
            setStatus("Skipped " + reason + " - capture rate limited");
            return;
        }

        final double requestedClipSeconds = clipLengthSecondsEntry.getDouble(MAX_CLIP_SECONDS);
        final double clipSeconds = clamp(requestedClipSeconds, 1.0, MAX_CLIP_SECONDS);

        captureCounter++;

        for (String camera : CAMERAS) {
            triggerRewindCapture(camera, captureCounter, clipSeconds);
        }

        NetworkTableInstance.getDefault().flush();

        lastCaptureTimeSec = nowSec;
        lastReasonEntry.setString(reason);
        lastSaveTimestampEntry.setDouble(nowSec);
        setStatus("Saved " + reason + " for " + clipSeconds + "s from both cameras");
    }

    private static void setRewindEnabled(String limelightName, boolean enabled) {
        NetworkTableInstance.getDefault()
                .getTable(limelightName)
                .getEntry("rewind_enable_set")
                .setNumber(enabled ? 1 : 0);
    }

    private static void triggerRewindCapture(String limelightName, long counter, double durationSeconds) {
        NetworkTableInstance.getDefault()
                .getTable(limelightName)
                .getEntry("capture_rewind")
                .setDoubleArray(new double[] {counter, durationSeconds});
    }

    private void setStatus(String status) {
        statusEntry.setString(status);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}