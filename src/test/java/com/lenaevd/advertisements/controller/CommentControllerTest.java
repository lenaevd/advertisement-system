package com.lenaevd.advertisements.controller;

import com.lenaevd.advertisements.dto.comment.ChangeCommentRequest;
import com.lenaevd.advertisements.dto.comment.CommentDto;
import com.lenaevd.advertisements.dto.comment.LeaveCommentRequest;
import com.lenaevd.advertisements.mapper.CommentMapper;
import com.lenaevd.advertisements.model.Advertisement;
import com.lenaevd.advertisements.model.Comment;
import com.lenaevd.advertisements.model.User;
import com.lenaevd.advertisements.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {
    @Mock
    private CommentService commentService;
    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentController commentController;

    private Principal principal;
    private Comment comment;
    private CommentDto commentDto;
    private Advertisement ad;
    private int adId = 15;
    private int id = 2;

    @BeforeEach
    void setUp() {
        principal = mock(Principal.class);
        ad = new Advertisement();
        ad.setId(adId);
        comment = new Comment(ad, new User(), "comment");
        comment.setId(id);
        commentDto = new CommentDto(id, adId, 1, "comment",
                LocalDateTime.of(2025, 1, 1, 15, 0));
    }

    @Test
    void leaveComment() {
        //GIVEN
        LeaveCommentRequest request = new LeaveCommentRequest(1, "angry comment");

        //WHEN
        ResponseEntity<Void> response = commentController.leaveComment(request, principal);

        //THEN
        verify(commentService).createComment(request, principal);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void changeComment() {
        //GIVEN
        ChangeCommentRequest request = new ChangeCommentRequest(1, "changed comment");
        Principal principal = mock(Principal.class);

        //WHEN
        ResponseEntity<Void> response = commentController.changeComment(request, principal);

        //THEN
        verify(commentService).changeComment(request, principal);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void getCommentsByAdvertisementId() {
        //GIVEN
        List<Comment> comments = List.of(comment);
        List<CommentDto> commentDtos = List.of(commentDto);

        when(commentService.getByAdvertisementId(adId)).thenReturn(comments);
        when(commentMapper.commentsToCommentDtos(comments)).thenReturn(commentDtos);

        //WHEN
        ResponseEntity<List<CommentDto>> response = commentController.getCommentsByAdvertisementId(adId);

        //THEN
        verify(commentService).getByAdvertisementId(adId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(commentDtos, response.getBody());
    }

    @Test
    void getAll() {
        //GIVEN
        List<Comment> comments = List.of(comment);
        List<CommentDto> commentDtos = List.of(commentDto);

        when(commentService.getAll()).thenReturn(comments);
        when(commentMapper.commentsToCommentDtos(comments)).thenReturn(commentDtos);

        //WHEN
        ResponseEntity<List<CommentDto>> response = commentController.getAll();

        //THEN
        verify(commentService).getAll();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(commentDtos, response.getBody());
    }

    @Test
    void getCommentById() {
        //GIVEN
        when(commentService.getById(id)).thenReturn(comment);
        when(commentMapper.commentToCommentDto(comment)).thenReturn(commentDto);

        //WHEN
        ResponseEntity<CommentDto> response = commentController.getCommentById(id);

        //THEN
        verify(commentService).getById(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(commentDto, response.getBody());
    }

    @Test
    void deleteById() {
        //WHEN
        ResponseEntity<Void> response = commentController.deleteById(id);

        //THEN
        verify(commentService).deleteById(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
