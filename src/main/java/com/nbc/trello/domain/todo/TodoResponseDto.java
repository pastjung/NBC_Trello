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
    private Integer count;

    private List<CardResponseDto> cardDtos;

    public TodoResponseDto(Todo todo) {
        this.title = todo.getTitle();
        this.count = todo.getCount();
    }

    public TodoResponseDto(String title, Integer count, List<CardResponseDto> cardDtos) {
        this.title = title;
        this.count = count;
        this.cardDtos = cardDtos;
    }
}
