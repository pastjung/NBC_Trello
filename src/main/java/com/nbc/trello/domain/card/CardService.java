package com.nbc.trello.domain.card;

import com.nbc.trello.domain.comment.Comment;
import com.nbc.trello.domain.comment.CommentRepository;
import com.nbc.trello.domain.todo.Todo;
import com.nbc.trello.domain.todo.TodoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final CommentRepository commentRepository;
    private final TodoRepository todoRepository;

    //카드 등록
    public CardResponseDto CardCreateService(Long boardId, Long columnId,
        CardRequestDto cardRequestDto) {
        Card card = new Card(cardRequestDto);
        Todo todo = todoRepository.findById(columnId).get();
        card.setTodo(todo);
        Card save = cardRepository.save(card);

        return new CardResponseDto(boardId, todo.getId(), save.getId());
    }

    //카드 단건조회
    public CardCommentResponseDto CardGetService(Long boardId, Long columnId, Long cardId) {

        Card card = cardRepository.findById(cardId).get();
        List<Comment> byCardId = commentRepository.findByCardId(cardId);

        CardCommentResponseDto cardCommentResponseDto = new CardCommentResponseDto(card);

        for (Comment com : byCardId) {
            cardCommentResponseDto.getGetCommentResponseDtoList().add(
                getCommentResponseDto.builder().commentId(com.getId())
                    .comment(com.getContent()).build()
            );
        }

        return CardCommentResponseDto.builder().cardId(card.getId()).name(card.getName())
            .description(card.getDescription())
            .getCommentResponseDtoList(cardCommentResponseDto.getGetCommentResponseDtoList())
            .build();
    }

    //카드 삭제
    public CardResponseDto CardDeleteService(Long boardId, Long columnId, Long cardId) {

        Card card = cardRepository.findById(cardId).get();
        if (columnId == card.getTodo().getId()) {
            cardRepository.delete(card);
        }

        //return new CardResponseDto(boardId, columnId, cardId);
        return null;
    }


    //카드 수정
    public CardResponseDto CardUpdateService(Long boardId, Long columnId, Long cardId,
        CardRequestDto cardRequestDto) {

        Card card = cardRepository.findById(cardId).get();

        if (columnId == card.getTodo().getId()) {
            card.CardUpdate(cardRequestDto);
            Card save = cardRepository.save(card);
        }

        return new CardResponseDto(boardId, card.getTodo().getId(), card.getId());
    }

}
