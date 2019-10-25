
package com.capgemini.dnd.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.capgemini.dnd.customexceptions.BackEndException;
import com.capgemini.dnd.customexceptions.ConnectionException;
import com.capgemini.dnd.customexceptions.DisplayException;
import com.capgemini.dnd.customexceptions.DoesNotExistException;
import com.capgemini.dnd.customexceptions.ExpiryDateException;
import com.capgemini.dnd.customexceptions.ManufacturingDateException;
import com.capgemini.dnd.customexceptions.ProcessDateException;
import com.capgemini.dnd.customexceptions.RMIDDoesNotExistException;
import com.capgemini.dnd.customexceptions.RMNameDoesNotExistException;
import com.capgemini.dnd.customexceptions.RMOrderIDDoesNotExistException;
import com.capgemini.dnd.customexceptions.RMOrderNotAddedException;
import com.capgemini.dnd.customexceptions.SupplierIDDoesNotExistException;
import com.capgemini.dnd.customexceptions.WIdDoesNotExistException;
//import com.capgemini.dnd.model.RawMaterialSpecs;
import com.capgemini.dnd.dao.RawMaterialDAO;
import com.capgemini.dnd.dao.RawMaterialDAOImpl;
import com.capgemini.dnd.dto.DisplayRawMaterialOrder;
import com.capgemini.dnd.dto.RawMaterialOrder;
import com.capgemini.dnd.dto.RawMaterialStock;
import com.capgemini.dnd.dto.Supplier;
import com.capgemini.dnd.entity.RawMaterialOrderEntity;
import com.capgemini.dnd.util.JsonUtil;

public class RawMaterialServiceImpl implements RawMaterialService {

	RawMaterialDAO rawMaterialDAO = new RawMaterialDAOImpl();
	
	public List<RawMaterialOrder> displayRawMaterialOrderDetails() throws Exception {
		return(rawMaterialDAO.displayRawMaterialOrderDetails());
	}

	public List<RawMaterialOrder> displayPendingRawMaterialOrderDetails() throws Exception {
		return(rawMaterialDAO.displayPendingRawMaterialOrderDetails());
	}

	public List<RawMaterialOrder> displayCancelledRawMaterialOrderDetails() throws Exception {
		return(rawMaterialDAO.displayCancelledRawMaterialOrderDetails());
	}

	public List<RawMaterialOrder> displayReceivedRawMaterialOrderDetails() throws Exception {
		return(rawMaterialDAO.displayReceivedRawMaterialOrderDetails());
	}

	@Override
	public List<RawMaterialOrder> displayDispatchedRawMaterialOrderDetails() throws Exception {
		return(rawMaterialDAO.displayDispatchedRawMaterialOrderDetails());
	}
	
	public String placeRawMaterialOrder(RawMaterialOrder newRawMaterialOrder) throws RMOrderNotAddedException, ConnectionException, SQLException, DisplayException{
		
		if (rawMaterialDAO.addRawMaterialOrder(newRawMaterialOrder))
			return (JsonUtil.convertJavaToJson("Raw Material Order placed successfully"));
		return (JsonUtil.convertJavaToJson("0 rows updated"));
	}

	public String updateStatusRawMaterialOrder(String orderId, String newStatus) throws Exception {
		return (JsonUtil.convertJavaToJson(rawMaterialDAO.updateStatusRawMaterialOrder(orderId, newStatus)));
	}

	public List<RawMaterialOrder> displayOrdersFromSupplier(String supid) throws Exception {
		return(rawMaterialDAO.displayOrdersFromSupplier(supid));
	}

	public List<RawMaterialOrder> displayRawmaterialOrdersbetweenDetails(Date dt1, Date dt2) throws Exception {
		return(rawMaterialDAO.displayRawmaterialOrdersbetweenDetails(dt1, dt2));
	}

	public boolean doesRawMaterialOrderIdExist(String orderId)
			throws RMOrderIDDoesNotExistException, ConnectionException, SQLException {
		return (rawMaterialDAO.doesRawMaterialOrderIdExist(orderId));
	}

