package org.elections.domain;

import jakarta.persistence.*;

@Entity
@Table(
        name = "election_options",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_option_name_per_election",
                columnNames = {"election_id", "name"}
        )
)
public class ElectionOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "election_id")
    private Election election;

    @Column(nullable=false)
    private String name;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Election getElection() { return election; }
    public void setElection(Election election) { this.election = election; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}