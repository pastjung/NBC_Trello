package com.nbc.trello.domain.card;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Builder
@AllArgsConstructor
public class CardCommentResponseDto {

    private Long cardId;
    private String name;
    private String description;
    private List<getCommentResponseDto> getCommentResponseDtoList = new ArrayList<>();

    public CardCommentResponseDto(Card card){
        this.cardId = card.getId();
        this.name = card.getName();
        this.description = card.getDescription();
    }

}
