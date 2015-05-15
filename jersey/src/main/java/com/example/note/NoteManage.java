package com.example.note;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import com.example.note.entity.Note;
import com.example.note.entity.NoteBook;
import com.example.note.entity.User;

public interface NoteManage {
	
//	*********************************用户部分*********************************
	
	public User getUser();
	
//	*********************************笔记本部分*********************************
	
	/**获取所有的笔记本
	 * @return
	 */
	public List<NoteBook> getUserNoteBooks();
	
	/**获取笔记本下的笔记
	 * @param notebook
	 * @return
	 */
	public List<Note> getNotesInNoteBook(NoteBook notebook);
	
	/**创建一个笔记本
	 * @param noteBook_name
	 * @return
	 */
	public NoteBook createNoteBook(String noteBook_name);

	/**删除一个笔记本
	 * @param notebook
	 * @param deleteTime
	 * @return
	 */
	public boolean deleteNoteBook(NoteBook notebook,Date deleteTime);
	
	
//	*********************************笔记部分*********************************
	
	/**创建笔记，返回新建笔记路径
	 * @param note
	 * @return	笔记路径
	 */
	public String createNote(NoteBook notebook,Note note);
	public String createNote(String notebook_path,String note_title,String note_content,String note_author,String note_source,long note_createTime);
	public String createNote(String note_content);
	
	/**查看笔记，返回笔记内容
	 * @param note
	 * @return	笔记详情
	 */
	public Note viewNote(Note note);
	public Note viewNote(String note_path);
	
	/**修改笔记
	 * @param note
	 * @return
	 */
	public boolean modifyNote(Note note);
	public boolean modifyNote(String note_path,String note_title,String note_content,String note_author,String note_source,long note_modifyTime);
	
	/**移动笔记，返回移动后笔记路径
	 * @param note
	 * @param notebook
	 * @return	笔记路径
	 */
	public String moveNote(Note note,NoteBook notebook);
	public String moveNote(String note_path,String noteBook_path);
	
	/**删除笔记
	 * @param note
	 * @return
	 */
	public boolean deleteNote(Note note);
	public boolean deleteNote(String note_path,long modityTime);
	
//	*********************************分享部分*********************************
	
	/**分享笔记
	 * @param note_path
	 * @return	分享笔记的链接
	 */
	public String sharePublish(String note_path);
	
	
//	*********************************附件惭怍部分*********************************
	
	/**上传附件|图片
	 * @param name
	 * @param size
	 * @param type
	 * @param in
	 * @return
	 */
	public String uploadAttachment(String name,long size,String type,InputStream in);
	
	/**下载附件
	 * @return
	 */
	public InputStream downloadAttachment();
}
