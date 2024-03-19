package com.nbc.trello.domain.board;

import com.nbc.trello.domain.timeStamped.TimeStamped;
import com.nbc.trello.domain.todo.Todo;
import com.nbc.trello.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "boards")
@Getter
@Setter
@NoArgsConstructor
public class Board extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "board")
    private List<Todo> todoList = new ArrayList<>();

/*
    public Board(BoardRequestDto requestDto) {
        this.name = requestDto.getName();
        this.color = requestDto.getColor();
        this.description = requestDto.getDescription();
    }
 */
}