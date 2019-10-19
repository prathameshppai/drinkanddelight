package com.capgemini.dnd.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.capgemini.dnd.customexceptions.DateValueException;
import com.capgemini.dnd.customexceptions.SameDateException;
import com.capgemini.dnd.dto.ProductOrder;
import com.capgemini.dnd.dto.RawMaterialOrder;
import com.capgemini.dnd.service.ProductService;
import com.capgemini.dnd.service.ProductServiceImpl;

public class DisplayProductOrderBetweenDatesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public DisplayProductOrderBetweenDatesServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
//        response.getWriter().append("Served at: ").append(request.getContextPath());
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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = null, date2 = null;
		LocalDate date = LocalDate.now();
		String errormessage = "";
		Date today = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
		ProductService productServiceObject = new ProductServiceImpl();
		List<ProductOrder> poList = new ArrayList<ProductOrder>();

		try {
			date1 = sdf.parse(req.getParameter("date1"));
			if (date1.compareTo(today) > 0) {
				throw new DateValueException("Start date cannot be greater than today");
			}
		} catch (ParseException | DateValueException e) {

			errormessage += "\\n" + e.getMessage();
		}

		try {
			date2 = sdf.parse(req.getParameter("date2"));
			if (date2.compareTo(today) > 0) {
				throw new DateValueException("End date cannot be greater than today");
			}
		} catch (ParseException | DateValueException e) {

			errormessage += "\\n" + e.getMessage();
		}

		try {
			date2 = sdf.parse(req.getParameter("date2"));
			if (date1.compareTo(date2) == 0) {
				throw new SameDateException("Start date and End date cannot be same");
			}

		} catch (ParseException | SameDateException e) {

			errormessage += "\\n" + e.getMessage();
		}

		try {

			poList = productServiceObject.displayProductOrderbetweenDetails(date1, date2);

		} catch (Exception e) {

			errormessage += "\\n" + e.getMessage();
		}

		if (errormessage.equals("")) {

			String upperhtml = "<!DOCTYPE html>\r\n" + "<html>\r\n" + "<head>\r\n" + "<meta charset=\"ISO-8859-1\">\r\n"
					+ "<title>Display Product Orders From Distributor</title>\r\n"
					+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\r\n"
					+ "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css\">\r\n"
					+ "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js\"></script>\r\n"
					+ "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js\"></script>\r\n"
					+ "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js\"></script>\r\n"
					+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"C:\\Users\\dgupta23\\eclipse-workspace\\MyBiodata\\WebContent\\WEB-INF\\PlaceOrder.css\">\r\n"
					+ "</head>\r\n" + "\r\n" + "<body>\r\n" + "<div class=\"container\">\r\n"
					+ "<div class=\"row\">\r\n" + "            <div class=\"col-lg-9 header-title\">\r\n"
					+ "            \r\n" + "                <h1>Product Orders From Distributor</h1>\r\n"
					+ "                \r\n" + "            </div>\r\n" + "            <div class=\"col-lg-3\">\r\n"
					+ "            <a href=\"#\"><img alt=\"logo\" src=\"Images/logo.png\" class=\"rounded-circle float-right header-img-title\" width=\"120\" height=\"120\">\r\n"
					+ "            </a>\r\n" + "            </div>\r\n" + "        </div>\r\n" + "\r\n" + "<hr>\r\n"
					+ "<br>\r\n" + "<div class=\"table-responsive\">" + "<table class=\"table table-striped\">>\r\n"
					+ "  <tr>\r\n" + "    <th>Order<br>Id</th>\r\n" + "    <th>Name</th> \r\n" + "    <th>pId</th>\r\n"
					+ "    <th>distributor <br> Id</th>\r\n" + "    <th>Quantity<br>Value</th>\r\n"
					+ "    <th>Quantity<br>Unit</th>\r\n" + "    <th>Order<br> Date</th>\r\n"
					+ "    <th>Delivery <br>Date</th>\r\n" + "    <th>Unit <br> Price</th>\r\n"
					+ "    <th>Total<br>Price</th>\r\n" + "    <th>Delivery<br>Status</th>\r\n"
					+ "    <th>Warehouse <br>Id</th>\r\n" + "  </tr>";

			for (ProductOrder po : poList)

			{
				upperhtml += "<div class=\"table-responsive\">" + "<table class=\"table table-striped\">" + "<tr> <td>"
						+ po.getOrderId() + "</td>&nbsp;<td>" + "<div class=\"col-lg-1\">" + po.getName() + "<div>"
						+ "</td>&nbsp;<td>" + po.getPid() + "</td><td>" + po.getDistributorId() + "</td><td>"
						+ po.getQuantityValue() + "</td><td>" + po.getQuantityUnit() + "</td><td>" + po.getDateOfOrder()
						+ "</td><td>" + po.getDateofDelivery() + "</td><td>" + po.getPricePerUnit() + "</td><td>"
						+ po.getTotalPrice() + "</td><td>" + po.getDeliveryStatus() + "</td><td>" + po.getWarehouseId()
						+ "</td></tr>" + "</table>" + "</div>";

			}

			upperhtml += "<tr>\r\n" + "\r\n" + "</tr>\r\n" + "\r\n" + "</table>\r\n" + "</div>\r\n" + "</body>\r\n"
					+ "</html>";

			out.write(upperhtml);
		} else {
			String message = "<script> alert(\"" + errormessage + "\")</script>";
			res.getWriter().write(message);
			RequestDispatcher rd = req.getRequestDispatcher("/DisplayProductOrdersBetweenDates.html");
			rd.include(req, res);
		}
	}
}