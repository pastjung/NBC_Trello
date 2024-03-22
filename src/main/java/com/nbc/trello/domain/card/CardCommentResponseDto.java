package com.nbc.trello.domain.card;

import java.time.LocalDateTime;
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
    private Long boardId;
    private String name;
    private String description;
    private LocalDateTime deadline;
    private List<GetCommentResponseDto> getCommentResponseDtoList = new ArrayList<>();

    public CardCommentResponseDto(Card card) {
        this.cardId = card.getId();
        this.name = card.getName();
        this.description = card.getDescription();
        this.deadline = card.getDeadline();
        this.boardId = card.getTodo().getBoard().getId();
    }

}