package com.nbc.trello.domain.card;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@NoArgsConstructor
public class CardRequestDto {
    private Long boardId;
    private Long columnId;
    private Long cardId;
    private String name;
    private String description;
    private String background;
    private String pic;
    private LocalDateTime deadline;

    public CardRequestDto(String name, String description, String background, String pic, LocalDateTime deadline){
        this.name = name;
        this.description = description;
        this.background = background;
        this.pic = pic;
        this.deadline = deadline;
    }

    public CardRequestDto(Card card){
        this.name = card.getName();
        this.description = card.getDescription();
        this.background = card.getColor();
        this.pic = card.getPic();
        this.deadline = card.getDeadline();
    }
}
