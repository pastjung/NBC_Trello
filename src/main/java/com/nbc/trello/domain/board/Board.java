package com.nbc.trello.domain.board;

import com.nbc.trello.domain.timeStamped.TimeStamped;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DialectOverride.Version;

@Entity
@Table(name = "boards")
@Getter
@Setter
@NoArgsConstructor
public class Board extends TimeStamped {

    @Version(major = 0)
    private int version;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String description;

    public Board(BoardRequestDto requestDto) {
        this.name = requestDto.getName();
        this.color = requestDto.getColor();
        this.description = requestDto.getDescription();
    }

    public BoardResponseDto toDto() {
        BoardResponseDto responseDto = new BoardResponseDto();
        responseDto.setBoard_id(this.Id);
        return responseDto;
    }

    private void updateVersion(int version){
        this.version = version;
    }
}