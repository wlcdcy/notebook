package com.example.note;

import java.io.InputStream;
import java.util.List;

//import com.example.note.entity.Note;
//import com.example.note.entity.NoteBook;
//import com.example.note.entity.User;

public interface NoteManage<T,S> {
	
//	*********************************用户部分*********************************
	
	//public User getUser();
	
//	*********************************笔记本部分*********************************
	
	/**获取所有的笔记本
	 * @return
	 */
	public List<?> getNoteBooks();
	
	/**获取笔记本下的笔记
	 * @param notebook
	 * @return
	 */
	public List<?> getNotes(String notebook_id,int offset,
			int maxNotes);
	
	/**创建一个笔记本
	 * @param noteBook_name
	 * @return
	 */
	public T createNoteBook(String noteBook_name);

	/**删除一个笔记本
	 * @param notebook_id
	 * @return
	 */
	public boolean deleteNoteBook(String notebook_id);
	
	
//	*********************************笔记部分*********************************
	
	/**创建笔记，返回新建笔记id
	 * @param notebook_id
	 * @param note_title
	 * @param note_body
	 * @return
	 */
	public String createNote(String notebook_id,String note_title,String note_body);

	/**查看笔记，返回笔记
	 * @param note_id
	 * @return
	 */
	public S getNote(String note_id);
	
	/**修改笔记
	 * @param note_id
	 * @param note_title
	 * @param note_content
	 * @return
	 */
	public boolean modifyNote(String note_id,String note_title,String note_content);
	
	/**移动笔记，返回移动后笔记路径
	 * @param noteBook_id
	 * @param note_id
	 * @return
	 */
	public boolean moveNote(String noteBook_id,String note_id);
	
	/**复制笔记
	 * @param noteBook_id
	 * @param note_id
	 * @return
	 */
	public String copyNote(String noteBook_id,String note_id);
	
	/**删除笔记
	 * 
	 * @param note_id
	 * @return
	 */
	public boolean deleteNote(String note_id);
	
//	*********************************分享部分*********************************
	
	/**分享笔记
	 * @param note_id
	 * @return	分享笔记的链接地址
	 */
	public String sharePublish(String note_id);
	
	/**取消分享
	 * @param note_id
	 * @return
	 */
	public boolean unSharePublish(String note_id);
	
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
