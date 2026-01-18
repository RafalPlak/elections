package org.elections.controller;

import jakarta.validation.Valid;
import org.elections.dto.CastVoteRequest;
import org.elections.dto.VoteResponse;
import org.elections.service.VotingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/elections/{electionId}/votes")
public class VotingController {
    private final VotingService votingService;

    public VotingController(VotingService votingService) { this.votingService = votingService; }

    @PostMapping
    public ResponseEntity<VoteResponse> cast(@PathVariable Long electionId, @Valid @RequestBody CastVoteRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(votingService.castVote(electionId, req));
    }
}
