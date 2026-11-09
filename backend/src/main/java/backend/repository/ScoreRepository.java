package backend.repository;

import backend.domain.Score;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScoreRepository extends JpaRepository<Score, Long> {

    Optional<Score> findByAttemptId(Long attemptId);

    List<Score> findAllByOrderByCreatedAtDesc();
}