package com.example.action;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(urlPatterns = { "/login.html" }, asyncSupported = false, loadOnStartup = 1, name = "helloServlet", displayName = "helloServlet", initParams = { @WebInitParam(name = "username", value = "") })
public class HelloServlet extends HttpServlet {
	Logger log = LoggerFactory.getLogger(HelloServlet.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(req, resp);
		Map params = req.getParameterMap();
		Iterator<String> param_keys = params.keySet().iterator();
		while (param_keys.hasNext()) {
			log.debug("param(name) :" + param_keys.next());
			log.debug("param(value) :" + params.get(param_keys.next()));
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}

}
