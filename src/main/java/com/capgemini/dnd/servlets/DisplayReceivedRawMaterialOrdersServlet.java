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

import com.capgemini.dnd.dto.RawMaterialOrder;
import com.capgemini.dnd.service.RawMaterialService;
import com.capgemini.dnd.service.RawMaterialServiceImpl;

public class DisplayReceivedRawMaterialOrdersServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public DisplayReceivedRawMaterialOrdersServlet() {
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
		PrintWriter out = res.getWriter();
		RawMaterialService rawmaterialServiceObject = new RawMaterialServiceImpl();
		List<RawMaterialOrder> rmoList = new ArrayList<RawMaterialOrder>();
		try {
			rmoList = rawmaterialServiceObject.displayReceivedRawMaterialOrderDetails();
		} catch (Exception e) {

			e.printStackTrace();
		}
		String upperhtml = "<!DOCTYPE html>\r\n" + "<html>\r\n" + "<head>\r\n" + "<meta charset=\"ISO-8859-1\">\r\n"
				+ "<title>Display Received Raw Material Orders</title>\r\n"
				+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\r\n"
				+ "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css\">\r\n"
				+ "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js\"></script>\r\n"
				+ "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js\"></script>\r\n"
				+ "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js\"></script>\r\n"
				+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"C:\\Users\\dgupta23\\eclipse-workspace\\MyBiodata\\WebContent\\WEB-INF\\PlaceOrder.css\">\r\n"
				+ "</head>\r\n" + "\r\n" + "<body>\r\n" + "<div class=\"container\">\r\n" + "<div class=\"row\">\r\n"
				+ "            <div class=\"col-lg-9 header-title\">\r\n" + "            \r\n"
				+ "                <h1>Received Raw Material Orders</h1>\r\n" + "                \r\n"
				+ "            </div>\r\n" + "            <div class=\"col-lg-3\">\r\n"
				+ "            <a href=\"#\"><img alt=\"logo\" src=\"Images/logo.png\" class=\"rounded-circle float-right header-img-title\" width=\"120\" height=\"120\">\r\n"
				+ "            </a>\r\n" + "            </div>\r\n" + "        </div>\r\n" + "\r\n" + "<hr>\r\n"
				+ "<br>\r\n" + "<div class=\"table-responsive\">" + "<table class=\"table table-striped\">>\r\n"
				+ "  <tr>\r\n" + "    <th>Order<br>Id</th>\r\n" + "    <th>Name</th> \r\n" + "    <th>pId</th>\r\n"
				+ "    <th>distributor <br> Id</th>\r\n" + "    <th>Quantity<br>Value</th>\r\n"
				+ "    <th>Quantity<br>Unit</th>\r\n" + "    <th>Order<br> Date</th>\r\n"
				+ "    <th>Delivery <br>Date</th>\r\n" + "    <th>Unit <br> Price</th>\r\n"
				+ "    <th>Total<br>Price</th>\r\n" + "    <th>Delivery<br>Status</th>\r\n"
				+ "    <th>Warehouse <br>Id</th>\r\n" + "  </tr>";

		for (RawMaterialOrder rmo : rmoList)

		{
			upperhtml += "<div class=\"table-responsive\">" + "<table class=\"table table-striped\">" + "<tr> <td>"
					+ rmo.getOrderId() + "</td>&nbsp;<td>" + "<div class=\"col-lg-1\">" + rmo.getName() + "<div>"
					+ "</td>&nbsp;<td>" + rmo.getRmId() + "</td><td>" + rmo.getSupplierId() + "</td><td>"
					+ rmo.getQuantityValue() + "</td><td>" + rmo.getQuantityUnit() + "</td><td>" + rmo.getDateOfOrder()
					+ "</td><td>" + rmo.getDateOfDelivery() + "</td><td>" + rmo.getPricePerUnit() + "</td><td>"
					+ rmo.getTotalPrice() + "</td><td>" + rmo.getDeliveryStatus() + "</td><td>" + rmo.getWarehouseId()
					+ "</td></tr>" + "</table>" + "</div>";

		}

		upperhtml += "<tr>\r\n" + "\r\n" + "</tr>\r\n" + "\r\n" + "</table>\r\n" + "</div>\r\n" + "</body>\r\n"
				+ "</html>";

		out.write(upperhtml);
	}

}