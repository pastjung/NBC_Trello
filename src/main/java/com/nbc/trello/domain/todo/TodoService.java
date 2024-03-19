package com.nbc.trello.domain.todo;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoResponseDto createTodo(TodoRequestDto requestDto) {
        Todo todo = Todo.builder()
            .title(requestDto.getTitle())
            .build();

        return new TodoResponseDto(todoRepository.save(todo));
    }

    public List<TodoResponseDto> getTodos() {
        List<Todo> todoList = todoRepository.findAll(Sort.by(Direction.DESC, "createdAt"));

        return todoList.stream()
            .map(TodoResponseDto::new)
            .toList();
    }

    @Transactional
    public void updateTodo(Long todoId, TodoRequestDto requestDto) {
        Todo todo = findTodo(todoId);

        todo.update(requestDto);
    }

    @Transactional
    public void deleteTodo(Long todoId) {
        Todo todo = findTodo(todoId);

        todoRepository.delete(todo);
    }

    private Todo findTodo(Long todoId) {
        return todoRepository.findById(todoId)
            .orElseThrow(() -> new IllegalArgumentException("해당 컬럼이 존재하지 않습니다."));
    }
}
