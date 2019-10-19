package com.capgemini.dnd.servlets;

 

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

 

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.capgemini.dnd.customexceptions.ConnectionException;
import com.capgemini.dnd.customexceptions.ExitDateException;
import com.capgemini.dnd.customexceptions.ProcessDateException;
import com.capgemini.dnd.customexceptions.ProductOrderIDDoesNotExistException;
import com.capgemini.dnd.customexceptions.RMOrderIDDoesNotExistException;
import com.capgemini.dnd.dto.ProductStock;
import com.capgemini.dnd.dto.RawMaterialStock;
import com.capgemini.dnd.service.ProductService;
import com.capgemini.dnd.service.ProductServiceImpl;
import com.capgemini.dnd.service.RawMaterialService;
import com.capgemini.dnd.service.RawMaterialServiceImpl;

 

public class UpdateProcessDateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

 

    public UpdateProcessDateServlet() {
        super();
    }

 

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    }

 

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

 

        doGet(req, res);
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		HttpSession session = req.getSession();
		if(session.getAttribute("username") == null) {
			RequestDispatcher rd = req.getRequestDispatcher("/loginpage.html");
			rd.include(req, res);
		}
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        RawMaterialService rawMaterialServiceObject = new RawMaterialServiceImpl();
        
        String OrderId = req.getParameter("OrderId");
        Date ProcessDate = null;

 

        try {
            if (rawMaterialServiceObject.doesRawMaterialOrderIdExist(OrderId)) {
                try {
                    ProcessDate = sdf.parse(req.getParameter("ProcessDate"));
                    try {
                        if (rawMaterialServiceObject.processDateCheck(new RawMaterialStock(OrderId, ProcessDate))) {
                        	rawMaterialServiceObject.updateProcessDateinStock(new RawMaterialStock(OrderId, ProcessDate));
                        	res.getWriter().write("<script> alert(\"" + "Process date updated" + "\")</script>");
                            RequestDispatcher rd=req.getRequestDispatcher("/updateProcessDate.html");  
                            rd.include(req, res);
                        }
                    } catch (ProcessDateException exception) {
                    	res.getWriter().write("<script> alert(\"" + exception.getMessage() + "\")</script>");
                        RequestDispatcher rd = req.getRequestDispatcher("/updateProcessDate.html");
                        rd.include(req, res);
                    }
                } catch (ParseException exception) {
                	res.getWriter().write("<script> alert(\"" + exception.getMessage() + "\")</script>");
                    RequestDispatcher rd = req.getRequestDispatcher("/updateProcessDate.html");
                    rd.include(req, res);
                }
            }
        } catch (RMOrderIDDoesNotExistException | ConnectionException | SQLException exception) {
        	res.getWriter().write("<script> alert(\"" + exception.getMessage() + "\")</script>");
			RequestDispatcher rd = req.getRequestDispatcher("/updateProcessDate.html");
            rd.include(req, res);
        }
    }
}