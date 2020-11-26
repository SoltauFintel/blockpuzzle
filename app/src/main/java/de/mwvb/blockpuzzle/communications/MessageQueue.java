package de.mwvb.blockpuzzle.communications;

import java.util.ArrayList;
import java.util.List;

public class MessageQueue {
    private final List<Message> messages = new ArrayList<>();

    public void add(Message msg) {
        messages.add(msg);
    }

    public List<Message> getMessages() {
        return messages;
    }
}
