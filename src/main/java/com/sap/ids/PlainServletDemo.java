package com.sap.ids;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PlainServletDemo extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3496759813533203259L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.getWriter().println("Plain old Hello, ");
		response.getWriter().println("Darren");
		response.getWriter().close();
	}

}
