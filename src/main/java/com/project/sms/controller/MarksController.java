package com.project.sms.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.sms.dto.MarksRequest;
import com.project.sms.dto.MarksResponse;
import com.project.sms.service.MarksService;

@RestController
@RequestMapping("/api/teachers/marks")
public class MarksController {

    private final MarksService marksService;

    public MarksController(MarksService marksService) {
        this.marksService = marksService;
    }

    @PostMapping
    public ResponseEntity<MarksResponse> add(Authentication auth, @Valid @RequestBody MarksRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(marksService.addMarks(auth.getName(), request));
    }

    @PutMapping("/{id}")
    public MarksResponse update(Authentication auth, @PathVariable Long id, @Valid @RequestBody MarksRequest request) {
        return marksService.updateMarks(auth.getName(), id, request);
    }

    @GetMapping
    public List<MarksResponse> list(Authentication auth,
                                    @RequestParam(required = false) Long studentId,
                                    @RequestParam(required = false) Long subjectId) {
        return marksService.getMarksForTeacher(auth.getName(), studentId, subjectId);
    }
}
