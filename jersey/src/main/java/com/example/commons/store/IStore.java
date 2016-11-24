package com.example.commons.store;


import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * 文件存储接口
 * @author liugang
 *
 */
public interface IStore {
    
    /**配置文件初始化
     * @param prop
     */
    public void init(Properties prop);
	/**
	 * 获取不同文件系统的数据库存储路径
	 * @return
	 * @throws Exception
	 */
	public String storeStartPath()throws Exception;
	/**
	 * 获取文件存储名称
	 * @param fileName
	 * @param isStoreName 是否是分布式或者是本地的存储名称
	 * @return
	 * @throws Exception
	 */
	public String storeName(String fileName)throws Exception;
	
	/**
	 * 获取分布式或者本地存储文件路径
	 * @return
	 * @throws Exception
	 */
	public String storePath()throws Exception;
	
	/**
	 * 获取不同存储的文件下载路径
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public String downloadPath(String path)throws Exception;
	
	/**
	 * @param in 流
	 * @param hdfsDir 分布式路径
	 * @param md5 名称
	 * @param version 版本号
	 */
	public void upload(InputStream in, String path)throws Exception;
	
	/**
	 * @param localPth 本地路径
	 * @param hdfsPth 分布式路径
	 * @param md5 名称
	 * @param version 版本号
	 */
	public void upload(String localPth, String path)throws Exception;
	
	/**
	 * @param hdfsDir 下载路径
	 * @return
	 */
	public InputStream download(String path)throws Exception;
	
	/**
	 * @param hdfsDir 分布式路径
	 * @param recursive 是否级联删除
	 * @return
	 */
	public boolean delete(String path,boolean recursive)throws Exception;
	
	/**
	 * 获取存储文件的详细信息
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public FileStoreInfo getStoreFileInfo(String path)throws Exception;
	
	/**
	 * 获取本地临时文件的详细信息
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public FileStoreInfo getStoreFileInfoFromLocal(String path)throws Exception;
	
	/**
	 * 获取存储文件的详细信息
	 * @param path
	 * @return
	 * @throws Exception
	 */	
	public String getStoreFileBymd5Value(String path)throws Exception;
	
	/**
	 * 写临时文件
	 * @param path
	 */
	public void writeLocalDisk(InputStream in,String path)throws Exception;
	/**
	 * 获取临时文件路径
	 * @param isTempPath 是否是临时路径
	 * @return
	 * @throws Exception
	 */
	public String localPath()throws Exception;
	
	/**
	 * 获取指定路径对应的文件流
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public InputStream getInputStream(String path)throws Exception;
	/**
	 * 创建文件
	 * @param path
	 * @throws Exception
	 */
	public void createFile(String path)throws Exception;
	/**
	 * 删除本地文件
	 * @param path
	 * @throws Exception
	 */
	public void deleteLocalFile(String path)throws Exception;
	
	/**
	 * 获取本地文件流
	 * @return
	 * @throws Exception
	 */
	public InputStream getInputStreamLocal(String path) throws Exception;
	
	/**
	 * 获取本地输出流
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public OutputStream getOutputStreamLocal(String path)throws Exception;
}
