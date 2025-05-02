package pyo.quizgame.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pyo.quizgame.domain.UserMessage;
import pyo.quizgame.dto.UserMessageDto;
import pyo.quizgame.repository.MessageRepository;

@Service
@RequiredArgsConstructor
public class UserMessageService {

    private final MessageRepository messageRepository;

    public void saveUserMessage(UserMessageDto userMessageDto) {
        UserMessage userMessage = UserMessage.builder()
                .nickName(userMessageDto.getNickName())
                .message(userMessageDto.getMessage())
                .build();
        messageRepository.save(userMessage);
    }

    public Page<UserMessage> searchUserMessages(String searchText, Pageable pageable) {
        return messageRepository.findByMessageContaining(searchText, pageable);
    }
}
