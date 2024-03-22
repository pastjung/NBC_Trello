package com.nbc.trello.domain.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentResponseDto {

    private String username;
    private String content;

    public CommentResponseDto(Comment comment) {
        this.username = comment.getUser().getUsername();
        this.content = comment.getContent();
    }

    public CommentResponseDto(String username, String content) {
        this.username = username;
        this.content = content;
    }


}
