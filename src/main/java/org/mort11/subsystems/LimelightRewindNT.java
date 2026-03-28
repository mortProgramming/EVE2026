package org.mort11.subsystems;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * Limelight rewind capture via NetworkTables.
 *
 * capture_rewind = [counter, duration_seconds]
 * Counter is a request ID (NOT frames) and must change each capture request.
 */
public final class LimelightRewindNT {
    private final NetworkTable table;
    private long captureCounter;

    public LimelightRewindNT(String limelightTableName) {
        this.table = NetworkTableInstance.getDefault().getTable(limelightTableName);

        // Optional: resume counter so redeploys don't reuse an old value.
        double[] existing = table.getEntry("capture_rewind").getDoubleArray(new double[] {0, 0});
        this.captureCounter = existing.length > 0 ? (long) existing[0] : 0L;
    }

    public void setRewindEnabled(boolean enabled) {
        table.getEntry("rewind_enable_set").setDouble(enabled ? 1.0 : 0.0);
    }

    /** Saves the last durationSeconds (NetworkTables max is 165 seconds). */
    public void captureLastSeconds(int durationSeconds) {
        int clamped = Math.max(1, Math.min(165, durationSeconds));
        captureCounter++;
        table.getEntry("capture_rewind").setDoubleArray(new double[] {captureCounter, clamped});
    }
}

//     private final NetworkTable table;
//     private long captureCounter;

//     public LimelightRewindNT(String limelightTableName) {
//         this.table = NetworkTableInstance.getDefault().getTable(limelightTableName);

//         // Optional: resume from existing value so redeploys don't reuse old counters.
//         double[] existing = table.getEntry("capture_rewind").getDoubleArray(new double[] {0, 0});
//         this.captureCounter = existing.length > 0 ? (long) existing[0] : 0L;
//     }

//     public void setRewindEnabled(boolean enabled) {
//         table.getEntry("rewind_enable_set").setDouble(enabled ? 1.0 : 0.0);
//     }

//     /** Saves the last durationSeconds (NetworkTables max is 165). */
//     public void captureLastSeconds(int durationSeconds) {
//         int clamped = Math.max(1, Math.min(165, durationSeconds));
//         captureCounter++;
//         table.getEntry("capture_rewind").setDoubleArray(new double[] {captureCounter, clamped});
//     }
// }