	public String doesRMNameExist(String name) throws RMNameDoesNotExistException, ConnectionException, SQLException {
		if (rawMaterialDAO.doesRMNameExist(name))
			return ("RM Name found");
		return ("RM not found");
	}

	public boolean doesSupplierIdExist(String suppId)
			throws SupplierIDDoesNotExistException, ConnectionException, SQLException {
		return (rawMaterialDAO.doesSupplierIdExist(suppId));
	}

	@Override
	public boolean doesWIdExist(String WId) throws WIdDoesNotExistException, ConnectionException, SQLException {
		return (rawMaterialDAO.doesWIdExist(WId));
	}

	@Override
	public boolean doesRawMaterialIdExist(String rmId, String name)
			throws RMIDDoesNotExistException, ConnectionException, SQLException {
		return (rawMaterialDAO.doesRawMaterialIdExist(rmId, name));
	}

	@Override
	public String updateRMStock(RawMaterialStock rawMaterialStock) {
		try {
			String message = rawMaterialDAO.updateRMStock(rawMaterialStock);
			String jsonMessage = JsonUtil.convertJavaToJson(message);
			return jsonMessage;
		} catch (SQLException | ConnectionException exception) {
			String message = exception.getMessage(); 
			String jsonMessage = JsonUtil.convertJavaToJson(message);
			return jsonMessage;
			
		}

	}

	@Override
	public boolean processDateCheck(RawMaterialStock rawMaterialStock) throws ProcessDateException {
		try {
			return rawMaterialDAO.processDateCheck(rawMaterialStock);
		} catch (ProcessDateException e) {
			throw new ProcessDateException(e.getMessage());
		} catch (SQLException | ConnectionException e) {

			return false;
		}
	}

	@Override
	public String updateProcessDateinStock(RawMaterialStock rawMaterialStock) {
		String message =  rawMaterialDAO.updateProcessDateinStock(rawMaterialStock);
		String jsonMessage = JsonUtil.convertJavaToJson(message);
		return jsonMessage;

	}

	@Override
	public String trackRawMaterialOrder(RawMaterialStock rawMaterialStock) {
		String message =  rawMaterialDAO.trackRawMaterialOrder(rawMaterialStock);
		String jsonMessage = JsonUtil.convertJavaToJson(message);
		return jsonMessage;
	}

	@Override
	public boolean validateManufacturingDate(Date manufacturing_date) throws ManufacturingDateException {
		Date today = new Date();
		if (manufacturing_date.before(today)) {
			return true;
		}
		throw new ManufacturingDateException("Can't enter a future manufacturing date");

	}

	@Override
	public boolean validateExpiryDate(Date manufacturing_date, Date expiry_date) throws ExpiryDateException {
		if (expiry_date.after(manufacturing_date))
			return true;
		throw new ExpiryDateException("You cant enter expiry date before manufacturing date");
	}

	public String fetchSupplierDetail(Supplier supplierDetails) throws BackEndException, DoesNotExistException {
		
		 Supplier supplierObject = rawMaterialDAO.fetchSupplierDetail(supplierDetails);
		  String jsonMessage = JsonUtil.convertJavaToJson1(supplierObject);
		return jsonMessage;
		 
	}

	@Override
	public ArrayList<String> fetchRawMaterialNames() throws DisplayException, ConnectionException {
		return(rawMaterialDAO.getRawMaterialNames());
		
	}

	@Override
	public ArrayList<String> fetchSupplierIds() throws DisplayException, ConnectionException {
		return(rawMaterialDAO.getDistributorIds());
	}
	
	@Override
	public ArrayList<String> fetchWarehouseIds() throws DisplayException, ConnectionException {
		return(rawMaterialDAO.getWarehouseIds());
	}
	
	@Override

	public String displayRawmaterialOrders(DisplayRawMaterialOrder displayRawMaterialOrderObject) throws Exception {
		
		List<RawMaterialOrderEntity> rmoList2 = new ArrayList<RawMaterialOrderEntity>();
		rmoList2 = rawMaterialDAO.displayRawmaterialOrders(displayRawMaterialOrderObject);
		String jsonMessage = JsonUtil.convertJavaToJson1(rmoList2);
		return jsonMessage;
		
	}
}
