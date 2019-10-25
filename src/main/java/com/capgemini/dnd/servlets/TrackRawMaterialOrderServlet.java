package com.capgemini.dnd.servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.capgemini.dnd.customexceptions.ConnectionException;
import com.capgemini.dnd.customexceptions.RMOrderIDDoesNotExistException;
import com.capgemini.dnd.dto.RawMaterialStock;
import com.capgemini.dnd.service.RawMaterialService;
import com.capgemini.dnd.service.RawMaterialServiceImpl;
import com.capgemini.dnd.util.JsonUtil;
import com.capgemini.dnd.util.MappingUtil;

public class TrackRawMaterialOrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public TrackRawMaterialOrderServlet() {
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json");

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods",
				"ACL, CANCELUPLOAD, CHECKIN, CHECKOUT, COPY, DELETE, GET, HEAD, LOCK, MKCALENDAR, MKCOL, MOVE, OPTIONS, POST, PROPFIND, PROPPATCH, PUT, REPORT, SEARCH, UNCHECKOUT, UNLOCK, UPDATE, VERSION-CONTROL");
		response.setHeader("Access-Control-Allow-Headers",
				"Overwrite, Destination, Content-Type, Depth, User-Agent, Translate, Range, Content-Range, Timeout, X-File-Size, X-Requested-With, If-Modified-Since, X-File-Name, Cache-Control, Location, Lock-Token, If");
		response.setHeader("Access-Control-Expose-Headers", "DAV, content-length, Allow");
		RawMaterialService rawMaterialServiceObject = new RawMaterialServiceImpl();

		Map<String, String> myMap = MappingUtil.convertJsonObjectToFieldValueMap(request);
		String id = myMap.get("OrderId");

		try {
			if (rawMaterialServiceObject.doesRawMaterialOrderIdExist(id)) {
				System.out.println("1");
				response.getWriter().write(rawMaterialServiceObject.trackRawMaterialOrder(new RawMaterialStock(id)));

			}
		} catch (RMOrderIDDoesNotExistException | ConnectionException | SQLException exception) {
			System.out.println("2");
			String errorJsonMessage = JsonUtil.convertJavaToJson(exception.getMessage());
			response.getWriter().write(errorJsonMessage);

		}
	}
}
