package org.books.persistence.dto;

import java.io.Serializable;

public class CustomerInfo implements Serializable {

	private Long number;
	private String firstName;
	private String lastName;
	private String email;

	public CustomerInfo() {
	}

	public CustomerInfo(Long number, String firstName, String lastName, String email) {
		this.number = number;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}

	public Long getNumber() {
		return number;
	}

	public void setNumber(Long number) {
		this.number = number;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
