package com.lenaevd.advertisements.service;

import com.lenaevd.advertisements.dto.comment.ChangeCommentRequest;
import com.lenaevd.advertisements.dto.comment.LeaveCommentRequest;
import com.lenaevd.advertisements.model.Comment;

import java.security.Principal;
import java.util.List;

public interface CommentService {
    void createComment(LeaveCommentRequest request, Principal principal);

    void changeComment(ChangeCommentRequest request, Principal principal);

    List<Comment> getByAdvertisementId(int adId);

    List<Comment> getAll();

    Comment getById(int id);

    void deleteById(int id);
}
