package com.nbc.trello.domain.todo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findByBoardId(Long id);

    boolean existsByIdAndBoardId(Long todoId, Long boardId);
}
