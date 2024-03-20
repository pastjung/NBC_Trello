package com.nbc.trello.domain.card;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByTodoId(Long id);

    boolean existsByIdAndTodoId(Long cardId, Long todoId);
}
