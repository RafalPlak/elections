package org.elections.controller;

import jakarta.validation.Valid;
import org.elections.domain.ElectionStatus;
import org.elections.dto.*;
import org.elections.service.ElectionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/elections")
public class ElectionController {
    private final ElectionService service;

    public ElectionController(ElectionService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<ElectionResponse> create(@Valid @RequestBody CreateElectionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }

    @GetMapping("/{id}")
    public ElectionResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping
    public Page<ElectionListItem> list(
            @RequestParam ElectionStatus status,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return service.list(Optional.of(status), pageable);
    }

    @PostMapping("/{id}/options")
    public ResponseEntity<OptionResponse> addOption(@PathVariable Long id, @Valid @RequestBody AddOptionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addOption(id, req));
    }

    @GetMapping("/{id}/options")
    public List<OptionResponse> listOptions(@PathVariable Long id) {
        return service.listOptions(id);
    }

    @PostMapping("/{id}/open")
    public ElectionResponse open(@PathVariable Long id) { return service.open(id); }

    @PostMapping("/{id}/close")
    public ElectionResponse close(@PathVariable Long id) { return service.close(id); }
}
