package unical.demacs.rdm.persistence.service.implementation;

import com.google.common.util.concurrent.RateLimiter;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import unical.demacs.rdm.config.exception.JobCharacteristicException;
import unical.demacs.rdm.config.exception.JobCharacteristicNotFoundException;
import unical.demacs.rdm.config.exception.TooManyRequestsException;
import unical.demacs.rdm.persistence.entities.JobCharacteristic;
import unical.demacs.rdm.persistence.repository.JobCharacteristicRepository;
import unical.demacs.rdm.persistence.service.interfaces.IJobCharacteristic;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class JobCharacteristicImpl implements IJobCharacteristic {

    public static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private JobCharacteristicRepository jobCharacteristicRepository;
    private final RateLimiter rateLimiter;

    public List<JobCharacteristic> findByJob(Long jobId) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Attempting to get job characteristics by job id: {}", jobId);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for findByJob");
                throw new TooManyRequestsException();
            }
            List<JobCharacteristic> jobCharacteristics = jobCharacteristicRepository.findByJobId(jobId);
            if (jobCharacteristics.isEmpty()) {
                logger.warn("Job characteristics not found for job id: {}", jobId);
                throw new JobCharacteristicNotFoundException("Job characteristics not found for job id: " + jobId);
            }
            return jobCharacteristics;
        } catch (Exception e) {
            logger.error("Error getting job characteristics by job id: {}", jobId, e);
            throw new JobCharacteristicException("Error getting job characteristics");
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }
}
