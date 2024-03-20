package com.nbc.trello.domain.comment;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByCardId(Long cardId);

    boolean existsByIdAndCardId(Long cardId, Long commentId);
}
