package com.example.socket.obj;

import javolution.io.Struct;

public class MessageResponse extends Struct{
	public final Unsigned32 offset = new Unsigned32();
	public final Enum32<State> state = new Enum32<State>(State.values());
}
