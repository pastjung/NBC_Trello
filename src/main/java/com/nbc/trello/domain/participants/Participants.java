package com.nbc.trello.domain.participants;

import com.nbc.trello.domain.board.Board;
import com.nbc.trello.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Participants {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private Long userId;

  @Column
  private Long boardId;

  @Column
  private Boolean generator = false;

  public Participants(Long userId, Long boardId) {
    this.userId = userId;
    this.boardId = boardId;
  }
}