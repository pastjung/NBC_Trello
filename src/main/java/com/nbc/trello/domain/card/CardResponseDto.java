package com.nbc.trello.domain.card;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CardResponseDto {
    private Long boardId;
    private Long columnId;
    private Long cardId;

    public CardResponseDto(Long boardId, Long columnId, Long cardId){
        this.boardId = boardId;
        this.columnId = columnId;
        this.cardId = cardId;
    }
}
