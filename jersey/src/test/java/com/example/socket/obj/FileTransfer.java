package com.example.socket.obj;

import javolution.io.Struct;

public class FileTransfer extends Struct {
	public final Enum32<Type> type = new Enum32<Type>(Type.values());
	public final Unsigned8 sn = new Unsigned8();
	public final Unsigned8 invokeid = new Unsigned8();
	public final Unsigned8 len = new Unsigned8();
	public final Message body = new Message();
}
