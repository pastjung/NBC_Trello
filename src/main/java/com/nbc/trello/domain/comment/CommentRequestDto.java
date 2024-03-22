package com.nbc.trello.domain.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentRequestDto {

    @NotBlank(message = "내용을 필수로 입력해야 합니다.")
    private String content;

    public CommentRequestDto(String content) {
        this.content = content;
    }
}
