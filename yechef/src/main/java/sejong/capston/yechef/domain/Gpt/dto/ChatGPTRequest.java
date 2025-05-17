package sejong.capston.yechef.domain.Gpt.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChatGPTRequest {
    private String model;
    private List<Message> messages;
    public ChatGPTRequest(String model, List<Message> messages) {
        this.model = model;
        this.messages = messages;
    }
}
