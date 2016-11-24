package com.example.action;

import java.io.IOException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(urlPatterns = {
        "/hello.php" }, asyncSupported = false, loadOnStartup = 1, name = "helloServlet", displayName = "helloServlet")
public class HelloServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(HelloServlet.class);
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
        RequestDispatcher dispatcher = req.getRequestDispatcher("/hello.html");
        try {
            dispatcher.forward(req, resp);
        } catch (ServletException | IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
