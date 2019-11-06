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
import com.capgemini.dnd.customexceptions.IncompleteDataException;
import com.capgemini.dnd.customexceptions.ManufacturingDateException;
import com.capgemini.dnd.customexceptions.ProcessDateException;
import com.capgemini.dnd.customexceptions.RMOrderIDDoesNotExistException;
import com.capgemini.dnd.customexceptions.RMOrderNotAddedException;
import com.capgemini.dnd.dto.DisplayRawMaterialOrder;
import com.capgemini.dnd.dto.RawMaterialOrder;
import com.capgemini.dnd.dto.RawMaterialStock;
import com.capgemini.dnd.dto.Supplier;


public interface RawMaterialService {
//	public void viewRMspec(String name);

	
	public String placeRawMaterialOrder(RawMaterialOrder newRawMaterialOrder) throws RMOrderNotAddedException, ConnectionException, SQLException, DisplayException;

	public String updateStatusRawMaterialOrder(String oid, String newStatus) throws Exception;
	
	public String fetchSupplierDetail(Supplier supplier) throws BackEndException, DoesNotExistException, DisplayException;

	public String displayRawmaterialOrders(DisplayRawMaterialOrder displayRawMaterialOrderObject) throws DisplayException, BackEndException;

	public ArrayList<String> fetchRawMaterialNames() throws DisplayException, ConnectionException ;

	public ArrayList<String> fetchSupplierIds() throws DisplayException, ConnectionException;

	public ArrayList<String> fetchWarehouseIds() throws DisplayException, ConnectionException;
	
	
	public String trackRawMaterialOrder(RawMaterialStock rawMaterialStock);

	public boolean doesRawMaterialOrderIdExist(String id) throws RMOrderIDDoesNotExistException;
	
	boolean doesRawMaterialOrderIdExistInStock(String orderId);

	public boolean processDateCheck(RawMaterialStock rawMaterialStock) throws ProcessDateException, IncompleteDataException;

	public String updateProcessDateinStock(RawMaterialStock rawMaterialStock);

	public boolean validateManufacturingDate(Date manufacturing_date) throws ManufacturingDateException;

	public boolean validateExpiryDate(Date manufacturing_date, Date expiry_date) throws ExpiryDateException;

	public String updateRawMaterialStock(RawMaterialStock rawMaterialStock);

	
}

