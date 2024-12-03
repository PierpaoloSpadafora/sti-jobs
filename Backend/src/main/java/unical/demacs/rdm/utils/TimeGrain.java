package unical.demacs.rdm.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class TimeGrain {
    private int grainIndex;
    private long startTimeInSeconds;
}
