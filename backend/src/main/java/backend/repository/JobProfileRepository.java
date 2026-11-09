package backend.repository;

import backend.domain.JobProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobProfileRepository extends JpaRepository<JobProfile, Long> {
}