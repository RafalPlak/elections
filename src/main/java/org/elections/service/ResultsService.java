package org.elections.service;

import org.elections.dto.ResultRow;
import org.elections.repository.VoteRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResultsService {
    private final VoteRepository voteRepository;

    public ResultsService(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    @Cacheable(cacheNames = "electionResults", key = "#electionId")
    public List<ResultRow> results(Long electionId) {
        return voteRepository.countResults(electionId).stream()
                .map(arr -> new ResultRow((Long) arr[0], (Long) arr[1]))
                .toList();
    }
}
