package com.nbc.trello.domain.card;


import com.nbc.trello.global.response.CommonResponse;
import java.security.PublicKey;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
public class getCommentResponseDto {
        private Long commentId;
        private String comment;

}

