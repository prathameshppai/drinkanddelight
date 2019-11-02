package com.capgemini.dnd.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.capgemini.dnd.customexceptions.BackEndException;
import com.capgemini.dnd.customexceptions.ConnectionException;
import com.capgemini.dnd.customexceptions.DisplayException;
import com.capgemini.dnd.customexceptions.DoesNotExistException;
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
import com.capgemini.dnd.entity.RawMaterialOrderEntity;

public interface RawMaterialDAO {



	public List<RawMaterialOrder> displayPendingRawMaterialOrderDetails() throws Exception;

	public List<RawMaterialOrder> displayCancelledRawMaterialOrderDetails() throws Exception;

	public List<RawMaterialOrder> displayReceivedRawMaterialOrderDetails() throws Exception;
	
	public boolean addRawMaterialOrder(RawMaterialOrder newRawMaterialOrder)  throws RMOrderNotAddedException, ConnectionException, SQLException, DisplayException;

	public String updateStatusRawMaterialOrder(String oid, String newStatus) throws Exception;
	
	public List<RawMaterialOrder> displayOrdersFromSupplier(String supid) throws Exception ;
	
	public List<RawMaterialOrder> displayRawmaterialOrdersbetweenDetails(Date dt1, Date dt2) throws Exception;

	public List<RawMaterialOrder> displayRawMaterialOrderDetails() throws Exception;

	public List<RawMaterialOrder> displayDispatchedRawMaterialOrderDetails() throws Exception;

	public Supplier fetchSupplierDetail(Supplier supplierDetails) throws BackEndException, DoesNotExistException;

	public List<RawMaterialOrderEntity> displayRawmaterialOrders(DisplayRawMaterialOrder displayRawMaterialOrderObject) throws DisplayException, BackEndException;

	public ArrayList<String> getRawMaterialNames() throws DisplayException, ConnectionException;

	public ArrayList<String> getDistributorIds() throws DisplayException, ConnectionException;

	public ArrayList<String> getWarehouseIds() throws DisplayException, ConnectionException;
	
	
	public boolean doesRawMaterialOrderIdExist(String orderId) throws RMOrderIDDoesNotExistException;

	public String trackRawMaterialOrder(RawMaterialStock rawMaterialStock);

	public boolean processDateCheck(RawMaterialStock rawMaterialStock) throws ProcessDateException;

	public String updateProcessDateinStock(RawMaterialStock rawMaterialStock);

	public String updateRawMaterialStock(RawMaterialStock rawMaterialStock);

	public boolean doesRawMaterialOrderIdExistInStock(String orderId);

}