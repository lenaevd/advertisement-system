package com.lenaevd.advertisements.mapper;

import com.lenaevd.advertisements.dto.comment.CommentDto;
import com.lenaevd.advertisements.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "advertisement.id", target = "advertisementId")
    @Mapping(source = "author.id", target = "authorId")
    CommentDto commentToCommentDto(Comment comment);

    List<CommentDto> commentsToCommentDtos(List<Comment> comments);
}
