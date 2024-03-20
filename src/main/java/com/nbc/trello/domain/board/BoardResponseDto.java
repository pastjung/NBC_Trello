package com.nbc.trello.domain.board;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nbc.trello.domain.todo.TodoResponseDto;
import jakarta.persistence.Column;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class BoardResponseDto {

  private Long board_id;

  private Long user_id;

  private String name;

  private String color;

  private String description;

  private List<TodoResponseDto> todoDtos;

  public BoardResponseDto(Board board) {
    this.board_id = board.getId();
    this.name = board.getName();
    this.color = board.getColor();
    this.description = board.getDescription();
  }

  public BoardResponseDto(Long board_id, Long user_id) {
    this.board_id = board_id;
    this.user_id = user_id;
  }

  public BoardResponseDto(String name, List<TodoResponseDto> todoDtos) {
    this.name = name;
    this.todoDtos = todoDtos;
  }
}