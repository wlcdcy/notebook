package com.example.note;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import com.example.note.entity.Note;
import com.example.note.entity.NoteBook;
import com.example.note.entity.User;

public class YoudaoNote implements NoteManage {

	public User getUser() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<NoteBook> getUserNoteBooks() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Note> getNotesInNoteBook(NoteBook notebook) {
		// TODO Auto-generated method stub
		return null;
	}

	public NoteBook createNoteBook(String noteBook_name) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean deleteNoteBook(NoteBook notebook, Date deleteTime) {
		// TODO Auto-generated method stub
		return false;
	}

	public String createNote(NoteBook notebook, Note note) {
		// TODO Auto-generated method stub
		return null;
	}

	public String createNote(String notebook_path, String note_title,
			String note_content, String note_author, String note_source,
			long note_createTime) {
		// TODO Auto-generated method stub
		return null;
	}

	public String createNote(String note_content) {
		// TODO Auto-generated method stub
		return null;
	}

	public Note viewNote(Note note) {
		// TODO Auto-generated method stub
		return null;
	}

	public Note viewNote(String note_path) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean modifyNote(Note note) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean modifyNote(String note_path, String note_title,
			String note_content, String note_author, String note_source,
			long note_modifyTime) {
		// TODO Auto-generated method stub
		return false;
	}

	public String moveNote(Note note, NoteBook notebook) {
		// TODO Auto-generated method stub
		return null;
	}

	public String moveNote(String note_path, String noteBook_path) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean deleteNote(Note note) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean deleteNote(String note_path, long modityTime) {
		// TODO Auto-generated method stub
		return false;
	}

	public String sharePublish(String note_path) {
		// TODO Auto-generated method stub
		return null;
	}

	public String uploadAttachment(String name, long size, String type,
			InputStream in) {
		// TODO Auto-generated method stub
		return null;
	}

	public InputStream downloadAttachment() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
