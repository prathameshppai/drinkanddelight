package com.capgemini.dnd.controller;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.capgemini.dnd.customexceptions.ConnectionException;
import com.capgemini.dnd.customexceptions.DisplayException;
import com.capgemini.dnd.customexceptions.ProductOrderNotAddedException;
import com.capgemini.dnd.dto.ProductOrder;
import com.capgemini.dnd.service.ProductService;
import com.capgemini.dnd.util.JsonUtil;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/PlaceProductOrder")
public class PlaceProductOrderController {

	@Autowired
	private ProductService productService;


	ProductOrder productOrder;

	@GetMapping("/PlaceOrder")
	public String trackProductOrder(@RequestParam("name") String name, @RequestParam("supplierId") String supplierId,
			@RequestParam("quantityValue") Double quantityValue, @RequestParam("quantityUnit") String quantityUnit,
			@RequestParam("dateOfDelivery") @DateTimeFormat(pattern = "yyyy-MM-dd") String dateOfDelivery,
			@RequestParam("pricePerUnit") Double pricePerUnit, @RequestParam("warehouseId") String warehouseId) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			productOrder = new ProductOrder(name, supplierId, quantityValue, quantityUnit,
					sdf.parse(dateOfDelivery), pricePerUnit, warehouseId);
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
