package com.capgemini.dnd.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
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
import com.capgemini.dnd.customexceptions.ConnectionException;
import com.capgemini.dnd.customexceptions.DisplayException;
import com.capgemini.dnd.customexceptions.ExitDateException;
import com.capgemini.dnd.customexceptions.ProductOrderIDDoesNotExistException;
import com.capgemini.dnd.customexceptions.ProductOrderNotAddedException;
import com.capgemini.dnd.dto.DisplayProductOrder;
import com.capgemini.dnd.dto.ProductOrder;

import com.capgemini.dnd.dto.ProductStock;

@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/dispatcher-servlet.xml",
"file:src/main/webapp/WEB-INF/applicationContext.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ProductDAOImplTestSpring {

	@Autowired
	private ProductDAO productDAO;

	@Test
	@Transactional
	@Rollback(true)
	public void testAddProductOrder() throws ParseException, ConnectionException, SQLException, DisplayException, ProductOrderNotAddedException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		ProductOrder productOrder = new ProductOrder("JUICE","d004",25,"kg", sdf.parse("2019-12-12"),50,"w03");
		assertTrue(productDAO.addProductOrder(productOrder));
	}
	
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
	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateProductDeliveryStatus1() throws Exception  {
		String actualMessage = null;
		
		try {
			if(productDAO.doesProductOrderIdExist("5") ){
			actualMessage = productDAO.updateStatusProductOrder("5","Dispatched");
			}
		} catch (ProductOrderIDDoesNotExistException e) {
			actualMessage = e.getMessage();
		}
		String expectedMessage = "Updated succesfully";
		assertEquals(expectedMessage, actualMessage);
	}
	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateProductDeliveryStatus2() throws Exception  {
		String actualMessage = null;
		try {
			if(productDAO.doesProductOrderIdExist("1000") ){
			actualMessage = productDAO.updateStatusProductOrder("5","Dispatched");
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
    public void testDisplayProductOrder() {
        DisplayProductOrder displayProductOrder = new DisplayProductOrder("DISPATCHED","SUP1","2019-11-06","2019-11-06");
       
        assertThrows(DisplayException.class, () -> {
            productDAO.displayProductOrders(displayProductOrder);
            });
    }

}
