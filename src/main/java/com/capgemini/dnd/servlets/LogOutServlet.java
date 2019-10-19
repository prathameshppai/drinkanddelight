
    
package com.capgemini.dnd.servlets;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Servlet implementation class Logout
 */
public class LogOutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LogOutServlet() {
        super();
        // TODO Auto-generated constructor stub
    }


    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
//        response.getWriter().append("Served at: ").append(request.getContextPath());
        HttpSession session = request.getSession();
        session.removeAttribute("username");
        session.invalidate();
//        RequestDispatcher rd = request.getRequestDispatcher("/loginpage.html");
//        rd.include(request, response);
        
        response.getWriter().write(ServletConstants.LOGOUT_SUCCESSFUL_MESSAGE);
        response.sendRedirect("loginpage.html");
    }


    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }


}
 








