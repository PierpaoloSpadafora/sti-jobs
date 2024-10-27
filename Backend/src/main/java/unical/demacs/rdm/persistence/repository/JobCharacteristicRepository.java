package unical.demacs.rdm.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unical.demacs.rdm.persistence.entities.Job;
import unical.demacs.rdm.persistence.entities.JobCharacteristic;

import java.util.List;

@Repository
public interface JobCharacteristicRepository extends JpaRepository<JobCharacteristic, Long> {

    List<JobCharacteristic> findByJobId(Long jobId);
}
