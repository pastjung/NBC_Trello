package com.nbc.trello.domain.card;

import com.nbc.trello.global.response.CommonResponse;
import com.nbc.trello.global.util.UserDetailsImpl;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards/{boardId}/todos/{todoId}/cards")
public class CardController {

    private final CardService cardService;

    @PostMapping("/test")
    public void create100(
        @PathVariable Long boardId,
        @PathVariable Long todoId,
        @RequestBody CardRequestDto cardRequestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails){
        cardService.create100(boardId,todoId,cardRequestDto, userDetails.getUser());
    }

    @PostMapping()
    public ResponseEntity<CommonResponse<CardResponseDto>> createCard(
        @PathVariable Long boardId,
        @PathVariable Long todoId,
        @RequestBody CardRequestDto cardRequestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        CardResponseDto cardResponseDto = cardService.CardCreateService(boardId, todoId,
            cardRequestDto, userDetails.getUser());

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
        @PathVariable Long cardId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        CardCommentResponseDto cardCommentResponseDto = cardService.cardGetService(boardId,
            todoId, cardId, userDetails);

        return ResponseEntity.ok(CommonResponse.<CardCommentResponseDto>builder()
            .msg("카드 조회에 성공하였습니다.")
            .statusCode(HttpStatus.OK.value())
            .data(cardCommentResponseDto)
            .build());
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<CommonResponse<CardResponseDto>> deleteCard(
        @PathVariable Long boardId,
        @PathVariable Long todoId,
        @PathVariable Long cardId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        CardResponseDto cardResponseDto = cardService.CardDeleteService(boardId, todoId, cardId,
            userDetails.getUser());

        return ResponseEntity.ok(CommonResponse.<CardResponseDto>builder()
            .msg("카드 삭제에 성공하였습니다.")
            .statusCode(HttpStatus.OK.value())
            .data(cardResponseDto)
            .build());
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<CommonResponse<CardResponseDto>> updateCard(
        @PathVariable Long boardId,
        @PathVariable Long todoId,
        @PathVariable Long cardId,
        @RequestBody CardRequestDto cardRequestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        CardResponseDto cardResponseDto = cardService.CardUpdateService(boardId, todoId, cardId,
            cardRequestDto, userDetails.getUser());

        return ResponseEntity.ok(CommonResponse.<CardResponseDto>builder()
            .msg("카드 수정에 성공하였습니다.")
            .statusCode(HttpStatus.OK.value())
            .data(cardResponseDto)
            .build());
    }

    @PostMapping("/{cardId}/users/{userId}")
    public ResponseEntity<CommonResponse<Void>> inviteUser(
        @PathVariable Long userId,
        @PathVariable Long boardId,
        @PathVariable Long todoId,
        @PathVariable Long cardId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        cardService.inviteUser(boardId, todoId, userId, cardId,
            userDetails.getUser());

        return ResponseEntity.ok()
            .body(CommonResponse.<Void>builder()
                .msg("작업자 초대에 성공하였습니다.")
                .statusCode(200)
                .build());
    }

    @PostMapping("/{cardId}/move")
    public ResponseEntity<CommonResponse<Void>> MoveCard(
        @PathVariable Long boardId,
        @PathVariable Long todoId,
        @PathVariable Long cardId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        cardService.MoveCard(boardId, todoId, cardId,
            userDetails.getUser());

        return ResponseEntity.ok()
            .body(CommonResponse.<Void>builder()
                .msg("카드가 이동했습니다.")
                .statusCode(200)
                .build());
    }

    @PatchMapping("/{cardId}")
    ResponseEntity<CommonResponse<Void>> changeSequenceCard(
        @PathVariable Long boardId,
        @PathVariable Long todoId,
        @PathVariable Long cardId,
        @RequestBody CardSequenceRequestDto cardSequenceRequestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        cardService.changeSequenceCard(boardId, todoId, cardId, cardSequenceRequestDto,
            userDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK.value()).body(
            CommonResponse.<Void>builder()
                .msg("카드 순서 이동했습니다.")
                .statusCode(HttpStatus.OK.value())
                .build()
        );
    }

}