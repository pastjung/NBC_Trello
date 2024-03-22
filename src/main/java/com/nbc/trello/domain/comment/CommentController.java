package com.nbc.trello.domain.comment;

import com.nbc.trello.global.response.CommonResponse;
import com.nbc.trello.global.util.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/boards/{boardId}/todos/{todoId}/cards/{cardId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommonResponse<CommentResponseDto>> createComment(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long boardId,
        @PathVariable Long todoId,
        @PathVariable Long cardId,
        @RequestBody @Valid CommentRequestDto request) {

        return ResponseEntity.ok()
            .body(CommonResponse.<CommentResponseDto>builder()
                .statusCode(HttpStatus.OK.value())
                .data(commentService.createComment(userDetails.getUsername(),
                    boardId, todoId, cardId, request))
                .build());
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommonResponse<CommentResponseDto>> updateComment(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long boardId,
        @PathVariable Long todoId,
        @PathVariable Long cardId,
        @PathVariable Long commentId,
        @RequestBody @Valid CommentRequestDto request) {

        return ResponseEntity.ok()
            .body(CommonResponse.<CommentResponseDto>builder()
                .statusCode(HttpStatus.OK.value())
                .data(commentService.updateComment(userDetails.getUsername(),
                    boardId, todoId, cardId, commentId, request))
                .build());
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommonResponse<String>> deleteComment(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long boardId,
        @PathVariable Long todoId,
        @PathVariable Long cardId,
        @PathVariable Long commentId) {

        return ResponseEntity.ok()
            .body(CommonResponse.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .data(commentService.deleteComment(userDetails.getUsername(),
                    boardId, todoId, cardId, commentId))
                .build());
    }
}

