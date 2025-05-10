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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;

@Service
public class CommentService {
    private static final String COMMENT_CHANGE_RESTRICTED = "User isn't the author of the comment";
    public static final String UNAVAILABLE_ACTION = "Action available only under active ads";
    private final CommentDao commentDao;
    private final UserService userService;
    private final AdvertisementService adService;

    public CommentService(CommentDao commentDao, UserService userService, AdvertisementService adService) {
        this.commentDao = commentDao;
        this.userService = userService;
        this.adService = adService;
    }

    @Transactional
    public void createComment(LeaveCommentRequest request, Principal principal) {
        User author = userService.getUserFromPrincipal(principal);
        Advertisement ad = adService.getAdvertisementByIdOrElseThrow(request.adId());
        if (ad.getStatus() == AdvertisementStatus.ACTIVE) {
            Comment comment = new Comment(ad, author, request.content());
            commentDao.save(comment);
        } else {
            throw new ActionIsImpossibleException(UNAVAILABLE_ACTION);
        }
    }

    @Transactional
    public void changeComment(ChangeCommentRequest request, Principal principal) {
        Comment comment = getById(request.id());
        User user = userService.getUserFromPrincipal(principal);
        if (user.equals(comment.getAuthor())) {
            if (comment.getAdvertisement().getStatus() == AdvertisementStatus.ACTIVE) {
                comment.setContent(request.content());
                commentDao.update(comment);
            } else {
                throw new ActionIsImpossibleException(UNAVAILABLE_ACTION);
            }
        } else {
            throw new NoRightsException(COMMENT_CHANGE_RESTRICTED);
        }
    }

    public List<Comment> getByAdvertisementId(int adId) {
        Advertisement ad = adService.getAdvertisementByIdOrElseThrow(adId);
        if (ad.getStatus() == AdvertisementStatus.ACTIVE) {
            List<Comment> comments = commentDao.findByAdvertisementId(adId);
            comments.sort(Comparator.comparing(Comment::getCreatedAt));
            return comments;
        } else {
            throw new ActionIsImpossibleException(UNAVAILABLE_ACTION);
        }
    }

    public List<Comment> getAll() {
        return commentDao.findAll();
    }

    public Comment getById(int id) {
        return commentDao.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, EntityName.COMMENT));
    }

    @Transactional
    public void deleteById(int id) {
        commentDao.deleteById(id);
    }
}
