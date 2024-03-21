package com.nbc.trello.domain.card;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CardCommentResponseDto {

    private Long cardId;
    private String name;
    private String description;
    //private List<GetCommentResponseDto> getCommentResponseDtoList = new ArrayList<>();
    private List<GetCommentResponseDto> getCommentResponseDtoList;

    public CardCommentResponseDto(Card card) {
        this.cardId = card.getId();
        this.name = card.getName();
        this.description = card.getDescription();
    }

}