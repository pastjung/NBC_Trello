package com.nbc.trello.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentRequest {

    @NotBlank(message = "내용을 필수로 입력해야 합니다.")
    private String content;

    public CommentRequest(String content) {
        this.content = content;
    }
}
