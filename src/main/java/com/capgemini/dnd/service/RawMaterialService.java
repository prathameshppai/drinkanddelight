package com.capgemini.dnd.service;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

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
import com.capgemini.dnd.dto.DisplayRawMaterialOrder;
import com.capgemini.dnd.dto.RawMaterialOrder;
import com.capgemini.dnd.dto.RawMaterialStock;
import com.capgemini.dnd.dto.Supplier;

@Service
public interface RawMaterialService {
//	public void viewRMspec(String name);

	public List<RawMaterialOrder> displayRawMaterialOrderDetails() throws Exception;

	public List<RawMaterialOrder> displayPendingRawMaterialOrderDetails() throws  Exception;

	public List<RawMaterialOrder> displayCancelledRawMaterialOrderDetails() throws Exception;

	public List<RawMaterialOrder> displayReceivedRawMaterialOrderDetails() throws Exception;

	public String placeRawMaterialOrder(RawMaterialOrder newRawMaterialOrder) throws RMOrderNotAddedException, ConnectionException, SQLException, DisplayException;

	public String updateStatusRawMaterialOrder(String oid, String newStatus) throws Exception;

	public List<RawMaterialOrder> displayOrdersFromSupplier(String supid) throws Exception ;
	
	public List<RawMaterialOrder> displayRawmaterialOrdersbetweenDetails(Date dt1, Date dt2) throws Exception;

	public boolean doesRawMaterialOrderIdExist(String id) throws RMOrderIDDoesNotExistException, ConnectionException, SQLException;

	public String trackRawMaterialOrder(RawMaterialStock rawMaterialStock);

	public String doesRMNameExist(String name) throws RMNameDoesNotExistException, ConnectionException, SQLException;

	public boolean doesSupplierIdExist(String name) throws SupplierIDDoesNotExistException, ConnectionException, SQLException;

	public boolean doesWIdExist(String warehouseId) throws WIdDoesNotExistException, ConnectionException, SQLException;

	public boolean doesRawMaterialIdExist(String rmId, String name) throws RMIDDoesNotExistException, ConnectionException, SQLException;

	public String updateRMStock(RawMaterialStock rawMaterialStock);

	public boolean processDateCheck(RawMaterialStock rawMaterialStock) throws ProcessDateException;

	public String updateProcessDateinStock(RawMaterialStock rawMaterialStock);

	public boolean validateManufacturingDate(Date manufacturing_date) throws ManufacturingDateException;

	public boolean validateExpiryDate(Date manufacturing_date, Date expiry_date) throws ExpiryDateException;

	public List<RawMaterialOrder> displayDispatchedRawMaterialOrderDetails() throws Exception;
	

	public String fetchSupplierDetail(Supplier supplier) throws BackEndException, DoesNotExistException;

	public String displayRawmaterialOrders(DisplayRawMaterialOrder displayRawMaterialOrderObject) throws DisplayException, BackEndException;

	public ArrayList<String> fetchRawMaterialNames() throws DisplayException, ConnectionException ;

	public ArrayList<String> fetchSupplierIds() throws DisplayException, ConnectionException;

	public ArrayList<String> fetchWarehouseIds() throws DisplayException, ConnectionException;

	
}

