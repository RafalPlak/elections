package org.elections.repository;

import org.elections.domain.ElectionOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ElectionOptionRepository extends JpaRepository<ElectionOption, Long> {
    List<ElectionOption> findByElectionId(Long electionId);
    Optional<ElectionOption> findByIdAndElectionId(Long optionId, Long electionId);
}
