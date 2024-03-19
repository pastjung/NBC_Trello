package com.nbc.trello.domain.todo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TodoResponseDto {

    private String title;

    public TodoResponseDto(Todo todo) {
        this.title = todo.getTitle();
    }
}
