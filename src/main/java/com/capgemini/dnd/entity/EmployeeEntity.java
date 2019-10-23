package com.capgemini.dnd.entity;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table (
name = "Employee", 
	uniqueConstraints = { 
		@UniqueConstraint(columnNames = "Email"),
		@UniqueConstraint(columnNames = "Contact") 
	}
)

public class EmployeeEntity {
	@Column(name = "EmpId")
	private String empId;

	@Column(name = "Name")
	private String name;

	@Column(name = "DOB")
	private Date dateOfBirth;

	@Column(name = "Email")
	private String email;

	@Column(name = "Gender")
	private String gender;

	@Column(name = "Designation")
	private String designation;

	@Column(name = "Contact")
	private String contact;

	public EmployeeEntity() {
	}

	public EmployeeEntity(String name, Date dateOfBirth, String email, String gender, String designation,
			String contact) {
		this.name = name;
		this.dateOfBirth = dateOfBirth;
		this.email = email;
		this.gender = gender;
		this.designation = designation;
		this.contact = contact;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	@Override
	public String toString() {
		return "EmployeeEntity [empId=" + empId + ", name=" + name + ", dateOfBirth=" + dateOfBirth + ", email=" + email
				+ ", gender=" + gender + ", designation=" + designation + ", contact=" + contact + "]";
	}
	
}
