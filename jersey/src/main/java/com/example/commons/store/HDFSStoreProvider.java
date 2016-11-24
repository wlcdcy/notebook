package com.example.commons.store;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Properties;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HDFSStoreProvider implements IStore {
    private static final Logger LOG = LoggerFactory.getLogger(HDFSStoreProvider.class);
    private static FileSystem fs = null;

    private String dfsUrl = "hdfs://192.168.1.14:9000";
    private String dfsPath = "/user/";
    private long dfsBlockSize = 4096;
    private Configuration conf = null;

    @Override
    public void init(Properties prop) {
        if (prop != null) {
            dfsUrl = prop.getProperty("hdfs.url");
            dfsPath = prop.getProperty("hdfs.path");
            dfsBlockSize = Long.valueOf(prop.getProperty("hdfs.block.size"));
            if (LOG.isDebugEnabled()) {
                LOG.debug("hadoop url : " + dfsUrl);
                LOG.debug("hadoop relative store path : " + dfsPath);
                LOG.debug("hadoop r/w block size : " + dfsBlockSize);
            }
        }
        conf = new Configuration();
        conf.set("user.name", "hadoop");
        conf.setLong("dfs.block.size", dfsBlockSize);
    }

    public synchronized FileSystem getFs() throws IOException {
        if (fs == null) {
            fs = FileSystem.get(URI.create(dfsUrl), conf);
        }
        return fs;
    }

    @Override
    public boolean delete(String path, boolean recursive) throws Exception {
        return getFs().delete(new Path(path), recursive);
    }

    @Override
    public InputStream download(String path) throws Exception {
        return getFs().open(new Path(path));
    }

    @Override
    public void upload(InputStream in, String path) throws Exception {
        Path p = new Path(path);
        if (getFs().exists(p)) {
            try {
                FSDataOutputStream out = getFs().append(new Path(path));
                IOUtils.copyBytes(in, out, conf);
            } catch (Exception e) {
                if (!getFs().exists(p)) {
                    FSDataOutputStream out = getFs().create(new Path(path));
                    IOUtils.copyBytes(in, out, conf);
                }
                LOG.warn(e.getMessage(), e);
            }
        } else {
            FSDataOutputStream out = getFs().create(new Path(path));
            IOUtils.copyBytes(in, out, conf);
        }

    }

    @Override
    public void upload(String localPth, String path) throws Exception {
        InputStream in = new BufferedInputStream(new FileInputStream(localPth));
        Path p = new Path(path);
        if (getFs().exists(p)) {
            FSDataOutputStream out = getFs().append(new Path(path));
            IOUtils.copyBytes(in, out, conf);
        } else {
            FSDataOutputStream out = getFs().create(new Path(path));
            IOUtils.copyBytes(in, out, conf);
        }

    }

    @Override
    public String storePath() throws Exception {
        return dfsPath;
    }

    @Override
    public String storeName(String fileName) throws Exception {
        return fileName;
    }

    @Override
    public String storeStartPath() throws Exception {
        return "";
    }

    @Override
    public String downloadPath(String path) throws Exception {
        return path;
    }

    @Override
    public FileStoreInfo getStoreFileInfo(String path) throws Exception {
        Path p = new Path(path);
        if (getFs().exists(p)) {
            FileStoreInfo fileInfo = new FileStoreInfo();
            FileStatus fileStatus = getFs().getFileStatus(p);
            fileInfo.setFileSize(fileStatus.getLen());
            fileInfo.setFileName(p.getName());
            return fileInfo;
        } else {
            return null;
        }
    }

    @Override
    public String getStoreFileBymd5Value(String path) throws Exception {
        Path p = new Path(path);
        if (getFs().exists(p)) {
            return DigestUtils.md5Hex(getFs().open(p));
        } else {
            return "";
        }
    }

    @Override
    public void writeLocalDisk(InputStream in, String path) throws Exception {
        OutputStream out = null;
        FileSystemManager fsManager = VFS.getManager();
        try {
            FileObject file = fsManager.resolveFile(path);
            if (!file.exists()) {
                file.createFile();
            }
            FileContent fc = file.getContent();
            out = fc.getOutputStream(true);// 设置可以将流追加到当前文件的末尾，继续写入
            byte[] b = new byte[1024 * 8];
            int len = 0;
            while ((len = in.read(b)) > 0) {
                out.write(b, 0, len);
            }
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        } finally {
            if (out != null) {
                out.close();
            }
            in.close();
        }
    }

    @Override
    public String localPath() throws Exception {
        return "";
    }

    @Override
    public InputStream getInputStream(String path) throws Exception {
        return getFs().open(new Path(path));
    }

    @Override
    public void createFile(String path) throws Exception {
        FileSystemManager fsManager = VFS.getManager();
        FileObject file = fsManager.resolveFile(path);
        if (!file.exists()) {
            file.createFile();
        }
    }

    @Override
    public FileStoreInfo getStoreFileInfoFromLocal(String path) throws Exception {
        FileSystemManager fsManager = VFS.getManager();
        FileObject file = fsManager.resolveFile(path);
        if (file.exists()) {
            FileStoreInfo fileInfo = new FileStoreInfo();
            fileInfo.setFileSize(file.getContent().getSize());
            fileInfo.setFileName(file.getName().getBaseName());
            return fileInfo;
        } else {
            return null;
        }
    }

    @Override
    public void deleteLocalFile(String path) throws Exception {
        FileSystemManager fsManager = VFS.getManager();
        FileObject file = fsManager.resolveFile(path);
        if (file != null) {
            file.delete();
        }
    }

    @Override
    public InputStream getInputStreamLocal(String path) throws Exception {
        FileSystemManager fsManager = VFS.getManager();
        FileObject file = fsManager.resolveFile(path);
        return file.getContent().getInputStream();
    }

    @Override
    public OutputStream getOutputStreamLocal(String path) throws Exception {
        FileSystemManager fsManager = VFS.getManager();
        FileObject file = fsManager.resolveFile(path);
        return file.getContent().getOutputStream();
    }

}
