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
import com.capgemini.dnd.customexceptions.RMOrderNotAddedException;
import com.capgemini.dnd.dto.RawMaterialOrder;
import com.capgemini.dnd.service.RawMaterialService;
import com.capgemini.dnd.util.JsonUtil;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/PlaceRawMaterialOrder")
public class PlaceRawMaterialOrderController {

	@Autowired
	private RawMaterialService rawMaterialService;

//	@Autowired
	RawMaterialOrder rawMaterialOrder;

	@CrossOrigin(origins = "*")
	@GetMapping("/PlaceOrder")
	public String trackProductOrder(@RequestParam("name") String name, @RequestParam("supplierId") String supplierId,
			@RequestParam("quantityValue") Double quantityValue, @RequestParam("quantityUnit") String quantityUnit,
			@RequestParam("dateOfDelivery") @DateTimeFormat(pattern = "yyyy-MM-dd") String dateOfDelivery,
			@RequestParam("pricePerUnit") Double pricePerUnit, @RequestParam("warehouseId") String warehouseId) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			rawMaterialOrder = new RawMaterialOrder(name, supplierId, quantityValue, quantityUnit,
					sdf.parse(dateOfDelivery), pricePerUnit, warehouseId);
		} catch (NumberFormatException | ParseException exception) {
			String errorJsonMessage = JsonUtil.convertJavaToJson(exception.getMessage());
			return errorJsonMessage;
		}

		Date today = new Date();
		rawMaterialOrder.setDateOfOrder(today);
		rawMaterialOrder.setDeliveryStatus("Pending");

		try {
			String jsonMessage = rawMaterialService.placeRawMaterialOrder(rawMaterialOrder);
			return jsonMessage;

		} catch (RMOrderNotAddedException | ConnectionException | SQLException | DisplayException exception) {
			String errorJsonMessage = JsonUtil.convertJavaToJson(exception.getMessage());
			return errorJsonMessage;
		}
	}
}
