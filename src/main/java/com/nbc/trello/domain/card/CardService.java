package com.nbc.trello.domain.card;

import com.nbc.trello.domain.author.Author;
import com.nbc.trello.domain.author.AuthorRepository;
import com.nbc.trello.domain.board.BoardRepository;
import com.nbc.trello.domain.comment.Comment;
import com.nbc.trello.domain.comment.CommentRepository;
import com.nbc.trello.domain.participants.ParticipantsRepository;
import com.nbc.trello.domain.todo.Todo;
import com.nbc.trello.domain.todo.TodoRepository;
import com.nbc.trello.domain.user.User;
import com.nbc.trello.domain.user.UserRepository;
import com.nbc.trello.global.util.UserDetailsImpl;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CardService {

    private final CardRepository cardRepository;
    private final CommentRepository commentRepository;
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final ParticipantsRepository participantsRepository;
    private final AuthorRepository authorRepository;
    private final BoardRepository boardRepository;

    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;  // stringRedisTemplate : Spring 에서 제공하는 StringRedisTemplate 인스턴스로, Redis 와 상호 작용하기 위한 템플릿

    private static final String LOCK_KEY = "counterLock";

    public void create100(Long boardId, Long todoId, CardRequestDto cardRequestDto, User user){
        IntStream.range(0, 100).parallel().forEach(i -> CardCreateService(boardId, todoId, cardRequestDto, user));
    }

    //카드 등록
    public CardResponseDto CardCreateService(Long boardId, Long todoId,
        CardRequestDto cardRequestDto, User user) {
        //참여자 검증
        validateParticipants(boardId, user);
        //보드 검증
        boardRepository.findById(boardId)
            .orElseThrow(() -> new IllegalArgumentException("보드가 존재하지 않습니다."));

        //칼럽이 있는지 검증
        Todo todo = todoRepository.findById(todoId)
            .orElseThrow(() -> new IllegalArgumentException("todo에 등록할 todo를 찾을 수 없습니다."));

        //보드에 투두가 있는지 검증
        validateTodoExistInBoard(boardId, todoId);

        /*
        RLock lock = redissonClient.getFairLock(LOCK_KEY);
        try {
            boolean isLocked = lock.tryLock(10, 60, TimeUnit.SECONDS);
            if (isLocked) {
                // 락은 동작중.. isLocked 는 false 가 반환되는 경우가 있다.
                try {
                    // 수행문
                } finally {
                    lock.unlock();
                }
            } else {
                // retry 핸들링..
                // 거절하는것도 방법
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
         */

        // 작업중인 Thread 확인
        final String threadName = Thread.currentThread().getName();
        System.out.println("threadName = " + threadName);

        if(todo.getCount() != null && todo.getCount() == 0){
            throw new IllegalArgumentException("카드 개수가 제한되어 있습니다.");
        }
        if(todo.getCount() != null){
            todo.setCount(todo.getCount() - 1);
        }

        List<Card> cardList = cardRepository.findAll(Sort.by(Direction.DESC, "sequence"));

        Card card = null;

        if (cardList.isEmpty()) {
            card = new Card(cardRequestDto);
            card.setSequence(1D);

        } else {
            card = new Card(cardRequestDto);
            card.setSequence(cardList.get(0).getSequence() + 1D);
        }

        card.setTodo(todo);
        Card save = cardRepository.save(card);

        //카드 생성할 때 레파지토리 작업자 리파지토리에 생성
        authorRepository.save(new Author(user.getId(), card.getId()));

        return new CardResponseDto(boardId, todo.getId(), save.getId());
    }

    //카드 단건조회
    public CardCommentResponseDto cardGetService(Long boardId, Long todoId, Long cardId,
        UserDetailsImpl userDetails) {
        //참여자 검증
        validateParticipants(boardId, userDetails.getUser());
        //보드 검증
        boardRepository.findById(boardId)
            .orElseThrow(() -> new IllegalArgumentException("보드가 존재하지 않습니다."));
        //투두 있는지
        validateTodoExistInBoard(boardId, todoId);

        //카드가 있으면 조회
        Card card = cardRepository.findById(cardId)
            .orElseThrow(() -> new IllegalArgumentException("조회할 수 있는 카드가 없습니다."));
        List<Comment> byCardId = commentRepository.findByCardId(cardId);
        CardCommentResponseDto cardCommentResponseDto = new CardCommentResponseDto(card);

        for (Comment com : byCardId) {
            cardCommentResponseDto.getGetCommentResponseDtoList().add(
                GetCommentResponseDto.builder().commentId(com.getId())
                    .comment(com.getContent()).build()
            );
        }

        return CardCommentResponseDto.builder()
            .cardId(card.getId())
            .boardId(card.getTodo().getBoard().getId())
            .name(card.getName())
            .description(card.getDescription())
            .deadline(card.getDeadline())
            .getCommentResponseDtoList(cardCommentResponseDto.getGetCommentResponseDtoList())
            .build();
    }

    //카드 삭제
    public CardResponseDto CardDeleteService(Long boardId, Long todoId, Long cardId, User user) {
        //참여자 검증
        validateParticipants(boardId, user);

        //보드 검증
        boardRepository.findById(boardId)
            .orElseThrow(() -> new IllegalArgumentException("보드가 존재하지 않습니다."));


        //칼럽이 있는지 검증
        Todo todo = todoRepository.findById(todoId)
            .orElseThrow(() -> new IllegalArgumentException("todo에 등록할 todo를 찾을 수 없습니다."));


        //투두가 있는지 검증
        validateTodoExistInBoard(boardId, todoId);

        Card card = cardRepository.findById(cardId)
            .orElseThrow(() -> new IllegalArgumentException("삭제할 카드가 존재하지 않습니다."));

        //작업자 검증
        validateAuthor(cardId, user.getId());

        if (Objects.equals(todoId, card.getTodo().getId())) {
            cardRepository.delete(card);
            List<Author> authorList = authorRepository.findByCardId(cardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 작업자입니다."));

            //작업자 삭제
            authorRepository.deleteAll(authorList);

            if(todo.getCount() != null){
                todo.setCount(todo.getCount() + 1);
            }
        }

        return new CardResponseDto(boardId, todoId, cardId);
    }

    //카드 수정
    public CardResponseDto CardUpdateService(Long boardId, Long todoId, Long cardId,
        CardRequestDto cardRequestDto, User user) {
        //참여자 검증
        validateParticipants(boardId, user);
        //보드 검증
        boardRepository.findById(boardId)
            .orElseThrow(() -> new IllegalArgumentException("보드가 존재하지 않습니다."));
        //투두 검증
        validateTodoExistInBoard(boardId, todoId);

        Card card = cardRepository.findById(cardId)
            .orElseThrow(() -> new IllegalArgumentException("수정할 카드가 존재하지 않습니다."));

        //작업자 검증
        validateAuthor(cardId, user.getId());

        if (Objects.equals(todoId, card.getTodo().getId())) {
            card.CardUpdate(cardRequestDto);
            Card save = cardRepository.save(card);
        }

        return new CardResponseDto(boardId, card.getTodo().getId(), card.getId());
    }

    public void inviteUser(Long boardId, Long todoId, Long userId, Long cardId, User user) {
        // 참여자 확인
        validateParticipants(boardId, user);
        //보드 검증
        boardRepository.findById(boardId)
            .orElseThrow(() -> new IllegalArgumentException("보드가 존재하지 않습니다."));
        // 보드에 투두 있는지 확인
        validateTodoExistInBoard(boardId, todoId);
        // 투두에 카드 있는지 확인
        validateCardExistInTodo(todoId, cardId);

        userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하는 유저가 아니여서 초대할 수 없습니다."));
        if (Objects.equals(userId, user.getId())) {
            throw new IllegalArgumentException("자기 자신은 초대할 수 없습니다.");
        }
        boolean present = authorRepository.findByCardIdAndUserId(cardId, userId)
            .isPresent();
        if (present) {
            throw new IllegalArgumentException("이미 작업중인 사용자입니다.");
        }
        validateParticipantsVerification(boardId, userId);

        Author author = new Author(userId, cardId);
        authorRepository.save(author);
    }

    public void MoveCard(Long boardId, Long todoId, Long cardId, User user) {
        // 유저 확인
        user = findUserBy(user.getEmail());
        // 참여자 확인
        validateParticipants(boardId, user);
        // 보드에 투두 들어있나 확인
        validateTodoExistInBoard(boardId, todoId);

        Card card = cardRepository.findById(cardId).
            orElseThrow(() -> new IllegalArgumentException("카드가 존재하지 않습니다."));

        Todo todo = todoRepository.findById(todoId).
            orElseThrow(() -> new IllegalArgumentException("투두가 존재하지 않습니다."));

        // 이동 받는 투두 개수 확인
        if(todo.getCount() != null && todo.getCount() == 0){
            throw new IllegalArgumentException("카드 개수가 제한되어 있습니다.");
        }

        // 이동하는 투두 개수 증가
        Todo preTodo = card.getTodo();
        if(preTodo.getCount() != null){
            preTodo.setCount(preTodo.getCount() + 1);
        }

        card.setTodo(todo);

        cardRepository.save(card);

        // 이동 받는 투두 개수 감소
        if(todo.getCount() != null){
            todo.setCount(todo.getCount() - 1);
        }

    }

    public void changeSequenceCard(Long boardId, Long todoId, Long cardId,
        CardSequenceRequestDto cardSequenceRequestDto,
        User user) {
        // 참여자 확인
        validateParticipants(boardId, user);
        // 보드에 투두 들어있나 확인
        validateTodoExistInBoard(boardId, todoId);

        Card card = cardRepository.findById(cardId)
            .orElseThrow(() -> new IllegalArgumentException("카드가 존재하지 않습니다."));

        List<Card> cardList = cardRepository.findAll(Sort.by(Direction.ASC, "sequence"));

        int to = cardSequenceRequestDto.getSequence();

        if (to < 1 || to > cardList.size()) {
            // 범위 밖
            throw new IllegalArgumentException("해당 순서로 바꿀 수 없습니다.");
        } else if (to == cardList.size()) {
            // 배열의 끝
            card.updateSequence(cardList.get(cardList.size() - 1).getSequence() + 1);
        } else if (to == 1) {
            // 배열의 시작
            card.updateSequence(cardList.get(0).getSequence() - 1);
        } else {
            // 배열의 중간

            int from = IntStream.range(0, cardList.size())
                .filter(i -> cardList.get(i).getId().equals(cardId))
                .findFirst()
                .orElse(-1);

            double sequence;
            double preSequence;

            if (to > from + 1) {
                sequence = cardList.get(to).getSequence();
                preSequence = cardList.get(to - 1).getSequence();
            } else if (to == from + 1) {
                throw new IllegalArgumentException("자기 자신으로는 이동할 수 없습니다.");
            } else {
                sequence = cardList.get(to - 1).getSequence();
                preSequence = cardList.get(to - 2).getSequence();
            }
            card.updateSequence(sequence, preSequence);
        }
    }

    private User findUserBy(String email) {
        return userRepository.findByEmail(email).orElseThrow(
            () -> new EntityNotFoundException("User 가 존재하지 않습니다."));
    }

    private void validateParticipants(Long boardId, User user) {
        if (!participantsRepository.existsByBoardIdAndUserId(boardId, user.getId())) {
            throw new EntityExistsException("참여자가 존재하지 않습니다.");
        }
    }

    private void validateCardExistInTodo(Long todoId, Long cardId) {
        if (!cardRepository.existsByIdAndTodoId(cardId, todoId)) {
            throw new EntityExistsException("Todo 에 Card 가 존재하지 않습니다.");
        }
    }

    private void validateTodoExistInBoard(Long boardId, Long todoId) {
        if (!todoRepository.existsByIdAndBoardId(todoId, boardId)) {
            throw new EntityExistsException("Board 에 Todo 가 존재하지 않습니다.");
        }
    }

    private void validateAuthor(Long cardId, Long userId) {
        if (!authorRepository.existsByCardIdAndUserId(cardId, userId)) {
            throw new IllegalArgumentException("작업자가 아닙니다.");
        }
    }

    private void validateParticipantsVerification(Long boardId, Long userId) {
        if (!participantsRepository.existsByBoardIdAndUserId(boardId, userId)) {
            throw new EntityExistsException("참여자가 아닙니다.");
        }
    }

}
