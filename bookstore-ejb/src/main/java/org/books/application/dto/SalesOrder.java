package org.books.application.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.books.persistence.dto.CustomerInfo;
import org.books.persistence.entity.Address;
import org.books.persistence.entity.CreditCard;
import org.books.persistence.entity.OrderItem;
import org.books.persistence.enumeration.OrderStatus;

public class SalesOrder implements Serializable {

	private Long number;
	private Date date;
	private BigDecimal amount;
	private OrderStatus status;
	private CustomerInfo customer;
	private Address address;
	private CreditCard creditCard;
	private List<OrderItem> items;

	public SalesOrder() {
	}

	public SalesOrder(Long number, Date date, BigDecimal amount, OrderStatus status,
			CustomerInfo customer, Address address, CreditCard creditCard, List<OrderItem> items) {
		this.number = number;
		this.date = date;
		this.amount = amount;
		this.status = status;
		this.customer = customer;
		this.address = address;
		this.creditCard = creditCard;
		this.items = items;
	}

	public Long getNumber() {
		return number;
	}

	public void setNumber(Long number) {
		this.number = number;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public CustomerInfo getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerInfo customer) {
		this.customer = customer;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}
}
