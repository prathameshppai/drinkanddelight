package com.capgemini.dnd.dao;

import static org.junit.jupiter.api.Assertions.*;

//import org.junit.jupiter.api.Test;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import com.capgemini.dnd.customexceptions.IncompleteDataException;
import com.capgemini.dnd.customexceptions.ExitDateException;
import com.capgemini.dnd.customexceptions.ProductOrderIDDoesNotExistException;
import com.capgemini.dnd.dto.ProductStock;




@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/dispatcher-servlet.xml",
"file:src/main/webapp/WEB-INF/applicationContext.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ProductDAOImplTestSpring {

	@Autowired
	private ProductDAO productDAO;
//	@Test
//	void testUpdateStatusProductOrder() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testDisplayProductOrderDetails() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testDisplayProductOrderbetweenDetails() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testDisplayOrdersFromDistributor() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testDisplayPendingProductOrderDetails() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testDisplayReceivedProductOrderDetails() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testDisplayDispatchedProductOrderDetails() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testDisplayCancelledProductOrderDetails() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testAddProductOrder() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testFetchDistributorDetail() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testFetchAddress() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testDisplayProductOrders() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetProductNames() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetDistributorIds() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetWarehouseIds() {
//		fail("Not yet implemented");
//	}

	@Test
	@Transactional
	@Rollback(true)
	public void testTrackProductOrder1() {
		ProductStock productStock = new ProductStock("2");
		String actualMessage = null;
		try {
			if(productDAO.doesProductOrderIdExist(productStock.getOrderId())) {
			actualMessage = productDAO.trackProductOrder(productStock);
			}
		} catch (ProductOrderIDDoesNotExistException e) {
			actualMessage = e.getMessage();
		}
		String expectedMessage = "The order ID had been in the warehouse with warehouseID = w01 from 2019-08-16 05:30:00.0 to 2019-10-23 05:30:00.0(68 days)"; 
			
		assertEquals(expectedMessage, actualMessage);
	}
	
		@Test
	@Transactional
	@Rollback(true)
	public void testTrackProductOrder2() {

		ProductStock productStock = new ProductStock("500");
		String actualMessage = null;
		try {
			if(productDAO.doesProductOrderIdExist(productStock.getOrderId())) {
			actualMessage = productDAO.trackProductOrder(productStock);
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
		assertTrue(productDAO.doesProductOrderIdExist("5"));
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testDoesProductOrderIdExist2() throws ProductOrderIDDoesNotExistException {
		
		assertThrows(ProductOrderIDDoesNotExistException.class, () -> {
			productDAO.doesProductOrderIdExist("500");
			});
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testExitDateCheck1() throws IncompleteDataException, ParseException, ExitDateException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		ProductStock productStock = new ProductStock("1",sdf.parse("2019-10-09"));
		
		
			 boolean actualMessage = productDAO.exitDateCheck(productStock);
			 assertTrue(actualMessage);
			
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testExitDateCheck2() throws IncompleteDataException, ParseException, ExitDateException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		ProductStock productStock = new ProductStock("1",sdf.parse("2020-10-09"));
		assertThrows(ExitDateException.class, () -> {
				 productDAO.exitDateCheck(productStock);
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
			if(productDAO.doesProductOrderIdExist(productStock.getOrderId())) {
			actualMessage = productDAO.updateExitDateinStock(productStock);
			}
		} catch (ProductOrderIDDoesNotExistException e) {
			actualMessage = e.getMessage();
		}
		String expectedMessage = "Data inserted";
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
			if(productDAO.doesProductOrderIdExist(productStock.getOrderId())) {
			actualMessage = productDAO.trackProductOrder(productStock);
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
			if(productDAO.doesProductOrderIdExist(productStock.getOrderId())) {
			actualMessage = productDAO.updateProductStock(productStock);
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
			if(productDAO.doesProductOrderIdExist(productStock.getOrderId())) {
			actualMessage = productDAO.updateProductStock(productStock);
			}
		} catch (ProductOrderIDDoesNotExistException e) {
			actualMessage = e.getMessage();
		}
		String expectedMessage = "Data inserted";
		assertEquals(expectedMessage, actualMessage);
	}
	

	@Test
	@Transactional
	@Rollback(true)
	public void testDoesProductOrderIdExistInStock1() {
		assertTrue(productDAO.doesProductOrderIdExistInStock("5"));
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testDoesProductOrderIdExistInStock2() {
		assertFalse(productDAO.doesProductOrderIdExistInStock("500"));
	}


}
