package com.nbc.trello.domain.todo;

import com.nbc.trello.global.response.CommonResponse;
import com.nbc.trello.global.util.UserDetailsImpl;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/boards/{boardId}/todos")
    ResponseEntity<CommonResponse<TodoResponseDto>> createTodo(
        @PathVariable Long boardId,
        @RequestBody @Valid TodoRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        TodoResponseDto responseDto = todoService.createTodo(boardId, requestDto,
            userDetails.getUser());

        return ResponseEntity.status(HttpStatus.CREATED.value()).body(
            CommonResponse.<TodoResponseDto>builder()
                .msg("투두 생성 완료")
                .statusCode(HttpStatus.CREATED.value())
                .data(responseDto)
                .build()
        );
    }

    @GetMapping("/boards/{boardId}/todos")
    ResponseEntity<CommonResponse<List<TodoResponseDto>>> getTodos(
        @PathVariable Long boardId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<TodoResponseDto> responseDtoList = todoService.getTodos(boardId,
            userDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK.value()).body(
            CommonResponse.<List<TodoResponseDto>>builder()
                .msg("투두 조회 완료")
                .statusCode(HttpStatus.OK.value())
                .data(responseDtoList)
                .build()
        );
    }

    @PutMapping("/boards/{boardId}/todos/{todoId}")
    ResponseEntity<CommonResponse<Void>> updateTodo(
        @PathVariable Long boardId,
        @PathVariable Long todoId,
        @RequestBody @Valid TodoRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        todoService.updateTodo(boardId, todoId, requestDto, userDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK.value()).body(
            CommonResponse.<Void>builder()
                .msg("투두 수정 성공")
                .statusCode(HttpStatus.OK.value())
                .build()
        );
    }

    @DeleteMapping("/boards/{boardId}/todos/{todoId}")
    ResponseEntity<CommonResponse<Void>> deleteTodo(
        @PathVariable Long boardId,
        @PathVariable Long todoId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        todoService.deleteTodo(boardId, todoId, userDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK.value()).body(
            CommonResponse.<Void>builder()
                .msg("투두 삭제 성공")
                .statusCode(HttpStatus.OK.value())
                .build()
        );
    }

    @PatchMapping("/boards/{boardId}/todos/{todoId}")
    ResponseEntity<CommonResponse<Void>> changeSequenceTodo(
        @PathVariable Long boardId,
        @PathVariable Long todoId,
        @RequestBody TodoSequenceRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        todoService.changeSequenceTodo(boardId, todoId, requestDto, userDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK.value()).body(
            CommonResponse.<Void>builder()
                .msg("투두 순서 이동 성공")
                .statusCode(HttpStatus.OK.value())
                .build()
        );
    }
}

