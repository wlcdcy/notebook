package com.example.note;

import java.io.InputStream;
import java.util.List;


public class YoudaoNote implements NoteManage {

	@Override
	public List getNoteBooks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getNotes(String notebook_id, int offset, int maxNotes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object createNoteBook(String noteBook_name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteNoteBook(String notebook_id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String createNote(String notebook_id, String note_title,
			String note_body) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getNote(String note_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean modifyNote(String note_id, String note_title,
			String note_content) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean moveNote(String noteBook_id, String note_id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String copyNote(String noteBook_id, String note_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteNote(String note_id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String sharePublish(String note_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean unSharePublish(String note_id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String uploadAttachment(String name, long size, String type,
			InputStream in) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream downloadAttachment() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
