package com.capgemini.dnd.util;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Table;

@Embeddable
@Table(name = "Distributor")
public class DistributorIdentity {
	@Column(name = "distributorId")
	private String distributorId;
	
	@Column(name = "addressId")
	private int addressId;

	public String getDistributorId() {
		return distributorId;
	}

	public void setDistributorId(String distributorId) {
		this.distributorId = distributorId;
	}

	public int getAddressId() {
		return addressId;
	}

	public void setAddressId(int addressId) {
		this.addressId = addressId;
	}
	
}
