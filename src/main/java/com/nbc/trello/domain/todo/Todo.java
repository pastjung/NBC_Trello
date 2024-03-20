package com.nbc.trello.domain.todo;

import com.nbc.trello.domain.board.Board;
import com.nbc.trello.domain.timeStamped.TimeStamped;
import com.nbc.trello.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "todos")
public class Todo extends TimeStamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private Long page;

  @Column(nullable = false)
  private String title;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "board_id")
  private Board board;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Builder
  public Todo(String title) {
    this.title = title;
  }

  public void update(TodoRequestDto requestDto) {
    this.title = requestDto.getTitle();
  }
}
