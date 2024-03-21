package com.nbc.trello.domain.todo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nbc.trello.domain.card.CardResponseDto;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
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
