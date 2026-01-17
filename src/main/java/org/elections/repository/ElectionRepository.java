package org.elections.repository;

import org.elections.domain.Election;
import org.elections.domain.ElectionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectionRepository extends JpaRepository<Election, Long> {
    Page<Election> findByStatus(ElectionStatus status, Pageable pageable);
}
