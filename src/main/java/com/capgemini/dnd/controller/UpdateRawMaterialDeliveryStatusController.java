package com.capgemini.dnd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capgemini.dnd.service.ProductService;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/UpdateRawMaterialDeliveryStatus")
public class UpdateRawMaterialDeliveryStatusController {
	@Autowired
private ProductService productservice;
	@PostMapping("/Update")
	
	
	

}
