package com.example.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(urlPatterns = {
        "/guide.php" }, asyncSupported = false, loadOnStartup = 1, name = "guideServlet", displayName = "guideServlet")
public class GuideServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(GuideServlet.class);
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        Map params = req.getParameterMap();
        Iterator<String> paramKeys = params.keySet().iterator();
        while (paramKeys.hasNext()) {
            LOG.debug("param(name) :" + paramKeys.next());
            LOG.debug("param(value) :" + params.get(paramKeys.next()));
        }

        Subject subject = SecurityUtils.getSubject();
        subject.getSession();
        subject.getPrincipal();
        RequestDispatcher dispatcher = req.getRequestDispatcher("/guide.html");
        try {
            dispatcher.forward(req, resp);
        } catch (ServletException | IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }
    
    
    @SuppressWarnings("deprecation")
    public static void parseUserInfo(String userThink){
        String s =URLDecoder.decode(userThink);
        LOG.info(s);
        String jsonStr = s.substring(6);
        ObjectMapper maper = new ObjectMapper();
        
        try {
            Map<?,?> obj = maper.readValue(jsonStr, Map.class);
            Iterator<?> keys = obj.keySet().iterator();
            while(keys.hasNext()){
               String key =  (String) keys.next();
               String value = (String)obj.get(key);
               LOG.info(String.format("%s:%s", key,value==null?"":URLDecoder.decode(value)));
            }
            LOG.info("========================================");
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }
    
    public static void parseUserInfo(File file){
        try(Reader reader = new FileReader(file);BufferedReader br = new BufferedReader(reader);) {
            String userThink = br.readLine();
            while( userThink!=null){
                parseUserInfo(userThink);
                userThink = br.readLine();
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } 
    }
    
    
    /**pull user info
     * @param args
     */
    public static void main(String[] args){
        //http://www.ibodao.com/Personal/ability/puserid/8.html
        String filePath = "D:/Users/Yahoo/git/notebook/jersey/src/main/java/com/example/action/ibodao.cookies";
        parseUserInfo(new File(filePath));
    }
}
