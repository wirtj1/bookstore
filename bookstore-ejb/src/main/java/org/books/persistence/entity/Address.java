package org.books.persistence.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Address implements Serializable {

	@Column(name = "ADDR_STREET", nullable = false)
	private String street;
	@Column(name = "ADDR_CITY", nullable = false)
	private String city;
	@Column(name = "ADDR_POSTAL_CODE", nullable = false)
	private String postalCode;
	@Column(name = "ADDR_COUNTRY", nullable = false)
	private String country;

	public Address() {
	}

	public Address(String street, String city, String postalCode, String country) {
		this.street = street;
		this.city = city;
		this.postalCode = postalCode;
		this.country = country;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}
