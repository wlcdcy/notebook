package com.example.socket.obj;

import javolution.io.Union;

public class Message extends Union{
	public MessageRequest request = new MessageRequest();
	public MessageResponse response = new MessageResponse();
	public MessageContent content = new MessageContent();
}
