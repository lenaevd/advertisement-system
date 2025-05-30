package com.lenaevd.advertisements.controller;

import com.lenaevd.advertisements.dto.comment.ChangeCommentRequest;
import com.lenaevd.advertisements.dto.comment.CommentDto;
import com.lenaevd.advertisements.dto.comment.LeaveCommentRequest;
import com.lenaevd.advertisements.mapper.CommentMapper;
import com.lenaevd.advertisements.service.CommentService;
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
@RequestMapping("/comments")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "endpoints working with comments")
public class CommentController {
    private final CommentService commentService;
    private final CommentMapper mapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @Operation(summary = "Leave comment", description = "Leaving comments is available only under active ads")
    public ResponseEntity<Void> leaveComment(@RequestBody @Validated LeaveCommentRequest request,
                                             Principal principal) {
        commentService.createComment(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @Operation(summary = "Change comment", description = "Changing comments is available only under active ads")
    public ResponseEntity<Void> changeComment(@RequestBody @Validated ChangeCommentRequest request,
                                              Principal principal) {
        commentService.changeComment(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @Operation(summary = "Get comments under ad", description = "Returns all comments under active advertisement")
    public ResponseEntity<List<CommentDto>> getCommentsByAdvertisementId(@RequestParam @NotNull Integer adId) {
        return ResponseEntity.ok(mapper.commentsToCommentDtos(commentService.getByAdvertisementId(adId)));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all comments", description = "Returns all comments, allowed only for admin")
    public ResponseEntity<List<CommentDto>> getAll() {
        return ResponseEntity.ok(mapper.commentsToCommentDtos(commentService.getAll()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get comment",
            description = "Returns a comment no matter whether ad active or not, allowed only for admin")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable int id) {
        return ResponseEntity.ok(mapper.commentToCommentDto(commentService.getById(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete comment", description = "Allowed only for admin")
    public ResponseEntity<Void> deleteById(@PathVariable int id) {
        commentService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
