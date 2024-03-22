package com.nbc.trello.domain.comment;

import com.nbc.trello.domain.card.Card;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DialectOverride.Version;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Table(name = "comments")
@NoArgsConstructor
public class Comment extends TimeStamped {

    @Version(major = 0)
    private int version;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Card card;

    public Comment(String content, User user, Card card) {
        this.content = content;
        this.user = user;
        this.card = card;
    }

    public void update(CommentRequestDto request) {
        this.content = request.getContent();
    }

    private void updateVersion(int version) {
        this.version = version;
    }
}