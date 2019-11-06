package com.capgemini.dnd.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.capgemini.dnd.customexceptions.BackEndException;
import com.capgemini.dnd.customexceptions.ConnectionException;
import com.capgemini.dnd.customexceptions.DisplayException;
import com.capgemini.dnd.customexceptions.DoesNotExistException;
import com.capgemini.dnd.customexceptions.IncompleteDataException;
import com.capgemini.dnd.customexceptions.ProcessDateException;
import com.capgemini.dnd.customexceptions.RMOrderIDDoesNotExistException;
import com.capgemini.dnd.customexceptions.RMOrderNotAddedException;
import com.capgemini.dnd.dto.DisplayRawMaterialOrder;
import com.capgemini.dnd.dto.RawMaterialOrder;
import com.capgemini.dnd.dto.RawMaterialStock;
import com.capgemini.dnd.dto.Supplier;
import com.capgemini.dnd.entity.RawMaterialOrderEntity;
import com.capgemini.dnd.entity.SupplierEntity;

public interface RawMaterialDAO {


	
	public boolean addRawMaterialOrder(RawMaterialOrder newRawMaterialOrder)  throws RMOrderNotAddedException, ConnectionException, SQLException, DisplayException;

	public String updateStatusRawMaterialOrder(String oid, String newStatus) throws Exception;
   public List<SupplierEntity> fetchSupplierDetail(Supplier supplierDetails) throws BackEndException, DoesNotExistException, DisplayException;

	public List<RawMaterialOrderEntity> displayRawmaterialOrders(DisplayRawMaterialOrder displayRawMaterialOrderObject) throws DisplayException, BackEndException;

	public ArrayList<String> getRawMaterialNames() throws DisplayException, ConnectionException;

	public ArrayList<String> getSupplierIds() throws DisplayException, ConnectionException;

	public ArrayList<String> getWarehouseIds() throws DisplayException, ConnectionException;
	
	public boolean doesRawMaterialOrderIdExist(String orderId) throws RMOrderIDDoesNotExistException;

	public String trackRawMaterialOrder(RawMaterialStock rawMaterialStock);

	public boolean processDateCheck(RawMaterialStock rawMaterialStock) throws ProcessDateException, IncompleteDataException;

	public String updateProcessDateinStock(RawMaterialStock rawMaterialStock);

	public String updateRawMaterialStock(RawMaterialStock rawMaterialStock);

	public boolean doesRawMaterialOrderIdExistInStock(String orderId);

}