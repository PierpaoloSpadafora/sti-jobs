package unical.demacs.rdm.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TimeWindow {
    private long startTime;
    private long endTime;
}
