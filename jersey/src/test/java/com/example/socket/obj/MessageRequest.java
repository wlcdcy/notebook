package com.example.socket.obj;

import javolution.io.Struct;

public class MessageRequest extends Struct{
	public final UTF8String filename = new UTF8String(256);
	public final UTF8String md5Val = new UTF8String(32);
	
}
