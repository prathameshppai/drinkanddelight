package com.capgemini.dnd.servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import com.capgemini.dnd.customexceptions.ConnectionException;
import com.capgemini.dnd.customexceptions.DisplayException;
import com.capgemini.dnd.customexceptions.ProductOrderNotAddedException;
import com.capgemini.dnd.dto.ProductOrder;
import com.capgemini.dnd.service.ProductService;
import com.capgemini.dnd.service.ProductServiceImpl;
import com.capgemini.dnd.util.HibernateUtil;
import com.capgemini.dnd.util.JsonUtil;
import com.capgemini.dnd.util.MappingUtil;

public class PlaceProductOrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1028319732394752L;

	public PlaceProductOrderServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) {

		String requestOrigin = request.getHeader("Origin");
		if (requestOrigin == null) {
			requestOrigin = "*";
		}
		System.out.println("Request Origin = " + requestOrigin);
		response.setHeader("Access-Control-Allow-Origin", requestOrigin);

		response.setHeader("Access-Control-Allow-Headers",
				"Content-Type, Authorization, Content-Length, X-Requested-With");
		response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST");
		response.setHeader("Access-Control-Allow-Credentials", "true");
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers",
				"Content-Type, Authorization, Content-Length, X-Requested-With");
		response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST");

		Map<String, String> myMap = MappingUtil.convertJsonObjectToFieldValueMap(request);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		ProductOrder productOrder = null;
		try {
			productOrder = new ProductOrder(myMap.get("name"), myMap.get("distributorId"),
					Double.parseDouble(myMap.get("quantityValue")), myMap.get("quantityUnit"),
					sdf.parse(myMap.get("dateOfDelivery")), Double.parseDouble(myMap.get("pricePerUnit")),
					myMap.get("warehouseId"));
		} catch (NumberFormatException | ParseException exception) {
			response.getWriter().write(JsonUtil.convertJavaToJson(exception.getMessage()));
		}

		Date today = new Date();
		productOrder.setDateOfOrder(today);
		productOrder.setDeliveryStatus("Pending");

	/*	Session session = HibernateUtil.getSessionFactory().openSession();//
		session.beginTransaction();
		session.save(productOrder);
		session.getTransaction().commit();
		HibernateUtil.shutdown();
*/
//		try {
//				String jsonMessage = productService.placeProductOrder(productOrder);
//				response.getWriter().write(jsonMessage);
//			
//		} catch (ProductOrderNotAddedException | ConnectionException | SQLException | DisplayException exception) {
//			String errorJsonMessage = JsonUtil.convertJavaToJson(exception.getMessage());
//			response.getWriter().write(errorJsonMessage);
//		}

//		Session session = HibernateUtil.getSessionFactory().openSession();//
//		session.beginTransaction();
//		session.save(productOrder);
//		session.getTransaction().commit();
//		HibernateUtil.shutdown();

		ProductService productService = new ProductServiceImpl();
		try {
				String jsonMessage = productService.placeProductOrder(productOrder);
				response.getWriter().write(jsonMessage);
			
		} catch (ProductOrderNotAddedException | ConnectionException | SQLException | DisplayException exception) {
			String errorJsonMessage = JsonUtil.convertJavaToJson(exception.getMessage());
			response.getWriter().write(errorJsonMessage);
		}

	}
}