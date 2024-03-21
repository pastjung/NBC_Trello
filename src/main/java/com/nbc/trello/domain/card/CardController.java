package com.nbc.trello.domain.card;

import com.nbc.trello.global.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Controller
@RequiredArgsConstructor
@RequestMapping("/boards/{boardId}/todos/{todoId}/cards")
public class CardController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CommonResponse<CardResponseDto>> createCard(
        @PathVariable Long boardId,
        @PathVariable Long todoId, @RequestBody CardRequestDto cardRequestDto) {

        CardResponseDto cardResponseDto = cardService.CardCreateService(boardId, todoId,
            cardRequestDto);

        return ResponseEntity.ok(CommonResponse.<CardResponseDto>builder()
            .msg("카드 생성에 성공하였습니다.")
            .statusCode(HttpStatus.OK.value())
            .data(cardResponseDto)
            .build());
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CommonResponse<CardCommentResponseDto>> getCard(
        @PathVariable Long boardId,
        @PathVariable Long todoId,
        @PathVariable Long cardId) {

        CardCommentResponseDto cardCommentResponseDto = cardService.CardGetService(boardId,
            todoId, cardId);

        return ResponseEntity.ok(CommonResponse.<CardCommentResponseDto>builder()
            .msg("카드 조회에 성공하였습니다.")
            .statusCode(HttpStatus.OK.value())
            .data(cardCommentResponseDto)
            .build());
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<CommonResponse<Void>> deleteCard(
        @PathVariable Long boardId,
        @PathVariable Long todoId,
        @PathVariable Long cardId) {

        cardService.CardDeleteService(boardId, todoId, cardId);

        return ResponseEntity.ok(CommonResponse.<Void>builder()
            .msg("카드 삭제에 성공하였습니다.")
            .statusCode(HttpStatus.OK.value())
            .build());
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<CommonResponse<CardResponseDto>> updateCard(
        @PathVariable Long boardId,
        @PathVariable Long todoId,
        @PathVariable Long cardId, @RequestBody CardRequestDto cardRequestDto) {

        CardResponseDto cardResponseDto = cardService.CardUpdateService(boardId, todoId, cardId,
            cardRequestDto);

        return ResponseEntity.ok(CommonResponse.<CardResponseDto>builder()
            .msg("카드 수정에 성공하였습니다.")
            .statusCode(HttpStatus.OK.value())
            .data(cardResponseDto)
            .build());
    }
}
