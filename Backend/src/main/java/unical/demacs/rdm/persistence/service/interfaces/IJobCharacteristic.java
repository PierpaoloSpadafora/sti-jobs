package unical.demacs.rdm.persistence.service.interfaces;

import unical.demacs.rdm.persistence.entities.JobCharacteristic;

import java.util.List;
import java.util.Optional;

public interface IJobCharacteristic {
    List<JobCharacteristic> findByJob(Long jobId);
}
