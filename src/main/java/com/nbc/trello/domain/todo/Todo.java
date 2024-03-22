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
import org.hibernate.annotations.DialectOverride.Version;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "todos")
public class Todo extends TimeStamped {

    @Version(major = 0)
    private int version;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Double sequence;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Builder
    public Todo(Board board, User user, String title, Double sequence) {
        this.board = board;
        this.user = user;
        this.title = title;
        this.sequence = sequence;
    }

    public void update(TodoRequestDto requestDto) {
        this.title = requestDto.getTitle();
    }

    public void updateSequence(Double sequence, Double preSequence) {
        this.sequence = (sequence + preSequence) / 2;
    }

    public void updateLastSequence(Double sequence) {
        this.sequence = sequence + 1;
    }

    private void updateVersion(int version){
        this.version = version;
    }
}
