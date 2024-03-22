package com.nbc.trello.domain.card;

import com.nbc.trello.domain.timeStamped.TimeStamped;
import com.nbc.trello.domain.todo.Todo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DialectOverride.Version;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class) //자동으로 LocalDateTime 생성
public class Card extends TimeStamped {

    @Version(major = 0)
    private int version;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String pic;

    @Column
    private String description;

    @Column
    private String color;

    @Column
    private LocalDateTime deadline;

    @Column
    private Double sequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Todo todo;

    public Card(CardRequestDto cardRequestDto) {
        this.name = cardRequestDto.getName();
        this.pic = cardRequestDto.getPic();
        this.description = cardRequestDto.getDescription();
        this.color = cardRequestDto.getBackground();
        this.deadline = cardRequestDto.getDeadline();
    }


    public void CardUpdate(CardRequestDto cardRequestDto) {
        this.name = cardRequestDto.getName();
        this.pic = cardRequestDto.getPic();
        this.description = cardRequestDto.getDescription();
        this.color = cardRequestDto.getBackground();
        this.deadline = cardRequestDto.getDeadline();
    }

    private void updateVersion(int version){
        this.version = version;
    }

    public void updateSequence(Double sequence, Double preSequence) {
        this.sequence = (sequence + preSequence) / 2;
    }

    public void updateLastSequence(Double sequence) {
        this.sequence = sequence + 1;
    }


}
