package com.nbc.trello.domain.todo;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodoRequestDto {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

}
