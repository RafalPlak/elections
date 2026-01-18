package org.elections.controller;

import jakarta.validation.Valid;
import org.elections.dto.CreateVoterRequest;
import org.elections.dto.VoterListItem;
import org.elections.dto.VoterResponse;
import org.elections.service.VoterService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/voters")
public class VoterController {
    private final VoterService service;

    public VoterController(VoterService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<VoterResponse> create(@Valid @RequestBody CreateVoterRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }

    @GetMapping("/{id}")
    public VoterResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PatchMapping("/{id}/block")
    public VoterResponse block(@PathVariable Long id) {
        return service.setBlocked(id, true);
    }

    @PatchMapping("/{id}/unblock")
    public VoterResponse unblock(@PathVariable Long id) {
        return service.setBlocked(id, false);
    }

    @GetMapping
    public Page<VoterListItem> list(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return service.list(pageable);
    }
}
