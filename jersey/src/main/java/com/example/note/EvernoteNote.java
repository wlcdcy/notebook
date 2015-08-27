package com.example.note;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.clients.UserStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.User;
import com.evernote.thrift.TException;
import com.evernote.thrift.transport.TTransportException;

public class EvernoteNote implements NoteManage<Notebook, Note> {

	public static final String developerToken = "S=s1:U=90d4b:E=154a4118f1f:C=14d4c605ff0:P=1cd:A=en-devtoken:V=2:H=95a2689386e276ce0e5384cd6e98b8a4";
	public static final String noteStoreUrl = "https://sandbox.evernote.com/shard/s1/notestore";

	public static final String evernoteHost = "sandbox.evernote.com";
	public static final String consumer_key = "hiwork";
	public static final String consumer_secret = "5382250a6f5eb0c8";
	private String access_token;

	private NoteStoreClient noteStore;
	private UserStoreClient userStore;

	public EvernoteNote(String access_token) {
		super();
		this.access_token = access_token;
		noteStore = createNoteStoreClient(access_token);
		userStore = createUserStoreClient(access_token);
	}

	public User getUser() {
		try {
			return userStore.getUser();
		} catch (EDAMUserException | EDAMSystemException | TException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Notebook> getNoteBooks() {
		NoteStoreClient noteStore = createNoteStoreClient(access_token);
		List<Notebook> notebooks = null;
		try {
			notebooks = noteStore.listNotebooks();
		} catch (EDAMUserException | EDAMSystemException | TException e) {
			e.printStackTrace();
		}
		return notebooks;
	}

	public List<Note> getNotes(String notebook_id, int offset,
			int maxNotes) {
		NoteFilter filter = new NoteFilter();
		filter.setNotebookGuid(notebook_id);
		filter.setOrder(NoteSortOrder.CREATED.getValue());
		filter.setAscending(true);

		NoteList notes = null;
		try {
			notes = noteStore.findNotes(filter, offset, maxNotes);
		} catch (EDAMUserException | EDAMSystemException
				| EDAMNotFoundException | TException e) {
			e.printStackTrace();
		}
		if (notes != null)
			return notes.getNotes();
		return null;
	}

	public Notebook createNoteBook(String name) {
		Notebook ourNotebook = new Notebook();
		ourNotebook.setName(name);
		try {
			return noteStore.createNotebook(ourNotebook);
		} catch (EDAMUserException | EDAMSystemException | TException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean deleteNoteBook(String  guid) {
		try {
			noteStore.expungeNotebook(guid);
			return true;
		} catch (EDAMUserException | EDAMSystemException
				| EDAMNotFoundException | TException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String createNote(String guid, String note_title,
			String note_body) {
		
		Notebook parentNotebook = null;
		try {
			parentNotebook = noteStore.getNotebook(guid);
		} catch (EDAMUserException | EDAMSystemException
				| EDAMNotFoundException | TException e1) {
			e1.printStackTrace();
		}
		
//		String nBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
//		nBody += "<!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\">";
//		nBody += "<en-note>" + note_body + "</en-note>";

		// Create note object
		Note ourNote = new Note();
		ourNote.setTitle(note_title);
		ourNote.setContent(processNoteBody(note_body));

		// parentNotebook is optional; if omitted, default notebook is used
		if (parentNotebook != null && parentNotebook.isSetGuid()) {
			ourNote.setNotebookGuid(parentNotebook.getGuid());
		}

		// Attempt to create note in Evernote account
		Note note = null;
		try {
			note = noteStore.createNote(ourNote);
			return note.getGuid();

		} catch (EDAMUserException edue) {
			// Something was wrong with the note data
			// See EDAMErrorCode enumeration for error code explanation
			// http://dev.evernote.com/documentation/reference/Errors.html#Enum_EDAMErrorCode
			System.out.println("EDAMUserException: " + edue);
		} catch (EDAMNotFoundException ednfe) {
			// Parent Notebook GUID doesn't correspond to an actual notebook
			System.out
					.println("EDAMNotFoundException: Invalid parent notebook GUID");
		} catch (Exception e) {
			// Other unexpected exceptions
			e.printStackTrace();
		}

		return null;
	}

	public Note getNote(String note_id) {
		try {
			return noteStore.getNote(note_id, false, false, false, false);
		} catch (EDAMUserException | EDAMSystemException
				| EDAMNotFoundException | TException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean modifyNote(String note_id, String note_title,String note_content) {
		Note note = null;
		try {
			note = noteStore.getNote(note_id, false, false, false, false);
		} catch (EDAMUserException | EDAMSystemException
				| EDAMNotFoundException | TException e) {
			e.printStackTrace();
		}
		if(note==null)
			return false;
		
		note.setTitle(note_title);
		note.setContent(processNoteBody(note_content));
		try {
			noteStore.updateNote(note);
			return true;
		} catch (EDAMUserException | EDAMSystemException
				| EDAMNotFoundException | TException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean moveNote(String noteBook_id,String note_id) {
		String newNote_id=copyNote(noteBook_id,note_id);
		if(StringUtils.isEmpty(newNote_id)){
			return false;
		}
		return deleteNote(note_id);
	}
	
	public String copyNote(String noteBook_id,String note_id) {

		try {
			Note  note= noteStore.copyNote(note_id, noteBook_id);
			return note.getGuid();
		} catch (EDAMUserException | EDAMSystemException
				| EDAMNotFoundException | TException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean deleteNote(String guid) {
		try {
			noteStore.deleteNote(guid);
			return true;
		} catch (EDAMUserException | EDAMSystemException
				| EDAMNotFoundException | TException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String sharePublish(String note_id) {
		try {
			String shardkey= noteStore.shareNote(note_id);
			User user = getUser();
			return String.format("https://%s/shard/%s/sh/%s/%s/", evernoteHost,user.getShardId(),note_id,shardkey);
		} catch (EDAMUserException | EDAMNotFoundException
				| EDAMSystemException | TException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean unSharePublish(String note_id) {
		try {
			noteStore.stopSharingNote(note_id);
			return true;
		} catch (EDAMUserException | EDAMNotFoundException
				| EDAMSystemException | TException e) {
			e.printStackTrace();
		}
		
		return false;
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
	
//	private static ClientFactory getClientFactory(String access_token) {
//		EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.SANDBOX,
//				access_token);
//		return  new ClientFactory(evernoteAuth);
//	}
	
	
	private static NoteStoreClient createNoteStoreClient(String access_token) {
		EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.SANDBOX,
				access_token);
		ClientFactory factory = new ClientFactory(evernoteAuth);
		NoteStoreClient noteStore = null;
		try {
			noteStore = factory.createNoteStoreClient();
		} catch (EDAMUserException | EDAMSystemException | TException e) {
			e.printStackTrace();
		}
		return noteStore;
	}

	private static UserStoreClient createUserStoreClient(String access_token) {
		EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.SANDBOX,
				access_token);
		ClientFactory factory = new ClientFactory(evernoteAuth);
		UserStoreClient userStore = null;
		try {
			userStore = factory.createUserStoreClient();
		} catch (TTransportException e) {
			e.printStackTrace();
		}
		return userStore;
	}
	
	private String processNoteBody(String content){
		String nBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		nBody += "<!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\">";
		nBody += "<en-note>" + content + "</en-note>";
		return nBody;
	}

	// TODO use demo
	public static void useDeveloperToken_demo(String access_token)
			throws EDAMUserException, EDAMSystemException, TException {
		UserStoreClient userStore = createUserStoreClient(access_token);
		User user = userStore.getUser();
		System.out.println("Evernote user id: " + user.getId());
		System.out.println("Evernote user shardId: " + user.getShardId());
		NoteStoreClient noteStore = createNoteStoreClient(access_token);

		List<Notebook> notebooks = noteStore.listNotebooks();

		for (Notebook notebook : notebooks) {
			System.out.println("Notebook: " + notebook.getName());
			NoteFilter filter = new NoteFilter();
			filter.setNotebookGuid(notebook.getGuid());
			filter.setOrder(NoteSortOrder.CREATED.getValue());
			filter.setAscending(true);

			NoteList notes = null;
			int offset = 0;
			int maxNotes = 1;
			do {
				try {
					notes = noteStore.findNotes(filter, offset, maxNotes);
				} catch (EDAMUserException | EDAMSystemException
						| EDAMNotFoundException | TException e) {
					e.printStackTrace();
				}

				if (notes == null)
					break;
				else {
					offset += notes.getNotes().size();
				}
				for (Note note : notes.getNotes()) {
					System.out.println("Note title: " + note.getTitle());
					System.out.println("Note guid: " + note.getGuid());
					try {
						String shardkey = noteStore.shareNote(note.getGuid());
						System.out.println("Note shardKey: " + shardkey);
						System.out.println("Note links : " + String.format("https://%s/shard/%s/nl/%s/%s/", evernoteHost,user.getShardId(), user.getId(),note.getGuid()));
						System.out.println("Note public links : " + String.format("https://%s/shard/%s/sh/%s/%s/", evernoteHost,user.getShardId(),note.getGuid(),shardkey));
					
						noteStore.stopSharingNote(note.getGuid());
					} catch (EDAMNotFoundException e) {
						e.printStackTrace();
					}
				}
			} while (notes.getNotes().size() == maxNotes);
		}
	}

	public static void main(String[] args) {
		String access_token = "S=s1:U=914eb:E=156b6b7061b:C=14f5f05d958:P=185:A=hiwork:V=2:H=5b8fdfb2ccf47a7bb59218851a11c21e";
		try {
			EvernoteNote.useDeveloperToken_demo(access_token);
		} catch (EDAMUserException e) {
			e.printStackTrace();
		} catch (EDAMSystemException e) {
			e.printStackTrace();
		} catch (TException e) {
			e.printStackTrace();
		}
	}
}
