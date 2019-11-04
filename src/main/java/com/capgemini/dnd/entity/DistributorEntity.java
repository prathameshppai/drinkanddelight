package com.capgemini.dnd.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.capgemini.dnd.util.DistributorIdentity;

@Entity
@Table(name = "Distributor")
public class DistributorEntity {

	@EmbeddedId
	private DistributorIdentity distributorCompositeId;

	@Column(name = "distributorName")
	private String distributorName;

	@Column(name = "email")
	private String email;

	@Column(name = "phoneNo")
	private String phoneNo;

	public DistributorEntity(String distributorName, String email, String phoneNo) {
		this.distributorName = distributorName;
		this.email = email;
		this.phoneNo = phoneNo;
	}

	public DistributorIdentity getDistributorCompositeId() {
		return distributorCompositeId;
	}

	public void setDistributorCompositeId(String distributorId, int addressId) {
		this.distributorCompositeId.setDistributorId(distributorId);
		this.distributorCompositeId.setAddressId(addressId);
	}

	public String getDistributorName() {
		return distributorName;
	}

	public void setDistributorName(String distributorName) {
		this.distributorName = distributorName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

}
