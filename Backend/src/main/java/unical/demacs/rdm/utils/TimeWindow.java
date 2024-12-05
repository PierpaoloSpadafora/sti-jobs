package unical.demacs.rdm.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a time interval with start and end times in epoch seconds
 */
@Data
@AllArgsConstructor
public class TimeWindow {
    private long startTime;  // Start time in epoch seconds
    private long endTime;    // End time in epoch seconds
}
