package com.ovturn.hdfs.action;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ovturn.hdfs.entity.FileInfo;
import com.ovturn.hdfs.service.HDFSHandler;

@WebServlet(urlPatterns = "/file/list.php")
public class FileListServlet extends HttpServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(FileListServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HDFSHandler handler = new HDFSHandler();
        String fname = req.getParameter("fname");
        LOG.info("-------------  "+fname);
        String fmime = req.getParameter("fmime");
        String spage = req.getParameter("page");
        int page = 1;
        try {
            page = Integer.parseInt(spage);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        }
        int offset = page < 1 ? 0 : (page - 1) * 1000;
        List<FileInfo> files = handler.listFiles(fname, fmime, offset);
        String htmlContent = getOutContent(files);
        resp.setCharacterEncoding("utf-8");
        try {
            resp.getWriter().println(htmlContent);
        } catch (Exception e) {
            LOG.warn(e.getMessage(),e);
        }
    }
    
    
    private String getOutContent(List<FileInfo> files){
        StringBuilder sb =new StringBuilder("<html><body><table><tr><th>存储ID</th><th>文件大小</th><th>文件名称</th></tr>");
        for(FileInfo file:files){
            sb.append(String.format("<tr><td><a target='_blank' href='%s'>%s</a></td><td>%s</td><td><a target='_blank' href='%s'>%s</a></td></tr>","delete/"+file.getFileStoreId(), file.getFileStoreId(),file.getFileSize(),"down/"+file.getFileStoreId(),file.getFileName()));
        }
        sb.append("</table></bidy></html>");
        return sb.toString();
    }

}
