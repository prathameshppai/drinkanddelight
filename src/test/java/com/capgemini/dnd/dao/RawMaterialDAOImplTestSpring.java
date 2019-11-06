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

import com.capgemini.dnd.customexceptions.BackEndException;
import com.capgemini.dnd.customexceptions.ConnectionException;
import com.capgemini.dnd.customexceptions.DisplayException;
import com.capgemini.dnd.customexceptions.IncompleteDataException;
import com.capgemini.dnd.customexceptions.ProcessDateException;
import com.capgemini.dnd.customexceptions.RMOrderIDDoesNotExistException;
import com.capgemini.dnd.customexceptions.RMOrderNotAddedException;
import com.capgemini.dnd.customexceptions.UnregisteredEmployeeException;
import com.capgemini.dnd.customexceptions.WrongPasswordException;
import com.capgemini.dnd.dto.Employee;
import com.capgemini.dnd.dto.RawMaterialOrder;
import com.capgemini.dnd.dto.RawMaterialStock;
import com.capgemini.dnd.entity.RawMaterialOrderEntity;

@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/dispatcher-servlet.xml",
"file:src/main/webapp/WEB-INF/applicationContext.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class RawMaterialDAOImplTestSpring {
	
	
//	private static AnnotationConfigApplicationContext context;
	
	@Autowired
	private RawMaterialDAO rawMaterialDAO;
	
	

//	@Test
//	public void testRawMaterialDAOImpl() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testUpdateStatusRawMaterialOrder() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testDisplayRawMaterialOrderDetails() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testDisplayPendingRawMaterialOrderDetails() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testDisplayReceivedRawMaterialOrderDetails() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testDisplayCancelledRawMaterialOrderDetails() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testDisplayDispatchedRawMaterialOrderDetails() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testDisplayRawmaterialOrdersbetweenDetails() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testDisplayOrdersFromSupplier() {
//		fail("Not yet implemented");
//	}
//

//
//	@Test
//	void testFetchSupplierDetail() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testAddSupplierAddress() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testDoesSupplierAddressExist() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testDisplayRawmaterialOrders() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetRawMaterialNames() {
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
	public void testAddRawMaterialOrder() throws ParseException, RMOrderNotAddedException, ConnectionException, SQLException, DisplayException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		RawMaterialOrder rawMaterialOrder = new RawMaterialOrder("JUICE","d004",25,"kg", sdf.parse("2019-12-12"),50,"w03");
		assertTrue(rawMaterialDAO.addRawMaterialOrder(rawMaterialOrder));
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testTrackRawMaterialOrder1() {

		RawMaterialStock rawMaterialStock = new RawMaterialStock("2");
		String actualMessage = null;
		try {
			if(rawMaterialDAO.doesRawMaterialOrderIdExist(rawMaterialStock.getOrderId())) {
			actualMessage = rawMaterialDAO.trackRawMaterialOrder(rawMaterialStock);
			}
		} catch (RMOrderIDDoesNotExistException e) {
			actualMessage = e.getMessage();
		}
		String expectedMessage = "The order ID had been in the warehouse with warehouseID = w01 from 2019-08-03 05:30:00.0 to 2019-10-07 05:30:00.0(65 days)";
		assertEquals(expectedMessage, actualMessage);
		
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testTrackRawMaterialOrder2() {

		RawMaterialStock rawMaterialStock = new RawMaterialStock("500");
		String actualMessage = null;
		try {
			if(rawMaterialDAO.doesRawMaterialOrderIdExist(rawMaterialStock.getOrderId())) {
			actualMessage = rawMaterialDAO.trackRawMaterialOrder(rawMaterialStock);
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
		assertTrue(rawMaterialDAO.doesRawMaterialOrderIdExist("5"));
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testDoesRawMaterialOrderIdExist2() throws RMOrderIDDoesNotExistException {
		
		assertThrows(RMOrderIDDoesNotExistException.class, () -> {
			rawMaterialDAO.doesRawMaterialOrderIdExist("500");
			});
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testProcessDateCheck1() throws ProcessDateException, IncompleteDataException, ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		RawMaterialStock rawMaterialStock = new RawMaterialStock("1",sdf.parse("2019-10-09"));
		
		
			 boolean actualMessage = rawMaterialDAO.processDateCheck(rawMaterialStock);
			 assertTrue(actualMessage);
			
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testProcessDateCheck2() throws ProcessDateException, IncompleteDataException, ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		RawMaterialStock rawMaterialStock = new RawMaterialStock("1",sdf.parse("2020-10-09"));
		assertThrows(ProcessDateException.class, () -> {
				 rawMaterialDAO.processDateCheck(rawMaterialStock);
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
			if(rawMaterialDAO.doesRawMaterialOrderIdExist(rawMaterialStock.getOrderId())) {
			actualMessage = rawMaterialDAO.updateProcessDateinStock(rawMaterialStock);
			}
		} catch (RMOrderIDDoesNotExistException e) {
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
		RawMaterialStock rawMaterialStock = new RawMaterialStock("500", sdf.parse("2019-10-09"));
		String actualMessage = null;
		try {
			if(rawMaterialDAO.doesRawMaterialOrderIdExist(rawMaterialStock.getOrderId())) {
			actualMessage = rawMaterialDAO.trackRawMaterialOrder(rawMaterialStock);
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
			if(rawMaterialDAO.doesRawMaterialOrderIdExist(rawMaterialStock.getOrderId())) {
			actualMessage = rawMaterialDAO.updateRawMaterialStock(rawMaterialStock);
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
			if(rawMaterialDAO.doesRawMaterialOrderIdExist(rawMaterialStock.getOrderId())) {
			actualMessage = rawMaterialDAO.updateRawMaterialStock(rawMaterialStock);
			}
		} catch (RMOrderIDDoesNotExistException e) {
			actualMessage = e.getMessage();
		}
		String expectedMessage = "Data inserted";
		assertEquals(expectedMessage, actualMessage);
	}
	

	@Test
	@Transactional
	@Rollback(true)
	public void testDoesRawMaterialOrderIdExistInStock1() {
		assertTrue(rawMaterialDAO.doesRawMaterialOrderIdExistInStock("5"));
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testDoesRawMaterialOrderIdExistInStock2() {
		assertFalse(rawMaterialDAO.doesRawMaterialOrderIdExistInStock("500"));
	}

	@Test
    @Transactional
    @Rollback(true)
    public void testUpdateRawMaterialDeliveryStatus1() throws Exception{
        String actualMessage = null;
        try {
            if(rawMaterialDAO.doesRawMaterialOrderIdExist("5") ){
            actualMessage = rawMaterialDAO.updateStatusRawMaterialOrder("5","Recieved");
            }
        } catch (RMOrderIDDoesNotExistException e) {
            actualMessage = e.getMessage();
        }
        String expectedMessage = "Updated succesfully";
        assertEquals(expectedMessage, actualMessage);
    }
	@Test
    @Transactional
    @Rollback(true)
    public void testUpdateRawMaterialDeliveryStatus2() throws Exception{
        String actualMessage = null;
        try {
            if(rawMaterialDAO.doesRawMaterialOrderIdExist("1000") ){
            actualMessage = rawMaterialDAO.updateStatusRawMaterialOrder("5","Recieved");
            }
        } catch (RMOrderIDDoesNotExistException e) {
            actualMessage = e.getMessage();
        }
        String expectedMessage = "RawMaterial Order ID does not exist";
        assertEquals(expectedMessage, actualMessage);
    }
}
