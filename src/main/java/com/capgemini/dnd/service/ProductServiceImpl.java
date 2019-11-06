package com.capgemini.dnd.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.capgemini.dnd.customexceptions.BackEndException;
import com.capgemini.dnd.customexceptions.ConnectionException;
import com.capgemini.dnd.customexceptions.DisplayException;
import com.capgemini.dnd.customexceptions.DoesNotExistException;
import com.capgemini.dnd.customexceptions.ExitDateException;
import com.capgemini.dnd.customexceptions.ExpiryDateException;
import com.capgemini.dnd.customexceptions.IncompleteDataException;
import com.capgemini.dnd.customexceptions.ManufacturingDateException;
import com.capgemini.dnd.customexceptions.ProductOrderIDDoesNotExistException;
import com.capgemini.dnd.customexceptions.ProductOrderNotAddedException;
import com.capgemini.dnd.dao.ProductDAO;
import com.capgemini.dnd.dto.DisplayProductOrder;
import com.capgemini.dnd.dto.Distributor;
import com.capgemini.dnd.dto.ProductOrder;
import com.capgemini.dnd.dto.ProductStock;
import com.capgemini.dnd.entity.DistributorEntity;
import com.capgemini.dnd.entity.ProductOrdersEntity;
import com.capgemini.dnd.util.JsonUtil;

@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

	Scanner scanner = new Scanner(System.in);

	@Autowired
	private ProductDAO productDAO;

	public String fetchCompleteDistributorDetail(Distributor distributor)
			throws BackEndException, DoesNotExistException, DisplayException {
//		distributor.setAddress(productDAO.fetchAddress(distributor));
		List<DistributorEntity> distributorList = productDAO.fetchDistributorDetail(distributor); 
		String jsonMessage = JsonUtil.convertJavaToJson1(distributorList);
		return jsonMessage;
	}
	public String updateStatusProductOrder(String oid, String newStatus) throws Exception {
		return JsonUtil.convertJavaToJson(productDAO.updateStatusProductOrder(oid, newStatus));
	}

	@Override
	public List<ProductOrder> displayProductOrderDetails() throws Exception {
		return (productDAO.displayProductOrderDetails());
	}

	@Override
	public List<ProductOrder> displayPendingProductOrderDetails() throws Exception {
		return (productDAO.displayPendingProductOrderDetails());
	}

	@Override
	public List<ProductOrder> displayReceivedProductOrderDetails() throws Exception {
		return (productDAO.displayReceivedProductOrderDetails());
	}

	@Override
	public List<ProductOrder> displayCancelledProductOrderDetails() throws Exception {
		return (productDAO.displayCancelledProductOrderDetails());
	}

	@Override
	public List<ProductOrder> displayDispatchedProductOrderDetails() throws Exception {
		return (productDAO.displayDispatchedProductOrderDetails());
	}

	@Override
	public List<ProductOrder> displayProductOrderbetweenDetails(Date dt1, Date dt2) throws Exception {
		return (productDAO.displayProductOrderbetweenDetails(dt1, dt2));
	}

	@Override
	public String placeProductOrder(ProductOrder newProductOrder) throws ProductOrderNotAddedException, ConnectionException, SQLException, DisplayException{
		if (productDAO.addProductOrder(newProductOrder))
			return (JsonUtil.convertJavaToJson("Product Order placed successfully"));
		return (JsonUtil.convertJavaToJson("0 rows updated"));
	}

	@Override
	public List<ProductOrder> displayOrdersFromDistributor(String distId) throws Exception {
		return (productDAO.displayOrdersFromDistributor(distId));
	}

	@Override
    public String displayProductOrders(DisplayProductOrder displayProductOrderObject) throws Exception {
        List<ProductOrdersEntity> poList2 = new ArrayList<ProductOrdersEntity>();
        poList2 = productDAO.displayProductOrders(displayProductOrderObject);
        String jsonMessage = JsonUtil.convertJavaToJson1(poList2);
        return jsonMessage;
    }

	@Override
	public ArrayList<String> fetchProductNames() throws DisplayException, ConnectionException {
		return(productDAO.getProductNames());
		
	}

	@Override
	public ArrayList<String> fetchDistributorIds() throws DisplayException, ConnectionException {
		return(productDAO.getDistributorIds());
	}
	
	@Override
	public ArrayList<String> fetchWarehouseIds() throws DisplayException, ConnectionException {
		return(productDAO.getWarehouseIds());
	}
	
	
	@Override
	public String trackProductOrder(ProductStock productStock) {
		String message = productDAO.trackProductOrder(productStock);
		String jsonMessage = JsonUtil.convertJavaToJson(message);
		return jsonMessage;
	}

	@Override
	public boolean doesProductOrderIdExist(String id) throws ProductOrderIDDoesNotExistException {
		return productDAO.doesProductOrderIdExist(id);
	}

	@Override
	public boolean exitDateCheck(ProductStock productStock) throws ExitDateException, IncompleteDataException {
		return productDAO.exitDateCheck(productStock);
	}

	@Override
	public String updateExitDateinStock(ProductStock productStock) {
		String message = productDAO.updateExitDateinStock(productStock);

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
	public String updateProductStock(ProductStock productStock) {
		String message = productDAO.updateProductStock(productStock);
		String jsonMessage = JsonUtil.convertJavaToJson(message);
		return jsonMessage;
	}

	@Override
	public boolean doesProductOrderIdExistInStock(String orderId) {
		return productDAO.doesProductOrderIdExistInStock(orderId);
	}
}
