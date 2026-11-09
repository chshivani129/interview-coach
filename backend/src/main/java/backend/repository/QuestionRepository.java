package backend.repository;

import backend.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByJobProfileId(Long jobProfileId);

    List<Question> findByJobProfileIdAndSkill(Long jobProfileId, String skill);
}