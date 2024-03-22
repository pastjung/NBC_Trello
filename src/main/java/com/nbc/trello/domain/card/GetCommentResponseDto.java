package com.nbc.trello.domain.card;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class GetCommentResponseDto {

    private Long commentId;
    private String comment;

}


