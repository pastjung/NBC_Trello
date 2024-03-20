package com.nbc.trello.domain.card;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    boolean existsByIdAndTodoId(Long cardId, Long todoId);
}
