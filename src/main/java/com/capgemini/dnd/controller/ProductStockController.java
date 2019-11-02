package com.capgemini.dnd.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.capgemini.dnd.util.JsonUtil;
import com.capgemini.dnd.customexceptions.ExitDateException;
import com.capgemini.dnd.customexceptions.ExpiryDateException;
import com.capgemini.dnd.customexceptions.ManufacturingDateException;
import com.capgemini.dnd.dto.ProductStock;
import com.capgemini.dnd.customexceptions.ProductOrderIDDoesNotExistException;
import com.capgemini.dnd.service.ProductService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/ProductStock")
public class ProductStockController {
	
	@Autowired
	private ProductService productService;
	
	
	@GetMapping("/TrackProduct/{id}")
	public String trackProductOrder(@PathVariable("id") String id) {
		
		try {
			productService.doesProductOrderIdExist(id);
			String message = productService.trackProductOrder(new ProductStock(id));
//			return ResponseEntity.ok().body(message);
			return message;
		}
		
		catch (ProductOrderIDDoesNotExistException exception) {

				String errorJsonMessage = JsonUtil.convertJavaToJson(exception.getMessage());
				return errorJsonMessage;
			}
		
	}
	
	
	@PutMapping("/UpdateExitDate/{id}")
	public String updateExitDate(@PathVariable("id") String id, @RequestParam("exitDate")@DateTimeFormat(pattern="yyyy-MM-dd") String date) {
		
		String errorMessage = "";
		Date exitDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			productService.doesProductOrderIdExist(id);
			try {
				exitDate = sdf.parse(date);
				
			} catch (ParseException exception) {
				errorMessage = exception.getMessage();
			}
		} catch (ProductOrderIDDoesNotExistException exception) {
			errorMessage = exception.getMessage();
		}

		try {
			if (errorMessage.isEmpty()) {
				if (productService.exitDateCheck(new ProductStock(id, exitDate))) {
					String exitDateJsonMessage = productService.updateExitDateinStock(new ProductStock(id, exitDate));

					return exitDateJsonMessage;

				}
			} else {
				String errorJsonMessage = JsonUtil.convertJavaToJson(errorMessage);
				return errorJsonMessage;
			}
		} catch (ExitDateException exception) {
			errorMessage += exception.getMessage();
			String errorJsonMessage = JsonUtil.convertJavaToJson(errorMessage);
			return errorJsonMessage;		
			}
		return errorMessage;
	}
	
	@PutMapping("/UpdateProductStockDetails/{id}")
	public String updateProductStockDetails(@PathVariable("id") String id, 
			@RequestParam("manDate")@DateTimeFormat(pattern="yyyy-MM-dd") String mandate, 
			@RequestParam("expDate")@DateTimeFormat(pattern="yyyy-MM-dd") String expdate,
			@RequestParam("qaStatus") String qaStatus) {
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String message = null;

		Date manufacturingDate = null;
		Date expiryDate = null;

		try {
			if (productService.doesProductOrderIdExist(id)) {
				try {
					manufacturingDate = sdf.parse(mandate);
					if (productService.validateManufacturingDate(manufacturingDate)) {
						try {
							expiryDate = sdf.parse(expdate);
							if (productService.validateExpiryDate(manufacturingDate, expiryDate)) {
								
								message = productService.updateProductStock(
										new ProductStock(id, manufacturingDate, expiryDate, qaStatus));
								return message;

							}
						} catch (ParseException | ExpiryDateException exception) {

							String errorJsonMessage = JsonUtil.convertJavaToJson(exception.getMessage());
							return errorJsonMessage;

						}
					}
				} catch (ParseException | ManufacturingDateException exception) {

					String errorJsonMessage = JsonUtil.convertJavaToJson(exception.getMessage());
					return errorJsonMessage;

				}
			}
		} catch (ProductOrderIDDoesNotExistException exception) {

			String errorJsonMessage = JsonUtil.convertJavaToJson(exception.getMessage());
			return errorJsonMessage;

		}
		return message;

	}

		
		
	}
	

		

	


