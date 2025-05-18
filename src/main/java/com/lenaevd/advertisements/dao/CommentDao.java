package com.lenaevd.advertisements.dao;

import com.lenaevd.advertisements.model.Comment;

import java.util.List;

public interface CommentDao extends Dao<Comment> {
    List<Comment> findByAdvertisementId(int adId);
}
