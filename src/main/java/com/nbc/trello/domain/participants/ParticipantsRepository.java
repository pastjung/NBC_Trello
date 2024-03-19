package com.nbc.trello.domain.participants;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantsRepository extends JpaRepository<Participants, Long> {

  Optional<Participants> findByBoardIdAndUserId (Long boardId, Long userId);
}
