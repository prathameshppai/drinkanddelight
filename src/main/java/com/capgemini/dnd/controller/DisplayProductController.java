package com.capgemini.dnd.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.capgemini.dnd.customexceptions.BackEndException;
import com.capgemini.dnd.customexceptions.DisplayException;
import com.capgemini.dnd.dto.DisplayProductOrder;
import com.capgemini.dnd.service.ProductService;
import com.capgemini.dnd.util.MappingUtil;

@Controller
@CrossOrigin(origins = "*")
@RequestMapping("/ProductOrder")
public class DisplayProductController {
	@Autowired
	ProductService productServiceObject;

	@RequestMapping(method = RequestMethod.POST)
	public void display(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String jsonMessage = "";
		String errorMessage = "";
		PrintWriter out = response.getWriter();

		DisplayProductOrder displayProductOrderObject = new DisplayProductOrder();
		Map<String, String> fieldValueMap = new HashMap<String, String>();
		fieldValueMap = MappingUtil.convertJsonObjectToFieldValueMap(request);

		String DeliveryStatusVar = fieldValueMap.get("deliveryStatus");
		String DistributorIDVar = fieldValueMap.get("distributorid");
		String date1Var = fieldValueMap.get("startdate");
		String date2Var = fieldValueMap.get("endDate");

		displayProductOrderObject.setDeliveryStatus(DeliveryStatusVar);
		displayProductOrderObject.setDistributorid(DistributorIDVar);
		displayProductOrderObject.setStartdate(date1Var);
		displayProductOrderObject.setEndDate(date2Var);

		try {
			jsonMessage = productServiceObject.displayProductOrders(displayProductOrderObject);
		} catch (DisplayException | BackEndException e) {
			errorMessage = e.getMessage();
		}

		if (errorMessage.isEmpty()) {
			out.write(jsonMessage);
		}
	}
}
