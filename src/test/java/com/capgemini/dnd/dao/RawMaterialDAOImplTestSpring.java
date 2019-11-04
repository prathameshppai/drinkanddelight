package com.capgemini.dnd.dao;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import com.capgemini.dnd.customexceptions.RMOrderIDDoesNotExistException;
import com.capgemini.dnd.dto.RawMaterialStock;

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
//	@Test
//	void testAddRawMaterialOrder() {
//		fail("Not yet implemented");
//	}
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
	public void testTrackRawMaterialOrder1() {

		RawMaterialStock rawMaterialStock = new RawMaterialStock("5");
		String actualMessage = null;
		try {
			if(rawMaterialDAO.doesRawMaterialOrderIdExist(rawMaterialStock.getOrderId())) {
			actualMessage = rawMaterialDAO.trackRawMaterialOrder(rawMaterialStock);
			}
		} catch (RMOrderIDDoesNotExistException e) {
			actualMessage = e.getMessage();
		}
		String expectedMessage = "The order ID had been in the warehouse with warehouseID = w01 from 2019-08-14 05:30:00.0 to 2019-11-01 05:30:00.0(79 days)";
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

//	@Test
//	void testDoesRawMaterialOrderIdExist() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testProcessDateCheck() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testUpdateProcessDateinStock() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testUpdateRawMaterialStock() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testDoesRawMaterialOrderIdExistInStock() {
//		fail("Not yet implemented");
//	}

	

}
