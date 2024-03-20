package com.nbc.trello.domain.todo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    boolean existsByIdAndBoardId(Long todoId,Long boardId);
}
