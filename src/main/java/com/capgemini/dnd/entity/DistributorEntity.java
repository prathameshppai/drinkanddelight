package com.capgemini.dnd.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.capgemini.dnd.dto.Address;

@Entity
@Table(name = "Distributor")
public class DistributorEntity {

	public DistributorEntity() {
	
	}
	
	@Id
    @Column(name = "distributorId")
	private String distributorId;
	@Column(name = "distributorName")
	private String name;
	@Column(name = "addressId")
	private int addressId;
	@Column(name = "email")
	private String emailId;
	@Column(name = "PhoneNumber")
	private String phoneNo;
	@Column(name = "AddressIdFK")
	private String addressIdFK;
	
  public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public int getAddressId() {
		return addressId;
	}



	public void setAddressId(int addressId) {
		this.addressId = addressId;
	}



	public String getEmailId() {
		return emailId;
	}



	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}



	public String getPhoneNo() {
		return phoneNo;
	}



	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}



	public String getAddressIdFK() {
		return addressIdFK;
	}



	public void setAddressIdFK(String addressIdFK) {
		this.addressIdFK = addressIdFK;
	}



	public void setDistributorId(String distributorId) {
		this.distributorId = distributorId;
	}



	@Override
	public String toString() {
		return "DistributorEntity [distributorId=" + distributorId + ", name=" + name + ", addressId=" + addressId
				+ ", emailId=" + emailId + ", phoneNo=" + phoneNo + ", addressIdFK=" + addressIdFK + "]";
	}
	}