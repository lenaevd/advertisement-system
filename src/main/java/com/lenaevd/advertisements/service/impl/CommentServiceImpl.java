package com.lenaevd.advertisements.service.impl;

import com.lenaevd.advertisements.dao.impl.CommentDaoImpl;
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
import com.lenaevd.advertisements.service.AdvertisementService;
import com.lenaevd.advertisements.service.CommentService;
import com.lenaevd.advertisements.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private static final String COMMENT_CHANGE_RESTRICTED = "User isn't the author of the comment";
    private static final String UNAVAILABLE_ACTION = "Action available only under active ads";
    private final CommentDaoImpl commentDao;
    private final UserService userService;
    private final AdvertisementService adService;

    @Override
    @Transactional
    public void createComment(LeaveCommentRequest request, Principal principal) {
        User author = userService.getUserFromPrincipal(principal);
        Advertisement ad = adService.getAdvertisementById(request.adId());
        if (ad.getStatus() == AdvertisementStatus.ACTIVE) {
            Comment comment = new Comment(ad, author, request.content());
            commentDao.save(comment);
        } else {
            throw new ActionIsImpossibleException(UNAVAILABLE_ACTION);
        }
    }

    @Override
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

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getByAdvertisementId(int adId) {
        Advertisement ad = adService.getAdvertisementById(adId);
        if (ad.getStatus() == AdvertisementStatus.ACTIVE) {
            List<Comment> comments = commentDao.findByAdvertisementId(adId);
            comments.sort(Comparator.comparing(Comment::getCreatedAt));
            return comments;
        } else {
            throw new ActionIsImpossibleException(UNAVAILABLE_ACTION);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getAll() {
        return commentDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Comment getById(int id) {
        return commentDao.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, EntityName.COMMENT));
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        Comment comment = getById(id);
        commentDao.delete(comment);
    }
}
