package com.nbc.trello.domain.board;

import com.nbc.trello.domain.card.Card;
import com.nbc.trello.domain.card.CardRepository;
import com.nbc.trello.domain.card.CardResponseDto;
import com.nbc.trello.domain.participants.Participants;
import com.nbc.trello.domain.participants.ParticipantsRepository;
import com.nbc.trello.domain.todo.Todo;
import com.nbc.trello.domain.todo.TodoRepository;
import com.nbc.trello.domain.todo.TodoResponseDto;
import com.nbc.trello.domain.user.User;
import com.nbc.trello.domain.user.UserRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {

	private final BoardRepository boardRepository;

	private final TodoRepository todoRepository;

	private final CardRepository cardRepository;

	private final UserRepository userRepository;

	private final ParticipantsRepository participantsRepository;

	//보드 생성
	public BoardResponseDto createBoard(BoardRequestDto requestDto, User user) {
		Board board = new Board(requestDto);
		boardRepository.save(board);

		Participants participants = new Participants(user.getId(), board.getId());
		participants.setGenerator(true);
		participantsRepository.save(participants);

		return board.toDto();
	}

	//보드 전체 조회
	public List<BoardResponseDto> getBoardList() {
		List<Board> boardList = boardRepository.findAll();
		if (boardList.isEmpty()) {
			throw new IllegalArgumentException("조회할 수 있는 보드가 없습니다.");
		}
		List<BoardResponseDto> result = new ArrayList<>();
		for (Board board : boardList) {
			List<Todo> todos = todoRepository.findByBoardId(board.getId());
			List<TodoResponseDto> todoDtos = new ArrayList<>();

			for (Todo todo : todos) {
				List<Card> cards = cardRepository.findByTodoId(todo.getId());
				List<CardResponseDto> cardDtos = cards.stream()
					.map(CardResponseDto::new)
					.collect(Collectors.toList());

				todoDtos.add(new TodoResponseDto(todo.getTitle(), cardDtos));
			}
			result.add(new BoardResponseDto(board.getName(), todoDtos));
		}
		return result;
	}

	//보드 수정
	@Transactional
	public BoardResponseDto updateBoard(Long boardId, BoardRequestDto requestDto, User user) {
		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 보드입니다."));
		participantsRepository.findByBoardIdAndUserIdAndGenerator(boardId,
			user.getId(), true).orElseThrow(() -> new IllegalArgumentException("보드 생성자가 아닙니다."));

		board.setName(requestDto.getName());
		board.setColor(requestDto.getColor());
		board.setDescription(requestDto.getDescription());

		return board.toDto();
	}

	//보드 삭제
	@Transactional
	public BoardResponseDto deleteBoard(Long boardId, User user) {
		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 보드입니다."));
		participantsRepository.findByBoardIdAndUserIdAndGenerator(boardId,
			user.getId(), true).orElseThrow(() -> new IllegalArgumentException("보드 생성자가 아닙니다."));
		boardRepository.delete(board);

		List<Participants> participantsList = participantsRepository.findByBoardId(boardId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 참가자입니다."));
		participantsRepository.deleteAll(participantsList);

		return board.toDto();
	}

	//보드 초대
	public BoardResponseDto inviteUser(Long boardId, Long userId, User user) {

		userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("존재하는 유저가 아니여서 초대할 수 없습니다."));

		if (Objects.equals(userId, user.getId())) {
			throw new IllegalArgumentException("자기 자신은 초대할 수 없습니다.");
		}

		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 보드입니다."));
		participantsRepository.findByBoardIdAndUserIdAndGenerator(boardId,
			user.getId(), true).orElseThrow(() -> new IllegalArgumentException("보드 초대 권한이 없습니다."));
		boolean present = participantsRepository.findByBoardIdAndUserId(boardId, userId)
			.isPresent();
		if (present) {
			throw new IllegalArgumentException("이미 참가한 사용자입니다.");
		}

		Participants participants = new Participants(userId, board.getId());
		participantsRepository.save(participants);

		return new BoardResponseDto(boardId, userId);
	}
}