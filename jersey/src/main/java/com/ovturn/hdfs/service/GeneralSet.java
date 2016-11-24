package com.ovturn.hdfs.service;

public interface GeneralSet {
    public static String DBURL = Messages.getString("GeneralSet.db.url"); //$NON-NLS-1$
    public static String DBNAME = Messages.getString("GeneralSet.db.username"); //$NON-NLS-1$
    public static String DBPSWD = Messages.getString("GeneralSet.db.password"); //$NON-NLS-1$

    public static String DFSURI = Messages.getString("GeneralSet.hdfs.url"); //$NON-NLS-1$
    public static String DFSROOTPATH = Messages.getString("GeneralSet.hdfs.storepath"); //$NON-NLS-1$
    //public static String DFSBLOCKSIZE = Messages.getString("GeneralSet.hdfs.block.size"); //$NON-NLS-1$
    public static long DFSBLOCKSIZE = 4096;
}
