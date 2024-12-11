
package unical.demacs.rdm.utils;

import java.util.Comparator;

public class JobAssignmentDifficultyComparator implements Comparator<JobAssignment> {
    
    @Override
    public int compare(JobAssignment a, JobAssignment b) {
        // Ordina per durata decrescente e priorità
        int durationComparison = Long.compare(b.getSchedule().getDuration(), a.getSchedule().getDuration());
        if (durationComparison != 0) {
            return durationComparison;
        }
        // Se la durata è uguale, considera la priorità
        return Integer.compare(
            b.getSchedule().getJob().getPriority().ordinal(),
            a.getSchedule().getJob().getPriority().ordinal()
        );
    }
}