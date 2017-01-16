package com.example.action;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "loginServlet", urlPatterns = "/login.php")
public class LoginServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(LoginServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            resp.sendRedirect("index.php");
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            service1(req, resp);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public void service1(ServletRequest req, ServletResponse resp) throws IOException {
        String error = null;
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        token.setRememberMe(false);
        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            LOG.error(e.getMessage(), e);
            error = "用户名/密码错误 |其他错误：" + e.getMessage();
        }

        if (StringUtils.isEmpty(error)) {
            ((HttpServletResponse) resp).sendRedirect("hello.php");
        }else{
            req.setAttribute("error", error);
            ((HttpServletResponse) resp).sendRedirect("index.php");
        }

    }

}
