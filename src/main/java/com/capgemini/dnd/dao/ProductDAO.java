package com.capgemini.dnd.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.capgemini.dnd.customexceptions.BackEndException;
import com.capgemini.dnd.customexceptions.ConnectionException;
import com.capgemini.dnd.customexceptions.DisplayException;
import com.capgemini.dnd.customexceptions.DoesNotExistException;
import com.capgemini.dnd.customexceptions.ExitDateException;
import com.capgemini.dnd.customexceptions.IncompleteDataException;
import com.capgemini.dnd.customexceptions.ProductOrderIDDoesNotExistException;
import com.capgemini.dnd.customexceptions.ProductOrderNotAddedException;
import com.capgemini.dnd.dto.Address;
import com.capgemini.dnd.dto.DisplayProductOrder;
import com.capgemini.dnd.dto.Distributor;
import com.capgemini.dnd.dto.ProductOrder;
import com.capgemini.dnd.dto.ProductStock;
import com.capgemini.dnd.entity.DistributorEntity;
import com.capgemini.dnd.entity.ProductOrdersEntity;

public interface ProductDAO {

	public List<ProductOrder> displayProductOrderDetails() throws Exception;

	public String updateStatusProductOrder(String oid, String newStatus) throws Exception;

	public List<ProductOrder> displayPendingProductOrderDetails() throws Exception;

	public List<ProductOrder> displayCancelledProductOrderDetails() throws Exception;

	public List<ProductOrder> displayReceivedProductOrderDetails() throws Exception;

	public boolean addProductOrder(ProductOrder ProductOrderobject) throws ConnectionException, SQLException, ProductOrderNotAddedException, DisplayException;

	public List<ProductOrder> displayDispatchedProductOrderDetails() throws Exception;

	public List<ProductOrder> displayProductOrderbetweenDetails(Date dt1, Date dt2) throws Exception;

	public List<ProductOrder> displayOrdersFromDistributor(String distId) throws Exception;// check111

	public List<DistributorEntity> fetchDistributorDetail(Distributor distributor) throws BackEndException, DoesNotExistException, DisplayException;
	
	public Address fetchAddress(Distributor distributor) throws BackEndException, DoesNotExistException;

	public List<ProductOrdersEntity> displayProductOrders(DisplayProductOrder displayProductOrderObject) throws Exception;

	public ArrayList<String> getProductNames() throws DisplayException, ConnectionException;

	public ArrayList<String> getDistributorIds() throws DisplayException, ConnectionException;

	public ArrayList<String> getWarehouseIds() throws DisplayException, ConnectionException;
	
	public boolean doesProductOrderIdExist(String orderId) throws ProductOrderIDDoesNotExistException;

	public String trackProductOrder(ProductStock productStock);

	public boolean exitDateCheck(ProductStock productStock) throws ExitDateException, IncompleteDataException;

	public String updateExitDateinStock(ProductStock productStock);

	public String updateProductStock(ProductStock productStock);

	boolean doesProductOrderIdExistInStock(String orderId);

}
