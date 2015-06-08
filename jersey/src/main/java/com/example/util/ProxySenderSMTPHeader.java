package com.example.util;

import org.apache.commons.net.smtp.SimpleSMTPHeader;

public class ProxySenderSMTPHeader extends SimpleSMTPHeader {
	private final String __subject, __from, __to;
    private final StringBuffer __headerFields;
    private StringBuffer __cc;
    private String __sender;

	public ProxySenderSMTPHeader(String from, String to, String subject) {
		super(from, to, subject);
		__to = to;
        __from = from;
        __subject = subject;
        __headerFields = new StringBuffer();
        __cc = null;
        __sender = null;
	}

	@Override
    public String toString()
    {
        StringBuilder header = new StringBuilder();

        if (__headerFields.length() > 0) {
            header.append(__headerFields.toString());
        }

        header.append("From: ");
        header.append(__sender);
        
        header.append("\nSender: ");
        header.append(__from);
        
        header.append("\nTo: ");
        header.append(__to);

        if (__cc != null)
        {
            header.append("\nCc: ");
            header.append(__cc.toString());
        }

        if (__subject != null)
        {
            header.append("\nSubject: ");
            header.append(__subject);
        }

        header.append('\n');
        header.append('\n');

        return header.toString();
    }

	public void addSender(String sender) {
		this.__sender = sender;
	}
}
