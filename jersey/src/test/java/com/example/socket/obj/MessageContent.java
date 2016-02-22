package com.example.socket.obj;

import javolution.io.Struct;

public class MessageContent extends Struct {
	public final Unsigned8 len = new Unsigned8() ;
	public final UTF8String content = new UTF8String(4096);
	public final Unsigned16  crc16Val = new Unsigned16();
}
