package com.lenaevd.advertisements.controller;

import com.lenaevd.advertisements.dto.grade.ChangeGradeRequest;
import com.lenaevd.advertisements.dto.grade.GradeDto;
import com.lenaevd.advertisements.dto.grade.LeaveGradeRequest;
import com.lenaevd.advertisements.dto.grade.RatingResponse;
import com.lenaevd.advertisements.mapper.GradeMapper;
import com.lenaevd.advertisements.model.Grade;
import com.lenaevd.advertisements.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@Validated
@RestController
@RequestMapping("/grades")
@RequiredArgsConstructor
@Tag(name = "Grades", description = "endpoints working with grades, rating of user")
public class GradeController {

    private final GradeService gradeService;
    private final GradeMapper gradeMapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @Operation(summary = "Leave grade",
            description = "Leaving grade is available only if user has any purchase")
    public ResponseEntity<Void> leaveGrade(Principal principal, @RequestBody @Validated LeaveGradeRequest request) {
        gradeService.createGrade(principal, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @Operation(summary = "Get user's grades")
    public ResponseEntity<List<GradeDto>> getGradesByUserId(@RequestParam @NotNull Integer userId) {
        List<Grade> grades = gradeService.getUsersGrades(userId);
        return ResponseEntity.ok(gradeMapper.gradesToGradeDtos(grades));
    }

    @GetMapping("/rating")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @Operation(summary = "Get user's rating")
    public ResponseEntity<RatingResponse> getRatingByUserId(@RequestParam @NotNull Integer userId) {
        float rating = gradeService.getUsersRating(userId);
        return ResponseEntity.ok(new RatingResponse(userId, rating));
    }

    @PatchMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @Operation(summary = "Change grade")
    public ResponseEntity<Void> changeGrade(Principal principal, @RequestBody @Validated ChangeGradeRequest request) {
        gradeService.changeGrade(principal, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all grades", description = "Returns all grades, allowed only for admin")
    public ResponseEntity<List<GradeDto>> getGrades() {
        List<Grade> grades = gradeService.getAllGrades();
        return ResponseEntity.ok(gradeMapper.gradesToGradeDtos(grades));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete grade", description = "Allowed only for admin")
    public ResponseEntity<Void> deleteGrade(@PathVariable int id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
