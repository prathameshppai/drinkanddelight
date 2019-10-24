package com.capgemini.dnd.entity;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "ProductStock")
public class ProductStockEntity {
	
	@Id
	private int orderId;
	private Date exitDate;
	private Date manufacturingDate;
	private Date expiryDate;
	private String warehouseId;
	
	

	public ProductStockEntity() {
		
	}



	public int getOrderId() {
		return orderId;
	}



	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}



	public Date getExitDate() {
		return exitDate;
	}



	public void setExitDate(Date exitDate) {
		this.exitDate = exitDate;
	}



	public Date getManufacturingDate() {
		return manufacturingDate;
	}



	public void setManufacturingDate(Date manufacturingDate) {
		this.manufacturingDate = manufacturingDate;
	}



	public String getWarehouseId() {
		return warehouseId;
	}



	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}



	@Override
	public String toString() {
		return "ProductStockEntity [orderId=" + orderId + ", exitDate=" + exitDate + ", manufacturingDate="
				+ manufacturingDate + ", warehouseId=" + warehouseId + "]";
	}



	

}
