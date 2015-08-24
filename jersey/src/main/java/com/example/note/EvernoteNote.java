package com.example.note;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.clients.UserStoreClient;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.Notebook;
import com.evernote.thrift.TException;
import com.evernote.thrift.transport.TTransportException;
import com.example.note.entity.Note;
import com.example.note.entity.NoteBook;
import com.example.note.entity.User;

public class EvernoteNote implements NoteManage {
	
	public static final String developerToken = "S=s1:U=90d4b:E=154a4118f1f:C=14d4c605ff0:P=1cd:A=en-devtoken:V=2:H=95a2689386e276ce0e5384cd6e98b8a4";
	public static final String noteStoreUrl = "https://sandbox.evernote.com/shard/s1/notestore";
	
	public static final String evernoteHost = "sandbox.evernote.com";
	public static final String consumer_key="hiwork";
	public static final String consumer_secret="5382250a6f5eb0c8";

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
	
	
//	TODO evernote method
	
	public String getTempAuthToken() throws TTransportException{
		
//		String url="https://sandbox.evernote.com/oauth?oauth_consumer_key=en_oauth_test&oauth_signature=1ca0956605acc4f2%26&oauth_signature_method=PLAINTEXT&oauth_timestamp=1288364369&oauth_nonce=d3d9446802a44259&oauth_callback=https%3A%2F%2Ffoo.com%2Fsettings%2Findex.php%3Faction%3DoauthCallback";
		
		
		String urlFormat="%?soauth_consumer_key=%s&oauth_signature=%s&oauth_signature_method=%s&oauth_timestamp=%s&oauth_nonce=%s&oauth_callback=%s";
		
		
		EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.SANDBOX, developerToken);
		ClientFactory factory = new ClientFactory(evernoteAuth);
		UserStoreClient userStore = factory.createUserStoreClient();
		
		return developerToken;
		
		
		
	}
	
//	TODO use demo
	public static void useDeveloperToken_demo() throws EDAMUserException, EDAMSystemException, TException{
		 
		EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.SANDBOX, developerToken);
		ClientFactory factory = new ClientFactory(evernoteAuth);
		NoteStoreClient noteStore = factory.createNoteStoreClient();
		 
		List<Notebook> notebooks = noteStore.listNotebooks();
		 
		for (Notebook notebook : notebooks) {
		  System.out.println("Notebook: " + notebook.getName());
		}
	}
	
	public static void main(String[] args){
		try {
			EvernoteNote.useDeveloperToken_demo();
		} catch (EDAMUserException e) {
			e.printStackTrace();
		} catch (EDAMSystemException e) {
			e.printStackTrace();
		} catch (TException e) {
			e.printStackTrace();
		}
	}
}
