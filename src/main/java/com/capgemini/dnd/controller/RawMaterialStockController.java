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
import com.capgemini.dnd.customexceptions.ExpiryDateException;
import com.capgemini.dnd.customexceptions.ManufacturingDateException;
import com.capgemini.dnd.customexceptions.ProcessDateException;
import com.capgemini.dnd.dto.RawMaterialStock;
import com.capgemini.dnd.customexceptions.RMOrderIDDoesNotExistException;
import com.capgemini.dnd.service.RawMaterialService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/RawMaterialStock")
public class RawMaterialStockController {
	
	@Autowired
	private RawMaterialService rawMaterialService;
	
	
	@GetMapping("/TrackRawMaterial/{id}")
	public String trackProductOrder(@PathVariable("id") String id) {
		
		try {
			rawMaterialService.doesRawMaterialOrderIdExist(id);
			String message = rawMaterialService.trackRawMaterialOrder(new RawMaterialStock(id));
//			return ResponseEntity.ok().body(message);
			return message;
		}
		
		catch (RMOrderIDDoesNotExistException exception) {

				String errorJsonMessage = JsonUtil.convertJavaToJson(exception.getMessage());
				return errorJsonMessage;
			}
		
	}
	
	
	@PutMapping("/UpdateProcessDate/{id}")
	public String updateExitDate(@PathVariable("id") String id, @RequestParam("processDate")@DateTimeFormat(pattern="yyyy-MM-dd") String date) {
		
		String errorMessage = "";
		Date processDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			rawMaterialService.doesRawMaterialOrderIdExist(id);
			try {
				processDate = sdf.parse(date);
				
			} catch (ParseException exception) {
				errorMessage = exception.getMessage();
			}
		} catch (RMOrderIDDoesNotExistException exception) {
			errorMessage = exception.getMessage();
		}

		try {
			if (errorMessage.isEmpty()) {
				if (rawMaterialService.processDateCheck(new RawMaterialStock(id, processDate))) {
					String exitDateJsonMessage = rawMaterialService.updateProcessDateinStock(new RawMaterialStock(id, processDate));

					return exitDateJsonMessage;

				}
			} else {
				String errorJsonMessage = JsonUtil.convertJavaToJson(errorMessage);
				return errorJsonMessage;
			}
		} catch (ProcessDateException exception) {
			errorMessage += exception.getMessage();
			String errorJsonMessage = JsonUtil.convertJavaToJson(errorMessage);
			return errorJsonMessage;		
			}
		return errorMessage;
	}
	
	@PutMapping("/UpdateRawMaterialStockDetails/{id}")
	public String updateProductStockDetails(@PathVariable("id") String id, 
			@RequestParam("manDate")@DateTimeFormat(pattern="yyyy-MM-dd") String mandate, 
			@RequestParam("expDate")@DateTimeFormat(pattern="yyyy-MM-dd") String expdate,
			@RequestParam("qaStatus") String qaStatus) {
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String message = null;

		Date manufacturingDate = null;
		Date expiryDate = null;

		try {
			if (rawMaterialService.doesRawMaterialOrderIdExist(id)) {
				try {
					manufacturingDate = sdf.parse(mandate);
					if (rawMaterialService.validateManufacturingDate(manufacturingDate)) {
						try {
							expiryDate = sdf.parse(expdate);
							if (rawMaterialService.validateExpiryDate(manufacturingDate, expiryDate)) {
								
								message = rawMaterialService.updateRawMaterialStock(
										new RawMaterialStock(id, manufacturingDate, expiryDate, qaStatus));
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
		} catch (RMOrderIDDoesNotExistException exception) {

			String errorJsonMessage = JsonUtil.convertJavaToJson(exception.getMessage());
			return errorJsonMessage;

		}
		return message;

	}

		
		
	}
	

		

	


