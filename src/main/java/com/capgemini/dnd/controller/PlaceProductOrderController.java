package com.capgemini.dnd.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.capgemini.dnd.customexceptions.ConnectionException;
import com.capgemini.dnd.customexceptions.DisplayException;
import com.capgemini.dnd.customexceptions.ProductOrderNotAddedException;
import com.capgemini.dnd.dto.ProductOrder;
import com.capgemini.dnd.service.ProductService;
import com.capgemini.dnd.util.JsonUtil;
import com.capgemini.dnd.util.MappingUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/placeProductOrder")
public class PlaceProductOrderController {

	@Autowired
	private ProductService productService;

	@RequestMapping(method = RequestMethod.POST)
	public String trackProductOrder(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {

		Map<String, String> myMap = MappingUtil.convertJsonObjectToFieldValueMap(request);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		ProductOrder productOrder;
		try {
			productOrder = new ProductOrder(myMap.get("name"), myMap.get("distributorId"),
					Double.parseDouble(myMap.get("quantityValue")), myMap.get("quantityUnit"),
					sdf.parse(myMap.get("dateOfDelivery")), Double.parseDouble(myMap.get("pricePerUnit")),
					myMap.get("warehouseId"));
		} catch (NumberFormatException | ParseException exception) {
			String errorJsonMessage = JsonUtil.convertJavaToJson(exception.getMessage());
			return errorJsonMessage;
		}

		Date today = new Date();
		productOrder.setDateOfOrder(today);
		productOrder.setDeliveryStatus("Pending");
		
		try {
			String jsonMessage = productService.placeProductOrder(productOrder);
			return jsonMessage;

		} catch (ProductOrderNotAddedException | ConnectionException | SQLException | DisplayException exception) {
			String errorJsonMessage = JsonUtil.convertJavaToJson(exception.getMessage());
			return errorJsonMessage;
		}
	}
}