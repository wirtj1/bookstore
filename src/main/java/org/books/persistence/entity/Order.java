package org.books.persistence.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.books.persistence.enumeration.OrderStatus;

@Entity
@Table(name = "BOOK_ORDER")
@NamedQuery(name = "searchOrders",
		query = "SELECT NEW org.books.persistence.dto.OrderInfo(o.number, o.date, o.amount, o.status) "
		+ "FROM Order o WHERE o.customer = :customer AND o.date >= :startDate AND o.date < :endDate")
public class Order implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "NUMBER")
	private Long number;
	@Column(name = "ORDER_DATE", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;
	@Column(name = "AMOUNT", precision = 7, scale = 2, nullable = false)
	private BigDecimal amount;
	@Column(name = "STATUS", nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "CUSTOMER_NR")
	private Customer customer;
	@Embedded
	private Address address;
	@Embedded
	private CreditCard creditCard;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "ORDER_NR")
	private List<OrderItem> items = new ArrayList<>();

	public Order() {
	}

	public Order(Date date, BigDecimal amount, OrderStatus status,
			Customer customer, Address address, CreditCard creditCard, List<OrderItem> items) {
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

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
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

	public void setCreditCard(CreditCard card) {
		this.creditCard = card;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}
}
