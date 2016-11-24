package com.ovturn.hdfs.service;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.commons.store.HDFSStoreProvider;
import com.example.commons.store.IStore;
import com.example.commons.store.StoreFactory;
import com.example.commons.store.FileStoreInfo;
import com.ovturn.hdfs.entity.FileInfo;
import com.ovturn.hdfs.entity.StoreInfo;

public class HDFSHandler {
    private static final Logger LOG = LoggerFactory.getLogger(HDFSHandler.class);
    Connection connection = null;
    @SuppressWarnings({ "unchecked", "rawtypes" })
    ResultSetHandler<List<FileInfo>> fileInfos = new BeanListHandler(FileInfo.class);
    @SuppressWarnings({ "unchecked", "rawtypes" })
    ResultSetHandler<StoreInfo> fileStoreInfo = new BeanHandler(StoreInfo.class);
    @SuppressWarnings({ "unchecked", "rawtypes" })
    ResultSetHandler<FileInfo> fileInfo = new BeanHandler(FileInfo.class);
    QueryRunner run = new QueryRunner();
    IStore hdfsStore;
    static Properties prop =null;
    public HDFSHandler() {
        super();
        if(prop!=null){
            prop = new Properties();
            prop.setProperty("hdfs.url", GeneralSet.DFSURI);
            prop.setProperty("hdfs.path", GeneralSet.DFSROOTPATH);
            prop.setProperty("dfs.block.size", String.valueOf(GeneralSet.DFSBLOCKSIZE));
        }
        hdfsStore = StoreFactory.newInstance().createStore(HDFSStoreProvider.class.getName(),prop);
    }

