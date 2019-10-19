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
import com.capgemini.dnd.customexceptions.ExpiryDateException;
import com.capgemini.dnd.customexceptions.ManufacturingDateException;
import com.capgemini.dnd.customexceptions.ProductOrderIDDoesNotExistException;
import com.capgemini.dnd.customexceptions.RMOrderIDDoesNotExistException;
import com.capgemini.dnd.dto.ProductStock;
import com.capgemini.dnd.dto.RawMaterialStock;
import com.capgemini.dnd.service.ProductService;
import com.capgemini.dnd.service.ProductServiceImpl;
import com.capgemini.dnd.service.RawMaterialService;
import com.capgemini.dnd.service.RawMaterialServiceImpl;


public class UpdateProductStockServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public UpdateProductStockServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		response.getWriter().append("Served at: ").append(request.getContextPath());
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
		ProductService productServiceObject = new ProductServiceImpl();
		String errorMessage = "";

		String OrderId = req.getParameter("OrderId");
		Date ManufacturingDate = null;
		Date ExpiryDate = null;
		
		try {
			if(productServiceObject.doesProductOrderIdExist(OrderId)) {
				try {
					ManufacturingDate = sdf.parse(req.getParameter("ManufacturingDate"));
					if(productServiceObject.validateManufacturingDate(ManufacturingDate)) {
						try {
							ExpiryDate = sdf.parse(req.getParameter("ExpiryDate"));
							if(productServiceObject.validateExpiryDate(ManufacturingDate, ExpiryDate)) {
								String qaStatus = req.getParameter("QAStatus");
								String message = productServiceObject.updateProductStock(new ProductStock(OrderId, ManufacturingDate, ExpiryDate, qaStatus));
								res.getWriter().write("<script> alert(\"" + "Product Stock details updated successfully!" + "\")</script>");
								RequestDispatcher rd=req.getRequestDispatcher("/UpdateProductStock.html");  
							    rd.include(req, res);
							    
							}
						} catch (ParseException | ExpiryDateException exception) {
							res.getWriter().write(exception.getMessage());
							
							RequestDispatcher rd=req.getRequestDispatcher("/UpdateProductStock.html");  
						    rd.include(req, res);
						}
					}
				} catch (ParseException | ManufacturingDateException exception) {
					res.getWriter().write("<script> alert(\"" + exception.getMessage() + "\")</script>");
					RequestDispatcher rd=req.getRequestDispatcher("/UpdateProductStock.html");  
				    rd.include(req, res);
				}
			}
		} catch (ProductOrderIDDoesNotExistException | ConnectionException | SQLException exception) {
			res.getWriter().write(exception.getMessage());
			RequestDispatcher rd=req.getRequestDispatcher("/UpdateRMStock.html");  
		    rd.include(req, res);
		}
		

		
	}

}
