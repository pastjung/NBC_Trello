package com.nbc.trello.domain.board;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardRequestDto {

    @NotBlank(message = "제목을 입력해주세요.")
    private String name;

    @NotBlank(message = "색상을 입력해주세요.")
    private String color;

    @NotBlank(message = "설명을 입력해주세요.")
    private String description;

}

