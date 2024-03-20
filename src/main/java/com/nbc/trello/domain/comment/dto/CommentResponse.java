package com.nbc.trello.domain.comment.dto;

import com.nbc.trello.domain.comment.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentResponse {

    private String username;
    private String content;

    public CommentResponse(Comment comment) {
        this.username = comment.getUser().getUsername();
        this.content = comment.getContent();
    }

    public CommentResponse(String username, String content) {
        this.username = username;
        this.content = content;
    }


}
