package com.example.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(urlPatterns = { "/hello.php" }, asyncSupported = false, loadOnStartup = 1, name = "helloServlet", displayName = "helloServlet")
public class HelloServlet extends HttpServlet {
	Logger log = LoggerFactory.getLogger(HelloServlet.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// Map params = req.getParameterMap();
		// Iterator<String> param_keys = params.keySet().iterator();
		// while (param_keys.hasNext()) {
		// log.debug("param(name) :" + param_keys.next());
		// log.debug("param(value) :" + params.get(param_keys.next()));
		// }
		//
		// Subject subject = SecurityUtils.getSubject();
		// subject.getSession();
		// subject.getPrincipal();

		req.getRequestDispatcher("/hello.html").forward(req, resp);
	}

}
