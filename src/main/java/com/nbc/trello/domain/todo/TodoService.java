package com.nbc.trello.domain.todo;

import com.nbc.trello.domain.board.Board;
import com.nbc.trello.domain.board.BoardRepository;
import com.nbc.trello.domain.card.Card;
import com.nbc.trello.domain.card.CardRepository;
import com.nbc.trello.domain.card.CardResponseDto;
import com.nbc.trello.domain.participants.ParticipantsRepository;
import com.nbc.trello.domain.user.User;
import com.nbc.trello.domain.user.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
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
    private final BoardRepository boardRepository;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final ParticipantsRepository participantsRepository;

    public TodoResponseDto createTodo(Long boardId, TodoRequestDto requestDto, User user) {
        // 유저 확인
        user = findUserBy(user.getEmail());
        // 참여자 확인
        validateParticipants(boardId, user.getId());
        // 보드 확인
        Board board = findBoard(boardId);

        List<Todo> todoList = todoRepository.findAll(Sort.by(Direction.DESC, "sequence"));

        Todo todo = null;

        if (todoList.isEmpty()) {
            todo = Todo.builder()
                .board(board)
                .user(user)
                .title(requestDto.getTitle())
                .sequence(1D)
                .build();
        } else {
            todo = Todo.builder()
                .board(board)
                .user(user)
                .title(requestDto.getTitle())
                .sequence(todoList.get(0).getSequence() + 1D)
                .build();
        }

        return new TodoResponseDto(todoRepository.save(todo));
    }

    public List<TodoResponseDto> getTodos(Long boardId, User user) {
        // 유저 확인
        user = findUserBy(user.getEmail());
        // 보드 확인
        Board board = findBoard(boardId);
        // 참여자 확인
        validateParticipants(boardId, user.getId());
        List<Todo> todoList = todoRepository.findAll(Sort.by(Direction.ASC, "sequence"));
        if (todoList.isEmpty()) {
            throw new IllegalArgumentException("투두가 존재하지 않습니다.");
        }

        List<TodoResponseDto> result = new ArrayList<>();
        for (Todo todo : todoList) {
            List<Card> cardList = cardRepository.findByTodoId(todo.getId());
            List<CardResponseDto> cardResponseDtoList = cardList.stream()
                .map(CardResponseDto::new)
                .toList();

            result.add(new TodoResponseDto(todo.getTitle(), cardResponseDtoList));
        }

        return result;
    }

    @Transactional
    public void updateTodo(Long boardId, Long todoId, TodoRequestDto requestDto, User user) {
        // 유저 확인
        user = findUserBy(user.getEmail());
        // 참여자 확인
        validateParticipants(boardId, user.getId());
        // 보드 확인
        Board board = findBoard(boardId);
        // 보드에 투두 들어있나 확인
        validateTodoExistInBoard(board.getId(), todoId);
        // 컬럼 확인
        Todo todo = findTodo(todoId);

        todo.update(requestDto);
    }

    @Transactional
    public void deleteTodo(Long boardId, Long todoId, User user) {
        // 유저 확인
        user = findUserBy(user.getEmail());
        // 참여자 확인
        validateParticipants(boardId, user.getId());
        // 보드 확인
        Board board = findBoard(boardId);
        // 보드에 투두 들어있나 확인
        validateTodoExistInBoard(board.getId(), todoId);
        // 컬럼 확인
        Todo todo = findTodo(todoId);

        todoRepository.delete(todo);
    }

    @Transactional
    public void changeSequenceTodo(Long boardId, Long todoId, TodoSequenceRequestDto requestDto,
        User user) {
        // 유저 확인
        user = findUserBy(user.getEmail());
        // 참여자 확인
        validateParticipants(boardId, user.getId());
        // 보드 확인
        Board board = findBoard(boardId);
        // 보드에 투두 들어있나 확인
        validateTodoExistInBoard(boardId, todoId);
        // 컬럼 확인
        Todo todo = findTodo(todoId);

        List<Todo> todoList = todoRepository.findAll(Sort.by(Direction.ASC, "sequence"));

        Long todoListIndex = (long) requestDto.getSequence();

        if (todoListIndex < 0 || todoListIndex > todoList.size()) {
            throw new IllegalArgumentException("해당 순서로 바꿀 수 없습니다.");
        } else if (todoListIndex == todoList.size()) {
            todo.updateLastSequence(requestDto.getSequence() + 0D);
        } else {
            double sequence = todoList.get(requestDto.getSequence()).getSequence();
            double preSequence = todoList.get(requestDto.getSequence() - 1).getSequence();
            if (todoListIndex == 1) {
                todo.updateSequence(preSequence, 0D);
            } else
                todo.updateSequence(sequence, preSequence);
            }
        }

    private User findUserBy(String email) {
        return userRepository.findByEmail(email).orElseThrow(
            () -> new EntityNotFoundException("회원이 존재하지 않습니다."));
    }

    private Board findBoard(Long boardId) {
        return boardRepository.findById(boardId)
            .orElseThrow(() -> new EntityNotFoundException("해당 보드가 존재하지 않습니다."));
    }

    private Todo findTodo(Long todoId) {
        return todoRepository.findById(todoId)
            .orElseThrow(() -> new EntityExistsException("해당 투두가 존재하지 않습니다."));
    }

    private void validateTodoExistInBoard(Long boardId, Long todoId) {
        if (!todoRepository.existsByIdAndBoardId(todoId, boardId)) {
            throw new EntityExistsException("Board 에 Todo 가 존재하지 않습니다.");
        }
    }

    private void validateParticipants(Long boardId, Long userId) {
        if (!participantsRepository.existsByBoardIdAndUserId(boardId, userId)) {
            throw new EntityExistsException("참여자가 아닙니다.");
        }
    }
}
