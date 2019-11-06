package com.capgemini.dnd.service;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
//import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.capgemini.dnd.customexceptions.ExpiryDateException;
import com.capgemini.dnd.customexceptions.IncompleteDataException;
import com.capgemini.dnd.customexceptions.ManufacturingDateException;
import com.capgemini.dnd.customexceptions.ProcessDateException;
import com.capgemini.dnd.customexceptions.RMOrderIDDoesNotExistException;
import com.capgemini.dnd.dto.RawMaterialStock;

@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/dispatcher-servlet.xml",
"file:src/main/webapp/WEB-INF/applicationContext.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class RawMaterialServiceImplTest

{
	
	@Autowired
	private RawMaterialService rawMaterialService;

//	@Test
//	void testPlaceRawMaterialOrder() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testUpdateStatusRawMaterialOrder() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testFetchSupplierDetail() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testFetchRawMaterialNames() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testFetchSupplierIds() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testFetchWarehouseIds() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testDisplayRawmaterialOrders() {
//		fail("Not yet implemented");
//	}

	@Test
	@Transactional
	@Rollback(true)
	public void testTrackRawMaterialOrder1() {

		RawMaterialStock rawMaterialStock = new RawMaterialStock("2");
		String actualMessage = null;
		try {
			if(rawMaterialService.doesRawMaterialOrderIdExist(rawMaterialStock.getOrderId())) {
			actualMessage = rawMaterialService.trackRawMaterialOrder(rawMaterialStock);
			}
		} catch (RMOrderIDDoesNotExistException e) {
			actualMessage = e.getMessage();
		}
		String expectedMessage = "{\"message\":\"The order ID had been in the warehouse with warehouseID = w01 from 2019-08-03 05:30:00.0 to 2019-10-07 05:30:00.0(65 days)\"}";
		assertEquals(expectedMessage, actualMessage);
		
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testTrackRawMaterialOrder2() {

		RawMaterialStock rawMaterialStock = new RawMaterialStock("500");
		String actualMessage = null;
		try {
			if(rawMaterialService.doesRawMaterialOrderIdExist(rawMaterialStock.getOrderId())) {
			actualMessage = rawMaterialService.trackRawMaterialOrder(rawMaterialStock);
			}
		} catch (RMOrderIDDoesNotExistException e) {
			actualMessage = e.getMessage();
		}
		String expectedMessage = "RawMaterial Order ID does not exist";
		assertEquals(expectedMessage, actualMessage);
		
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDoesRawMaterialOrderIdExist1() throws RMOrderIDDoesNotExistException {
		assertTrue(rawMaterialService.doesRawMaterialOrderIdExist("5"));
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testDoesRawMaterialOrderIdExist2() throws RMOrderIDDoesNotExistException {
		
		assertThrows(RMOrderIDDoesNotExistException.class, () -> {
			rawMaterialService.doesRawMaterialOrderIdExist("500");
			});
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testProcessDateCheck1() throws ProcessDateException, IncompleteDataException, ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		RawMaterialStock rawMaterialStock = new RawMaterialStock("1",sdf.parse("2019-10-09"));
		
		
			 boolean actualMessage = rawMaterialService.processDateCheck(rawMaterialStock);
			 assertTrue(actualMessage);
			
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testProcessDateCheck2() throws ProcessDateException, IncompleteDataException, ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		RawMaterialStock rawMaterialStock = new RawMaterialStock("1",sdf.parse("2020-10-09"));
		assertThrows(ProcessDateException.class, () -> {
				 rawMaterialService.processDateCheck(rawMaterialStock);
				});
			
	}

	
	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateProcessDateinStock1() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		RawMaterialStock rawMaterialStock = new RawMaterialStock("5", sdf.parse("2019-10-09"));
		String actualMessage = null;
		try {
			if(rawMaterialService.doesRawMaterialOrderIdExist(rawMaterialStock.getOrderId())) {
			actualMessage = rawMaterialService.updateProcessDateinStock(rawMaterialStock);
			}
		} catch (RMOrderIDDoesNotExistException e) {
			actualMessage = e.getMessage();
		}
		String expectedMessage = "{\"message\":\"Data inserted\"}";
		assertEquals(expectedMessage, actualMessage);
	}

		
	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateProcessDateinStock2() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		RawMaterialStock rawMaterialStock = new RawMaterialStock("500", sdf.parse("2019-10-09"));
		String actualMessage = null;
		try {
			if(rawMaterialService.doesRawMaterialOrderIdExist(rawMaterialStock.getOrderId())) {
			actualMessage = rawMaterialService.trackRawMaterialOrder(rawMaterialStock);
			}
		} catch (RMOrderIDDoesNotExistException e) {
			actualMessage = e.getMessage();
		}
		String expectedMessage = "RawMaterial Order ID does not exist";
		assertEquals(expectedMessage, actualMessage);
	}
	
	

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateRawMaterialStock1() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		RawMaterialStock rawMaterialStock = new RawMaterialStock("100", sdf.parse("2019-10-09"), sdf.parse("2019-10-09"), "Passed");
		String actualMessage = null;
		try {
			if(rawMaterialService.doesRawMaterialOrderIdExist(rawMaterialStock.getOrderId())) {
			actualMessage = rawMaterialService.updateRawMaterialStock(rawMaterialStock);
			}
		} catch (RMOrderIDDoesNotExistException e) {
			actualMessage = e.getMessage();
		}
		String expectedMessage = "RawMaterial Order ID does not exist";
		assertEquals(expectedMessage, actualMessage);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateRawMaterialStock2() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		RawMaterialStock rawMaterialStock = new RawMaterialStock("5", sdf.parse("2019-09-02"), sdf.parse("2020-02-02"), "Passed");
		String actualMessage = null;
		try {
			if(rawMaterialService.doesRawMaterialOrderIdExist(rawMaterialStock.getOrderId())) {
			actualMessage = rawMaterialService.updateRawMaterialStock(rawMaterialStock);
			}
		} catch (RMOrderIDDoesNotExistException e) {
			actualMessage = e.getMessage();
		}
		String expectedMessage = "{\"message\":\"Data inserted\"}";
		assertEquals(expectedMessage, actualMessage);
	}
	

	@Test
	@Transactional
	@Rollback(true)
	public void testDoesRawMaterialOrderIdExistInStock1() {
		assertTrue(rawMaterialService.doesRawMaterialOrderIdExistInStock("5"));
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testDoesRawMaterialOrderIdExistInStock2() {
		assertFalse(rawMaterialService.doesRawMaterialOrderIdExistInStock("500"));
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testValidateManufacturingDate1() throws ManufacturingDateException, ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date manufacturing_date = sdf.parse("2019-11-06");
		assertTrue(rawMaterialService.validateManufacturingDate(manufacturing_date));
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testValidateManufacturingDate2() throws ManufacturingDateException, ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date manufacturing_date = sdf.parse("2021-01-06");
		assertThrows(ManufacturingDateException.class, () -> {
			rawMaterialService.validateManufacturingDate(manufacturing_date);
			});
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testValidateExpiryDate1() throws ParseException, ExpiryDateException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date manufacturing_date = sdf.parse("2019-11-06");
		Date expiry_date = sdf.parse("2020-05-01");
		assertTrue(rawMaterialService.validateExpiryDate(manufacturing_date, expiry_date));
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testValidateExpiryDate2() throws ExpiryDateException, ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date manufacturing_date = sdf.parse("2019-11-01");
		Date expiry_date = sdf.parse("2019-05-01");
		assertThrows(ExpiryDateException.class, () -> {
			rawMaterialService.validateExpiryDate(manufacturing_date, expiry_date);
			});
	}


}
