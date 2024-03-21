package com.nbc.trello.domain.card;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetCommentResponseDto {

    private Long commentId;
    private String comment;

}


