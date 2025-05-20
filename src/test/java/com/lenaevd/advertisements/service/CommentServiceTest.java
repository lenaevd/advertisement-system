package com.lenaevd.advertisements.service;

import com.lenaevd.advertisements.dao.CommentDao;
import com.lenaevd.advertisements.dto.comment.ChangeCommentRequest;
import com.lenaevd.advertisements.dto.comment.LeaveCommentRequest;
import com.lenaevd.advertisements.exception.ActionIsImpossibleException;
import com.lenaevd.advertisements.exception.NoRightsException;
import com.lenaevd.advertisements.exception.ObjectNotFoundException;
import com.lenaevd.advertisements.model.Advertisement;
import com.lenaevd.advertisements.model.AdvertisementStatus;
import com.lenaevd.advertisements.model.Comment;
import com.lenaevd.advertisements.model.EntityName;
import com.lenaevd.advertisements.model.User;
import com.lenaevd.advertisements.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private CommentDao commentDao;
    @Mock
    private UserService userService;
    @Mock
    private AdvertisementService adService;

    @InjectMocks
    CommentServiceImpl commentService;

    private static final String CONTENT = "typical comment";
    private static final String AUTHOR = "author";
    private Principal principal;
    private Comment comment;
    private User author;
    private Advertisement ad;
    private int id = 10;
    private int adId = 5;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setUsername(AUTHOR);

        ad = new Advertisement();
        ad.setId(5);

        comment = new Comment();
        comment.setId(adId);
        comment.setContent(CONTENT);
        comment.setAuthor(author);
        comment.setAdvertisement(ad);

        principal = mock(Principal.class);
    }

    @Test
    void createComment() {
        //GIVEN
        LeaveCommentRequest request = new LeaveCommentRequest(adId, CONTENT);
        ad.setStatus(AdvertisementStatus.ACTIVE);

        when(userService.getUserFromPrincipal(principal)).thenReturn(author);
        when(adService.getAdvertisementById(adId)).thenReturn(ad);

        //WHEN
        commentService.createComment(request, principal);

        //THEN
        verify(commentDao).save(any(Comment.class));
    }

    @Test
    void createCommentAndAdArchivedThrowsException() {
        //GIVEN
        LeaveCommentRequest request = new LeaveCommentRequest(adId, CONTENT);
        ad.setStatus(AdvertisementStatus.ARCHIVED);

        when(userService.getUserFromPrincipal(principal)).thenReturn(author);
        when(adService.getAdvertisementById(adId)).thenReturn(ad);

        //WHEN | THEN
        ActionIsImpossibleException exception = assertThrows(ActionIsImpossibleException.class,
                () -> commentService.createComment(request, principal));
        assertEquals("Action available only under active ads", exception.getMessage());
        verify(commentDao, never()).save(any(Comment.class));
    }

    @Test
    void changeComment() {
        //GIVEN
        ChangeCommentRequest request = new ChangeCommentRequest(id, "fantastic content");
        ad.setStatus(AdvertisementStatus.ACTIVE);

        when(userService.getUserFromPrincipal(any())).thenReturn(author);
        when(commentDao.findById(id)).thenReturn(Optional.of(comment));

        //WHEN
        commentService.changeComment(request, principal);

        //THEN
        assertEquals(request.content(), comment.getContent());
        verify(commentDao).update(comment);
    }

    @Test
    void changeCommentAndUserIsNotAuthorThrowsException() {
        //GIVEN
        ChangeCommentRequest request = new ChangeCommentRequest(id, "new content");
        User otherUser = new User();

        ad.setStatus(AdvertisementStatus.ACTIVE);

        when(userService.getUserFromPrincipal(principal)).thenReturn(otherUser);
        when(commentDao.findById(id)).thenReturn(Optional.of(comment));

        //WHEN | THEN
        NoRightsException exception = assertThrows(NoRightsException.class,
                () -> commentService.changeComment(request, principal));
        assertEquals("User isn't the author of the comment", exception.getMessage());
        verify(commentDao, never()).update(comment);
    }

    @Test
    void changeCommentWhenAdArchivedThrowsException() {
        //GIVEN
        ChangeCommentRequest request = new ChangeCommentRequest(id, "new content");
        ad.setStatus(AdvertisementStatus.ARCHIVED);

        when(userService.getUserFromPrincipal(any())).thenReturn(author);
        when(commentDao.findById(id)).thenReturn(Optional.of(comment));

        //WHEN | THEN
        ActionIsImpossibleException exception = assertThrows(ActionIsImpossibleException.class,
                () -> commentService.changeComment(request, principal));
        assertEquals("Action available only under active ads", exception.getMessage());
        verify(commentDao, never()).update(comment);
    }

    @Test
    void deleteById() {
        //GIVEN
        when(commentDao.findById(id)).thenReturn(Optional.of(comment));

        //WHEN
        commentService.deleteById(id);

        //THEN
        verify(commentDao).delete(comment);
    }

    @Test
    void getById() {
        //GIVEN
        when(commentDao.findById(id)).thenReturn(Optional.of(comment));

        //WHEN
        Comment resultComment = commentService.getById(id);

        //THEN
        assertEquals(comment, resultComment);
    }

    @Test
    void getByIdAndCommentNotFoundThrowException() {
        //GIVEN
        when(commentDao.findById(id)).thenReturn(Optional.empty());

        //WHEN | THEN
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> commentService.getById(id));
        assertEquals((new ObjectNotFoundException(id, EntityName.COMMENT)).getMessage(), exception.getMessage());
    }
}
