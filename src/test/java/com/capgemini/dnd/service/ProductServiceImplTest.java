package com.capgemini.dnd.service;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.capgemini.dnd.customexceptions.ExitDateException;
import com.capgemini.dnd.customexceptions.ExpiryDateException;
import com.capgemini.dnd.customexceptions.IncompleteDataException;
import com.capgemini.dnd.customexceptions.ManufacturingDateException;
import com.capgemini.dnd.customexceptions.ProductOrderIDDoesNotExistException;

import com.capgemini.dnd.dto.ProductStock;

@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/dispatcher-servlet.xml",
"file:src/main/webapp/WEB-INF/applicationContext.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ProductServiceImplTest {
	
	@Autowired
	private ProductService productService;

//	@Test
//	void testFetchCompleteDistributorDetail() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testUpdateStatusProductOrder() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testPlaceProductOrder() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testDisplayProductOrders() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testFetchProductNames() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testFetchDistributorIds() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testFetchWarehouseIds() {
//		fail("Not yet implemented");
//	}

	@Test
	@Transactional
	@Rollback(true)
	public void testTrackProductOrder1() {
		ProductStock productStock = new ProductStock("2");
		String actualMessage = null;
		try {
			if(productService.doesProductOrderIdExist(productStock.getOrderId())) {
			actualMessage = productService.trackProductOrder(productStock);
			}
		} catch (ProductOrderIDDoesNotExistException e) {
			actualMessage = e.getMessage();
		}
		String expectedMessage = "{\"message\":\"The order ID had been in the warehouse with warehouseID = w01 from 2019-08-16 05:30:00.0 to 2019-10-23 05:30:00.0(68 days)\"}"; 
			
		assertEquals(expectedMessage, actualMessage);
	}
	
		@Test
	@Transactional
	@Rollback(true)
	public void testTrackProductOrder2() {

		ProductStock productStock = new ProductStock("500");
		String actualMessage = null;
		try {
			if(productService.doesProductOrderIdExist(productStock.getOrderId())) {
			actualMessage = productService.trackProductOrder(productStock);
			}
		} catch (ProductOrderIDDoesNotExistException e) {
			actualMessage = e.getMessage();
		}
		String expectedMessage = "Product Order ID does not exist";
		assertEquals(expectedMessage, actualMessage);
		
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testDoesProductOrderIdExist1() throws ProductOrderIDDoesNotExistException {
		assertTrue(productService.doesProductOrderIdExist("5"));
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testDoesProductOrderIdExist2() throws ProductOrderIDDoesNotExistException {
		
		assertThrows(ProductOrderIDDoesNotExistException.class, () -> {
			productService.doesProductOrderIdExist("500");
			});
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testExitDateCheck1() throws IncompleteDataException, ParseException, ExitDateException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		ProductStock productStock = new ProductStock("1",sdf.parse("2019-10-09"));
		
		
			 boolean actualMessage = productService.exitDateCheck(productStock);
			 assertTrue(actualMessage);
			
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testExitDateCheck2() throws IncompleteDataException, ParseException, ExitDateException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		ProductStock productStock = new ProductStock("1",sdf.parse("2020-10-09"));
		assertThrows(ExitDateException.class, () -> {
				 productService.exitDateCheck(productStock);
				});
			
	}

	
	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateProcessDateinStock1() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		ProductStock productStock = new ProductStock("5", sdf.parse("2019-10-09"));
		String actualMessage = null;
		try {
			if(productService.doesProductOrderIdExist(productStock.getOrderId())) {
			actualMessage = productService.updateExitDateinStock(productStock);
			}
		} catch (ProductOrderIDDoesNotExistException e) {
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
		ProductStock productStock = new ProductStock("500", sdf.parse("2019-10-09"));
		String actualMessage = null;
		try {
			if(productService.doesProductOrderIdExist(productStock.getOrderId())) {
			actualMessage = productService.trackProductOrder(productStock);
			}
		} catch (ProductOrderIDDoesNotExistException e) {
			actualMessage = e.getMessage();
		}
		String expectedMessage = "Product Order ID does not exist";
		assertEquals(expectedMessage, actualMessage);
	}
	
	

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateProductStock1() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		ProductStock productStock = new ProductStock("100", sdf.parse("2019-10-09"), sdf.parse("2019-10-09"), "Passed");
		String actualMessage = null;
		try {
			if(productService.doesProductOrderIdExist(productStock.getOrderId())) {
			actualMessage = productService.updateProductStock(productStock);
			}
		} catch (ProductOrderIDDoesNotExistException e) {
			actualMessage = e.getMessage();
		}
		String expectedMessage = "Product Order ID does not exist";
		assertEquals(expectedMessage, actualMessage);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateProductStock2() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		ProductStock productStock = new ProductStock("5", sdf.parse("2019-09-02"), sdf.parse("2020-02-02"), "Passed");
		String actualMessage = null;
		try {
			if(productService.doesProductOrderIdExist(productStock.getOrderId())) {
			actualMessage = productService.updateProductStock(productStock);
			}
		} catch (ProductOrderIDDoesNotExistException e) {
			actualMessage = e.getMessage();
		}
		String expectedMessage = "{\"message\":\"Data inserted\"}";
		assertEquals(expectedMessage, actualMessage);
	}
	

	@Test
	@Transactional
	@Rollback(true)
	public void testDoesProductOrderIdExistInStock1() {
		assertTrue(productService.doesProductOrderIdExistInStock("5"));
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testDoesProductOrderIdExistInStock2() {
		assertFalse(productService.doesProductOrderIdExistInStock("500"));
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testValidateManufacturingDate1() throws ManufacturingDateException, ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date manufacturing_date = sdf.parse("2019-11-06");
		assertTrue(productService.validateManufacturingDate(manufacturing_date));
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testValidateManufacturingDate2() throws ManufacturingDateException, ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date manufacturing_date = sdf.parse("2021-01-06");
		assertThrows(ManufacturingDateException.class, () -> {
			productService.validateManufacturingDate(manufacturing_date);
			});
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testValidateExpiryDate1() throws ParseException, ExpiryDateException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date manufacturing_date = sdf.parse("2019-11-06");
		Date expiry_date = sdf.parse("2020-05-01");
		assertTrue(productService.validateExpiryDate(manufacturing_date, expiry_date));
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testValidateExpiryDate2() throws ExpiryDateException, ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date manufacturing_date = sdf.parse("2019-11-01");
		Date expiry_date = sdf.parse("2019-05-01");
		assertThrows(ExpiryDateException.class, () -> {
			productService.validateExpiryDate(manufacturing_date, expiry_date);
			});
	}

}
