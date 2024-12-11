
package unical.demacs.rdm.utils;

import java.util.Comparator;

public class JobAssignmentDifficultyComparator implements Comparator<JobAssignment> {
    
    @Override
    public int compare(JobAssignment a, JobAssignment b) {
        int durationComparison = Long.compare(b.getSchedule().getDuration(), a.getSchedule().getDuration());
        if (durationComparison != 0) {
            return durationComparison;
        }
        return Integer.compare(
            b.getSchedule().getJob().getPriority().ordinal(),
            a.getSchedule().getJob().getPriority().ordinal()
        );
    }
}