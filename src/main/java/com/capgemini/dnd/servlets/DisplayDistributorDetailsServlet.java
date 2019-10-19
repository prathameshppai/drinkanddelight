package com.capgemini.dnd.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.capgemini.dnd.dto.Distributor;
import com.capgemini.dnd.dto.RawMaterialOrder;
import com.capgemini.dnd.dto.Supplier;
import com.capgemini.dnd.service.ProductService;
import com.capgemini.dnd.service.ProductServiceImpl;
import com.capgemini.dnd.service.RawMaterialService;
import com.capgemini.dnd.service.RawMaterialServiceImpl;

public class DisplayDistributorDetailsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(req, res);
		res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		HttpSession session = req.getSession();
		if(session.getAttribute("username") == null) {
			RequestDispatcher rd = req.getRequestDispatcher("/loginpage.html");
			rd.include(req, res);
		}
		PrintWriter out = res.getWriter();
		ProductService productServiceObject = new ProductServiceImpl();
		Distributor distributorDetails = new Distributor();
		String DistributorID = req.getParameter("DistributorID");
		distributorDetails.setDistributorId(DistributorID);
		try {
			distributorDetails = productServiceObject.fetchCompleteDistributorDetail(distributorDetails);
		} catch (Exception e) {

			e.printStackTrace();
		}
		String upperhtml = "<!DOCTYPE html>\r\n" + "<html>\r\n" + "<head>\r\n" + "<meta charset=\"ISO-8859-1\">\r\n"
				+ "<title>Display Supplier Details</title>\r\n"
				+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\r\n"
				+ "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css\">\r\n"
				+ "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js\"></script>\r\n"
				+ "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js\"></script>\r\n"
				+ "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js\"></script>\r\n"
				+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"C:\\Users\\dgupta23\\eclipse-workspace\\MyBiodata\\WebContent\\WEB-INF\\PlaceOrder.css\">\r\n"
				+ "</head>\r\n" + "\r\n" + "<body>\r\n" + "<div class=\"container\">\r\n" + "<div class=\"row\">\r\n"
				+ "            <div class=\"col-lg-9 header-title\">\r\n" + "            \r\n"
				+ "                <h1>Distributor Details</h1>\r\n" + "                \r\n"
				+ "            </div>\r\n" + "            <div class=\"col-lg-3\">\r\n"
				+ "            <a href=\"#\"><img alt=\"logo\" src=\"Images/logo.png\" class=\"rounded-circle float-right header-img-title\" width=\"120\" height=\"120\">\r\n"
				+ "            </a>\r\n" + "            </div>\r\n" + "        </div>\r\n" + "\r\n" + "<hr>\r\n"
				+ "<br>\r\n" + "<div class=\"table-responsive\">" + "<table class=\"table table-striped\">>\r\n"
				+ "  <tr>\r\n" + "    <th>Distributor<br>Id</th>\r\n" + "    <th>Distributor<br>Name</th> \r\n" + "    <th>Phone<br>Number</th>\r\n"
				+ "    <th>Email<br>Id</th>\r\n" + "    <th>Address</th>\r\n";

		

		
			upperhtml += "<div class=\"table-responsive\">" + "<table class=\"table table-striped\">" + "<tr> <td>"
					+ distributorDetails.getDistributorId() + "</td>&nbsp;<td>" + "<div class=\"col-lg-1\">" + distributorDetails.getName() + "<div>"
					+ "</td>&nbsp;<td>" + distributorDetails.getPhoneNo() + "</td><td>" + distributorDetails.getEmailId() + "</td><td>"
					+ distributorDetails.getAddress();

		

		upperhtml += "<tr>\r\n" + "\r\n" + "</tr>\r\n" + "\r\n" + "</table>\r\n" + "</div>\r\n" + "</body>\r\n"
				+ "</html>";

		out.write(upperhtml);

	}
}