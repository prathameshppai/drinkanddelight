package com.capgemini.dnd.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

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
import com.capgemini.dnd.dao.ProductDAO;
import com.capgemini.dnd.dao.ProductDAOImpl;
import com.capgemini.dnd.dto.DisplayProductOrder;
import com.capgemini.dnd.dto.DisplayRawMaterialOrder;
import com.capgemini.dnd.dto.Distributor;
import com.capgemini.dnd.dto.ProductOrder;
import com.capgemini.dnd.dto.ProductStock;
import com.capgemini.dnd.dto.RawMaterialOrder;
import com.capgemini.dnd.util.JsonUtil;

public class ProductServiceImpl implements ProductService {

	Scanner scanner = new Scanner(System.in);

	ProductDAO productDAO = new ProductDAOImpl();

	public String fetchCompleteDistributorDetail(Distributor distributor)
			throws BackEndException, DoesNotExistException {
		distributor.setAddress(productDAO.fetchAddress(distributor));
		Distributor distributorObject = productDAO.fetchDistributorDetail(distributor); 
		String jsonMessage = JsonUtil.convertJavaToJson1(distributorObject);
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
//
//	public boolean placeProductOrder(ProductOrder newProductOrder)
//			throws ConnectionException, SQLException, ProductOrderNotAddedException {
//		return (productDAO.addProductOrder(newProductOrder));
//	}

	public String placeProductOrder(ProductOrder newProductOrder) throws ProductOrderNotAddedException, ConnectionException, SQLException, DisplayException{
		if (productDAO.addProductOrder(newProductOrder))
			return (JsonUtil.convertJavaToJson("Product Order placed successfully"));
		return (JsonUtil.convertJavaToJson("0 rows updated"));
	}

	@Override
	public List<ProductOrder> displayOrdersFromDistributor(String distId) throws Exception {
		return (productDAO.displayOrdersFromDistributor(distId));
	}

	public boolean doesProductNameExist(String name)
			throws ProductNameDoesNotExistException, ConnectionException, SQLException {
		return (productDAO.doesProductNameExist(name));
	}

	public boolean doesProductOrderIdExist(String orderId)
			throws ProductOrderIDDoesNotExistException, ConnectionException, SQLException {
		return (productDAO.doesProductOrderIdExist(orderId));
	}

	public boolean doesProductIdExist(String orderId, String name)
			throws ProductIDDoesNotExistException, ConnectionException, SQLException {
		return (productDAO.doesProductIdExist(orderId, name));
	}

	public boolean doesDistributorIdExist(String distId)
			throws DistributorIDDoesNotExistException, ConnectionException, SQLException {
		return (productDAO.doesDistributorIdExist(distId));
	}

	@Override
	public boolean doesWIdExist(String WId) throws WIdDoesNotExistException, ConnectionException, SQLException {
		return (productDAO.doesWIdExist(WId));
	}

	@Override
	public String trackProductOrder(ProductStock productStock) {
		String message = productDAO.trackProductOrder(productStock);
//		List<String> list = new ArrayList<String>();
//		list.add(message);
//		list.add("Data inserted");
//		System.out.println(list);
		String jsonMessage = JsonUtil.convertJavaToJson(message);
		return jsonMessage;
	}

	@Override
	public boolean exitDateCheck(ProductStock productStock)
			throws ExitDateException, SQLException, ConnectionException {
		return productDAO.exitDateCheck(productStock);
	}

	@Override
	public String updateExitDateinStock(ProductStock productStock) {
		String message = productDAO.updateExitDateinStock(productStock);

		String jsonMessage = JsonUtil.convertJavaToJson(message);
		return jsonMessage;

	}

	@Override
	public String updateProductStock(ProductStock productStock) {
		String message = productDAO.updateProductStock(productStock);
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
    public String displayProductOrders(DisplayProductOrder displayProductOrderObject) throws Exception {
        List<ProductOrder> poList2 = new ArrayList<ProductOrder>();
        poList2 = productDAO.displayProductOrders(displayProductOrderObject);
        String jsonMessage = JsonUtil.convertJavaToJson1(poList2);
        return jsonMessage;
    }

	
//	public static void main(String[] args) throws BackEndException, DoesNotExistException {
//		Distributor distributor=new Distributor();
//		distributor.setDistributorId("d001");
//		ProductService pd=new ProductServiceImpl();
//		distributor=pd.fetchCompleteDistributorDetail(distributor);
//		System.out.println(distributor);
//	}

	@Override
	public ArrayList<String> fetchProductNames() throws DisplayException, ConnectionException {
//		return(JsonUtil.convertJavaToJson1(productDAO.getProductNames()));
		return(productDAO.getProductNames());
		
	}

	@Override
	public ArrayList<String> fetchDistributorIds() throws DisplayException, ConnectionException {
//		return(JsonUtil.convertJavaToJson1(productDAO.getDistributorIds()));
		return(productDAO.getDistributorIds());
	}
	
	@Override
	public ArrayList<String> fetchWarehouseIds() throws DisplayException, ConnectionException {
//		return(JsonUtil.convertJavaToJson1(productDAO.getWarehouseIds()));
		return(productDAO.getWarehouseIds());
	}
}
