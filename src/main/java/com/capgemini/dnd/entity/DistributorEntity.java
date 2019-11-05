package com.capgemini.dnd.entity;

import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


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
	@Column(name = "address")
	private String address;
	@Column(name = "email")
	private String emailId;
	@Column(name = "PhoneNumber")
	private String phoneNo;
	
  public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getAddress() {
		return address;
	}



	public void setAddress(String address) {
		this.address = address;
	}



	public String getEmailId() {
		return emailId;
	}



	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}




	@Override
	public String toString() {
		return "DistributorEntity [distributorId=" + distributorId + ", name=" + name + ", addressId=" + addressId
				+ ", emailId=" + emailId + ", phoneNo=" + phoneNo +  "]";
	}
}
