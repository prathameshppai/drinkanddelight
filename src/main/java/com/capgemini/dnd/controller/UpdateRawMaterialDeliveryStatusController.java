package com.capgemini.dnd.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.capgemini.dnd.service.ProductService;
import com.capgemini.dnd.util.JsonUtil;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/UpdateRawMaterialDeliveryStatus")
public class UpdateRawMaterialDeliveryStatusController {
	@Autowired
private ProductService productservice;
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public void UpdateRawMaterialDeliveryStatus (@RequestParam("orderID") String orderId, @RequestParam("DeliveryStatus") String DeliveryStatuses, HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		
		try {
			if (productservice.doesProductOrderIdExist(orderId)) {
				String jsonMessage = productservice.updateStatusProductOrder(orderId, DeliveryStatuses);
				response.getWriter().write(jsonMessage);
			}

		} catch (Exception exception) {
			String errorJsonMessage = JsonUtil.convertJavaToJson(exception.getMessage());
			response.getWriter().write(errorJsonMessage);
		}
	
	}
}

