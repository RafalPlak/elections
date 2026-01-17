package org.elections.repository;

import org.elections.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByVoterIdAndElectionId(Long voterId, Long electionId);

    @Query("""
    select v.option.id as optionId, count(v.id) as votes
    from Vote v
    where v.election.id = :electionId
    group by v.option.id
  """)
    List<Object[]> countResults(Long electionId);
}
