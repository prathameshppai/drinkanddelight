package com.capgemini.dnd.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.capgemini.dnd.customexceptions.BackEndException;
import com.capgemini.dnd.customexceptions.ConnectionException;
import com.capgemini.dnd.customexceptions.DisplayException;
import com.capgemini.dnd.customexceptions.DoesNotExistException;
import com.capgemini.dnd.customexceptions.ExpiryDateException;
import com.capgemini.dnd.customexceptions.IncompleteDataException;
import com.capgemini.dnd.customexceptions.ManufacturingDateException;
import com.capgemini.dnd.customexceptions.ProcessDateException;
import com.capgemini.dnd.customexceptions.RMOrderIDDoesNotExistException;
import com.capgemini.dnd.customexceptions.RMOrderNotAddedException;
import com.capgemini.dnd.dao.RawMaterialDAO;
import com.capgemini.dnd.dto.DisplayRawMaterialOrder;
import com.capgemini.dnd.dto.RawMaterialOrder;
import com.capgemini.dnd.dto.RawMaterialStock;
import com.capgemini.dnd.dto.Supplier;
import com.capgemini.dnd.entity.RawMaterialOrderEntity;
import com.capgemini.dnd.util.JsonUtil;

@Service
//@Transactional(readOnly = true)
public class RawMaterialServiceImpl implements RawMaterialService {

	//RawMaterialDAO rawMaterialDAO = new RawMaterialDAOImpl();
	
	@Autowired
	private RawMaterialDAO rawMaterialDAO;
	public String placeRawMaterialOrder(RawMaterialOrder newRawMaterialOrder) throws RMOrderNotAddedException, ConnectionException, SQLException, DisplayException{
		
		if (rawMaterialDAO.addRawMaterialOrder(newRawMaterialOrder))
			return (JsonUtil.convertJavaToJson("Raw Material Order placed successfully"));
		return (JsonUtil.convertJavaToJson("0 rows updated"));
	}

	public String updateStatusRawMaterialOrder(String orderId, String newStatus) throws Exception {
		return (JsonUtil.convertJavaToJson(rawMaterialDAO.updateStatusRawMaterialOrder(orderId, newStatus)));
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

	public String displayRawmaterialOrders(DisplayRawMaterialOrder displayRawMaterialOrderObject) throws DisplayException, BackEndException {
		
		List<RawMaterialOrderEntity> rmoList2 = new ArrayList<RawMaterialOrderEntity>();
		rmoList2 = rawMaterialDAO.displayRawmaterialOrders(displayRawMaterialOrderObject);
		String jsonMessage = JsonUtil.convertJavaToJson1(rmoList2);
		return jsonMessage;
		
	}
	
	
	
	@Override
	public String trackRawMaterialOrder(RawMaterialStock rawMaterialStock) {
		String message = rawMaterialDAO.trackRawMaterialOrder(rawMaterialStock);

		String jsonMessage = JsonUtil.convertJavaToJson(message);
		return jsonMessage;
	}

	@Override
	public boolean doesRawMaterialOrderIdExist(String id) throws RMOrderIDDoesNotExistException {
		return rawMaterialDAO.doesRawMaterialOrderIdExist(id);
	}

	@Override
	public boolean processDateCheck(RawMaterialStock rawMaterialStock) throws ProcessDateException, IncompleteDataException {
		return rawMaterialDAO.processDateCheck(rawMaterialStock);
	}

	@Override
	public String updateProcessDateinStock(RawMaterialStock rawMaterialStock) {
		String message = rawMaterialDAO.updateProcessDateinStock(rawMaterialStock);

		String jsonMessage = JsonUtil.convertJavaToJson(message);
		return jsonMessage;
	}
	
	@Override
	public boolean validateManufacturingDate(Date manufacturing_date) throws ManufacturingDateException {
		Date today = new Date();
		if (manufacturing_date.before(today)) {
			return true;
		}
		throw new ManufacturingDateException("You cant enter a future manufacturing date");
	}

	@Override
	public boolean validateExpiryDate(Date manufacturing_date, Date expiry_date) throws ExpiryDateException {
		if (expiry_date.after(manufacturing_date))
			return true;
		throw new ExpiryDateException("You cant enter expiry date before manufacturing date");

	}

	@Override
	public String updateRawMaterialStock(RawMaterialStock rawMaterialStock) {
		String message = rawMaterialDAO.updateRawMaterialStock(rawMaterialStock);
		String jsonMessage = JsonUtil.convertJavaToJson(message);
		return jsonMessage;
	}
	
	@Override
	public boolean doesRawMaterialOrderIdExistInStock(String orderId) {
		return rawMaterialDAO.doesRawMaterialOrderIdExistInStock(orderId);
	}

	
	
	
	
}
