package com.capgemini.dnd.servlets;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.capgemini.dnd.customexceptions.ConnectionException;
import com.capgemini.dnd.customexceptions.RMOrderIDDoesNotExistException;
import com.capgemini.dnd.dto.RawMaterialStock;
import com.capgemini.dnd.service.RawMaterialService;
import com.capgemini.dnd.service.RawMaterialServiceImpl;

public class TrackRawMaterialOrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public TrackRawMaterialOrderServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		HttpSession session = request.getSession();
		if(session.getAttribute("username") == null) {
			RequestDispatcher rd = request.getRequestDispatcher("/loginpage.html");
			rd.include(request, response);
		}
		String id = request.getParameter("OrderId");
		RawMaterialService rawMaterialServiceObject = new RawMaterialServiceImpl();
		
		try {
			if(rawMaterialServiceObject.doesRawMaterialOrderIdExist(id)) {
				response.getWriter().write("<script> alert(\"" + rawMaterialServiceObject.trackRawMaterialOrder(new RawMaterialStock(id)) + "\")</script>");
				RequestDispatcher rd1=request.getRequestDispatcher("/trackRawMaterial.html");  
			    rd1.include(request, response);
			    response.getWriter().write("<script> document.getElementById(\"trackbox\").innerHTML =  \"" +rawMaterialServiceObject.trackRawMaterialOrder(new RawMaterialStock(id))+ "\"</script>");
			}	
		} catch (RMOrderIDDoesNotExistException | ConnectionException | SQLException exception) {
			response.getWriter().write("<script> alert(\"" + exception.getMessage() + "\")</script>");
			RequestDispatcher rd1=request.getRequestDispatcher("/trackRawMaterial.html");
			rd1.include(request, response);
		}
	}
}
