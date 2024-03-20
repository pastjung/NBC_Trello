package com.nbc.trello.domain.todo;

import com.nbc.trello.domain.card.CardResponseDto;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TodoResponseDto {

    private String title;

    private List<CardResponseDto> cardDtos;

    public TodoResponseDto(Todo todo) {
        this.title = todo.getTitle();
    }

    public TodoResponseDto(String title, List<CardResponseDto> cardDtos) {
        this.title = title;
        this.cardDtos = cardDtos;
    }
}