    @SuppressWarnings("unchecked")
    public List<FileInfo> listFiles(String fileMime, int offset) {
        Connection conn = null;
        try {
            conn = getConnection();
            String querySql = queryFileSql(null, fileMime, offset);
            return run.query(conn, querySql, fileInfos);
        } catch (SQLException e) {
            LOG.warn(e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOG.warn(e.getMessage(), e);
                }
            }
        }
        return ListUtils.EMPTY_LIST;
    }

    @SuppressWarnings("unchecked")
    public List<FileInfo> listFiles(String filename, String fileMime, int offset) {
        Connection conn = null;
        try {
            conn = getConnection();
            String querySql = queryFileSql(filename, fileMime, offset);
            return run.query(conn, querySql, fileInfos);
        } catch (SQLException e) {
            LOG.warn(e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOG.warn(e.getMessage(), e);
                }
            }
        }
        return ListUtils.EMPTY_LIST;
    }

    private Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(GeneralSet.DBURL, GeneralSet.DBNAME, GeneralSet.DBPSWD);
        } catch (SQLException | ClassNotFoundException e) {
            LOG.warn(e.getMessage(), e);
        }
        return null;
    }

    private String queryFileSql(String filename, String fileMime, int offset) {
        if (StringUtils.isBlank(fileMime)) {
            fileMime = "mp4";
        }
        if (StringUtils.isNotBlank(filename)) {
            return String.format(
                    "SELECT DISTINCT file_storeid as fileStoreId, file_name as fileName ,file_size as fileSize FROM t_fm_fileinfo where file_mime='%s' and file_name like %s ORDER BY file_id LIMIT %d,1000",
                    fileMime, "'%" + filename + "%'", offset);
        } else {
            return String.format(
                    "SELECT DISTINCT file_storeid as fileStoreId, file_name as fileName ,file_size as fileSize FROM t_fm_fileinfo where file_mime='%s' ORDER BY file_id LIMIT %d,1000",
                    fileMime, offset);
        }
    }

    public StoreInfo getFileStoreInfo(long fileStoreId) {
        Connection conn = null;
        try {
            conn = getConnection();
            String querySql = String.format(
                    "SELECT store_id as storeId,store_address as storePath,compress_addr as compPath from t_fm_filestore where store_id=%d",
                    fileStoreId);
            return run.query(conn, querySql, fileStoreInfo);
        } catch (SQLException e) {
            LOG.warn(e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOG.warn(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    public FileInfo getFileInfo(long fileStoreId) {
        Connection conn = null;
        try {
            conn = getConnection();
            String querySql = String.format(
                    "SELECT DISTINCT file_storeid as fileStoreId, file_name as fileName ,file_size as fileSize FROM t_fm_fileinfo where file_storeid=%d",
                    fileStoreId);
            return run.query(conn, querySql, fileInfo);
        } catch (SQLException e) {
            LOG.warn(e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOG.warn(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    public InputStream downFile(long fileStoreId) {
        StoreInfo fileStore = getFileStoreInfo(fileStoreId);
        String storePath = fileStore.getStorePath();
        // storePath = "mipan/000220f5-e419-458f-a4a5-fac26159f9c4.mp4";
        try {
            storePath = hdfsStore.storePath() + storePath;
            FileStoreInfo storeInfo = hdfsStore.getStoreFileInfo(storePath);
            if (storeInfo != null) {
                LOG.debug("_____________ " + storeInfo.getFileName());
                return hdfsStore.getInputStream(storePath);
            } else {
                LOG.warn(String.format("file[%s] does not exist.....", storePath));
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public boolean deleteFileFormDB(long fileStoreId) {
        Connection conn = null;
        try {
            conn = getConnection();
            String deleteSqlStore = String.format("delete from t_fm_filestore where store_id=%d", fileStoreId);
            String deleteSqlFile = String.format("delete from t_fm_fileinfo where file_storeid=%d", fileStoreId);
            run.update(conn, deleteSqlStore);
            run.update(conn, deleteSqlFile);
            return true;
        } catch (SQLException e) {
            LOG.warn(e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOG.warn(e.getMessage(), e);
                }
            }
        }
        return false;
    }

    public int[] deleteFileFormDBByBatch(Long[][] fileStoreIds) {
        Connection conn = null;
        try {
            conn = getConnection();
            String deleteSqlStore = "delete from t_fm_filestore where store_id = ?";
            run.batch(conn, deleteSqlStore, fileStoreIds);

            String deleteSqlFile = "delete from t_fm_fileinfo where file_storeid= ? ";
            return run.batch(conn, deleteSqlFile, fileStoreIds);
        } catch (SQLException e) {
            LOG.warn(e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOG.warn(e.getMessage(), e);
                }
            }
        }
        return ArrayUtils.EMPTY_INT_ARRAY;
    }

    public boolean deleteFileFormHDFS(long fileStoreId) {
        StoreInfo fileStore = getFileStoreInfo(fileStoreId);
        boolean isremove = true;
        if (fileStore != null) {
            String storePath = fileStore.getStorePath();
            // storePath = "mipan/00003f6b-5785-4690-a950-ddb5eaad07ff.JPG";
            try {
                storePath = hdfsStore.storePath() + storePath;
                isremove = hdfsStore.delete(storePath, true);
            } catch (Exception e) {
                isremove = false;
                LOG.warn(e.getMessage(),e);
            }
        }
        return isremove;
    }

    public boolean removeFile(long fileStoreId) {
        boolean isremove = deleteFileFormHDFS(fileStoreId);
        if (isremove) {
            return deleteFileFormDB(fileStoreId);
        }
        return false;
    }

    public int[] removeFiles(List<Long> fileStoreIds) {
        List<Long> deletedFiles = new ArrayList<>();
        for (Long fileStoreId : fileStoreIds) {
            boolean isremove = deleteFileFormHDFS(fileStoreId);
            if (isremove) {
                deletedFiles.add(fileStoreId);
            }
        }
        if (!deletedFiles.isEmpty()) {
            Long[][] delFileIds = new Long[deletedFiles.size()][1];
            for (int i = 0; i < deletedFiles.size(); i++) {
                delFileIds[i][0] = deletedFiles.get(i);
            }
            return deleteFileFormDBByBatch(delFileIds);
        }
        return ArrayUtils.EMPTY_INT_ARRAY;
    }

    public static void main(String[] args) {
        HDFSHandler handler = new HDFSHandler();
        long storeId = 0;
        List<FileInfo> fileInfos = handler.listFiles(null, 0);
        for (FileInfo fileInfo : fileInfos) {
            LOG.debug(String.format("fileSize[%d] fileStoreId[%d] fileName[%s]", fileInfo.getFileSize(),
                    fileInfo.getFileStoreId(), fileInfo.getFileName()));
        }

        fileInfos = handler.listFiles("完整版", null, 0);
        for (FileInfo fileInfo : fileInfos) {
            LOG.debug(String.format("fileSize[%d] fileStoreId[%d] fileName[%s]", fileInfo.getFileSize(),
                    fileInfo.getFileStoreId(), fileInfo.getFileName()));
            storeId = fileInfo.getFileStoreId();
        }
        LOG.info("start download file...... " + storeId);
        handler.downFile(storeId);
    }

}
