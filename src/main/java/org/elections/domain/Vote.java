package org.elections.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "votes",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_vote_per_voter_per_election",
                columnNames = {"voter_id", "election_id"}
        )
)
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "voter_id")
    private Voter voter;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "election_id")
    private Election election;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "option_id")
    private ElectionOption option;

    @Column(nullable = false)
    private Instant castAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Voter getVoter() {
        return voter;
    }

    public void setVoter(Voter voter) {
        this.voter = voter;
    }

    public Election getElection() {
        return election;
    }

    public void setElection(Election election) {
        this.election = election;
    }

    public ElectionOption getOption() {
        return option;
    }

    public void setOption(ElectionOption option) {
        this.option = option;
    }

    public Instant getCastAt() {
        return castAt;
    }

    public void setCastAt(Instant castAt) {
        this.castAt = castAt;
    }
}