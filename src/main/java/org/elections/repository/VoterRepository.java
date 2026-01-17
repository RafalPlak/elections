package org.elections.repository;

import org.elections.domain.Voter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoterRepository extends JpaRepository<Voter, Long> {
    boolean existsByPesel(String pesel);
}
