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
import org.springframework.web.bind.annotation.RestController;

@RestController
@Controller
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    @PostMapping("/boards/{board_id}/columns/{column_id}/cards")
    public ResponseEntity<CommonResponse<CardResponseDto>> createCard(
        @PathVariable("board_id") Long boardId,
        @PathVariable("column_id") Long columnId, @RequestBody CardRequestDto cardRequestDto){

        CardResponseDto cardResponseDto = cardService.CardCreateService(boardId, columnId, cardRequestDto);

        return ResponseEntity.ok(CommonResponse.<CardResponseDto>builder()
            .msg("카드 생성에 성공하였습니다.")
            .statusCode(HttpStatus.OK.value())
            .data(cardResponseDto)
            .build());
    }

    @GetMapping("/boards/{board_id}/columns/{column_id}/cards/{card_id}")
    public ResponseEntity<CommonResponse<CardCommentResponseDto>> getCard(
        @PathVariable("board_id") Long boardId,
        @PathVariable("column_id") Long columnId,
        @PathVariable("card_id") Long card_id){

        CardCommentResponseDto cardCommentResponseDto = cardService.CardGetService(boardId,
            columnId, card_id);

        return ResponseEntity.ok(CommonResponse.<CardCommentResponseDto>builder()
            .msg("카드 조회에 성공하였습니다.")
            .statusCode(HttpStatus.OK.value())
            .data(cardCommentResponseDto)
            .build());
    }

    @DeleteMapping("/boards/{board_id}/columns/{column_id}/cards/{card_id}")
    public ResponseEntity<CommonResponse<CardResponseDto>> deleteCard(
        @PathVariable("board_id") Long boardId,
        @PathVariable("column_id") Long columnId,
        @PathVariable("card_id") Long card_id){

        CardResponseDto cardResponseDto = cardService.CardDeleteService(boardId, columnId, card_id);

        return ResponseEntity.ok(CommonResponse.<CardResponseDto>builder()
            .msg("카드 삭제에 성공하였습니다.")
            .statusCode(HttpStatus.OK.value())
            .data(cardResponseDto)
            .build());
    }

    @PutMapping("/boards/{board_id}/columns/{column_id}/cards/{card_id}")
    public ResponseEntity<CommonResponse<CardResponseDto>> updateCard(
        @PathVariable("board_id") Long boardId,
        @PathVariable("column_id") Long columnId,
        @PathVariable("card_id") Long card_id, @RequestBody CardRequestDto cardRequestDto){

        CardResponseDto cardResponseDto = cardService.CardUpdateService(boardId, columnId, card_id,cardRequestDto);

        return ResponseEntity.ok(CommonResponse.<CardResponseDto>builder()
            .msg("카드 수정에 성공하였습니다.")
            .statusCode(HttpStatus.OK.value())
            .data(cardResponseDto)
            .build());
    }
}
