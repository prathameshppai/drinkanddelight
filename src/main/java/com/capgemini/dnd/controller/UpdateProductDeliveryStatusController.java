package com.capgemini.dnd.controller;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.capgemini.dnd.service.ProductService;
import com.capgemini.dnd.util.JsonUtil;
import com.capgemini.dnd.util.MappingUtil;
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/UpdateProductDeliveryStatus")
public class UpdateProductDeliveryStatusController {
	    @Autowired
	private ProductService productservice;
	    @RequestMapping(method = RequestMethod.POST)
	    public void UpdateProductDeliveryStatus ( HttpServletRequest request, HttpServletResponse response) throws IOException
	    {
			Map<String, String> myMap = MappingUtil.convertJsonObjectToFieldValueMap(request);
			String id = myMap.get("orderId");
			String status = myMap.get("DeliveryStatuses");
	        
	        try {
	            if (productservice.doesProductOrderIdExist(id)) {
	                String jsonMessage = productservice.updateStatusProductOrder(id, status);
	                response.getWriter().write(jsonMessage);
	            }
	            
	        } catch (Exception exception) {
	            String errorJsonMessage = JsonUtil.convertJavaToJson(exception.getMessage());
	            response.getWriter().write(errorJsonMessage);
	        }
	    
	    }
	}

