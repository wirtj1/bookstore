package org.books.persistence.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.books.persistence.enumeration.OrderStatus;

public class OrderInfo implements Serializable {

	private Long number;
	private Date date;
	private BigDecimal amount;
	private OrderStatus status;

	public OrderInfo() {
	}

	public OrderInfo(Long number, Date date, BigDecimal amount, OrderStatus status) {
		this.number = number;
		this.date = date;
		this.amount = amount;
		this.status = status;
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
}
