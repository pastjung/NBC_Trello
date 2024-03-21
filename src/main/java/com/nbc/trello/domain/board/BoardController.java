package com.nbc.trello.domain.board;

import com.nbc.trello.global.response.CommonResponse;
import com.nbc.trello.global.util.UserDetailsImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;

    //보드 생성
    @PostMapping
    public ResponseEntity<CommonResponse<BoardResponseDto>> createBoard(
        @RequestBody BoardRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BoardResponseDto responseDto = boardService.createBoard(requestDto, userDetails.getUser());
        return ResponseEntity.ok()
            .body(CommonResponse.<BoardResponseDto>builder()
                .msg("보드 생성에 성공하였습니다.")
                .statusCode(200)
                .data(responseDto)
                .build());
    }

    //보드 전체 조회
    @GetMapping
    public ResponseEntity<CommonResponse<List<BoardResponseDto>>> getBoardList() {
        List<BoardResponseDto> boardList = boardService.getBoardList();
        return ResponseEntity.ok()
            .body(CommonResponse.<List<BoardResponseDto>>builder()
                .msg("보드 전체 조회에 성공하였습니다.")
                .statusCode(200)
                .data(boardList)
                .build());
    }

    //보드 수정
    @PutMapping("/{boardId}")
    public ResponseEntity<CommonResponse<BoardResponseDto>> updateBoard(
        @PathVariable Long boardId, @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody BoardRequestDto requestDto) {
        BoardResponseDto responseDto = boardService.updateBoard(boardId, requestDto,
            userDetails.getUser());
        return ResponseEntity.ok()
            .body(CommonResponse.<BoardResponseDto>builder()
                .msg("보드 수정에 성공하였습니다.")
                .statusCode(200)
                .data(responseDto)
                .build());

    }

    //보드 삭제
    @DeleteMapping("/{boardId}")
    public ResponseEntity<CommonResponse<BoardResponseDto>> deleteBoard(
        @PathVariable Long boardId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BoardResponseDto responseDto = boardService.deleteBoard(boardId, userDetails.getUser());
        return ResponseEntity.ok()
            .body(CommonResponse.<BoardResponseDto>builder()
                .msg("보드 삭제에 성공하였습니다.")
                .statusCode(200)
                .data(responseDto)
                .build());
    }

    //보드 초대
    @PostMapping("/{boardId}/users/{userId}")
    public ResponseEntity<CommonResponse<BoardResponseDto>> inviteUser(
        @PathVariable Long boardId, @PathVariable Long userId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BoardResponseDto responseDto = boardService.inviteUser(boardId, userId,
            userDetails.getUser());
        return ResponseEntity.ok()
            .body(CommonResponse.<BoardResponseDto>builder()
                .msg("보드 초대에 성공하였습니다.")
                .statusCode(200)
                .data(responseDto)
                .build());
    }
}
