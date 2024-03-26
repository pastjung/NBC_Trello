package com.nbc.trello.domain.card;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class CardResponseDto {

    private Long boardId;
    private Long columnId;
    private Long cardId;
    private String name;

    public CardResponseDto(Long boardId, Long columnId, Long cardId) {
        this.boardId = boardId;
        this.columnId = columnId;
        this.cardId = cardId;
    }

    public CardResponseDto(Card card) {
        this.columnId = card.getTodo().getId();
        this.cardId = card.getId();
        this.name = card.getName();

    }
}