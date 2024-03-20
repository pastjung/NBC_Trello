package com.nbc.trello.domain.card;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class getCommentResponseDto {

    private Long commentId;
    private String comment;

}

