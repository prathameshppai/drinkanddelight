package com.capgemini.dnd.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.capgemini.dnd.customexceptions.BackEndException;
import com.capgemini.dnd.customexceptions.ConnectionException;
import com.capgemini.dnd.customexceptions.DisplayException;
import com.capgemini.dnd.customexceptions.DistributorIDDoesNotExistException;
import com.capgemini.dnd.customexceptions.DoesNotExistException;
import com.capgemini.dnd.customexceptions.ExitDateException;
import com.capgemini.dnd.customexceptions.ExpiryDateException;
import com.capgemini.dnd.customexceptions.ManufacturingDateException;
import com.capgemini.dnd.customexceptions.ProductIDDoesNotExistException;
import com.capgemini.dnd.customexceptions.ProductNameDoesNotExistException;
import com.capgemini.dnd.customexceptions.ProductOrderIDDoesNotExistException;
import com.capgemini.dnd.customexceptions.ProductOrderNotAddedException;
import com.capgemini.dnd.customexceptions.WIdDoesNotExistException;
import com.capgemini.dnd.dto.DisplayProductOrder;
import com.capgemini.dnd.dto.Distributor;
import com.capgemini.dnd.dto.ProductOrder;
import com.capgemini.dnd.dto.ProductStock;

public interface ProductService {

	public String fetchCompleteDistributorDetail(Distributor distributor) throws BackEndException, DoesNotExistException;
	
	public String updateStatusProductOrder(String oid, String newStatus) throws Exception;

	public List<ProductOrder> displayProductOrderDetails() throws Exception;

	public List<ProductOrder> displayPendingProductOrderDetails() throws Exception;

	public List<ProductOrder> displayCancelledProductOrderDetails() throws Exception;

	public List<ProductOrder> displayReceivedProductOrderDetails() throws Exception;

	public String placeProductOrder(ProductOrder ProductOrderobject)
			throws ProductOrderNotAddedException, ConnectionException, SQLException, DisplayException;

	public List<ProductOrder> displayDispatchedProductOrderDetails() throws Exception;

	public List<ProductOrder> displayProductOrderbetweenDetails(Date dt1, Date dt2) throws Exception;

	public List<ProductOrder> displayOrdersFromDistributor(String distid) throws Exception;

	public String displayProductOrders(DisplayProductOrder displayProductOrderObject) throws Exception;

	public ArrayList<String> fetchProductNames() throws DisplayException, ConnectionException;

	public ArrayList<String> fetchDistributorIds() throws DisplayException, ConnectionException;

	public ArrayList<String> fetchWarehouseIds() throws DisplayException, ConnectionException;
	
	public String trackProductOrder(ProductStock productStock);

	public boolean doesProductOrderIdExist(String id) throws ProductOrderIDDoesNotExistException;
	
	boolean doesProductOrderIdExistInStock(String orderId);

	public boolean exitDateCheck(ProductStock productStock) throws ExitDateException;

	public String updateExitDateinStock(ProductStock productStock);

	public boolean validateManufacturingDate(Date manufacturing_date) throws ManufacturingDateException;

	public boolean validateExpiryDate(Date manufacturing_date, Date expiry_date) throws ExpiryDateException;

	public String updateProductStock(ProductStock productStock);

}
