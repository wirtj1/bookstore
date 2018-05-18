package org.books.presentation.rest;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import org.books.application.dto.PurchaseOrder;
import org.books.application.dto.PurchaseOrderItem;
import org.books.application.dto.SalesOrder;
import org.books.persistence.dto.OrderInfo;
import org.books.persistence.entity.Address;
import org.books.persistence.entity.Book;
import org.books.persistence.entity.CreditCard;
import org.books.persistence.entity.Customer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static org.books.persistence.enumeration.CreditCardType.MASTERCARD;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class ResourcesIT {

	private static final String RESOURCE_BASE_URL = "http://localhost:8080/bookstore/rest/";

	private WebTarget catalogTarget;
	private WebTarget customersTarget;
	private WebTarget ordersTarget;

	private Customer customer;
	private List<PurchaseOrderItem> items;
	private SalesOrder order;

	@BeforeClass
	public void lookupResources() {
		Client client = ClientBuilder.newBuilder().build();
		catalogTarget = client.target(RESOURCE_BASE_URL + "books");
		customersTarget = client.target(RESOURCE_BASE_URL + "customers");
		ordersTarget = client.target(RESOURCE_BASE_URL + "orders");
	}

	@BeforeClass
	public void initData() {
		customer = new Customer("Alice", "Smith", "alice@example.org", "alice",
				new Address("123 Maple Street", "Mill Valley", "CA-90952", "US"),
				new CreditCard(MASTERCARD, "5400000000000005", 01, 2020));
		items = Arrays.asList(new PurchaseOrderItem("143024626X", 1), new PurchaseOrderItem("1449370179", 1));
	}

	@Test
	public void findBook() throws Exception {
		assertNotNull(catalogTarget.path("143024626X").request(APPLICATION_JSON).get(Book.class));
	}

	@Test
	public void searchBooks() throws Exception {
		GenericType<List<Book>> type = new GenericType<List<Book>>() {
		};
		assertFalse(catalogTarget.queryParam("keywords", "java ee").request(APPLICATION_JSON).get(type).isEmpty());
		assertFalse(catalogTarget.queryParam("keywords", "java ee goncalves").request(APPLICATION_JSON)
				.get(type).isEmpty());
		assertFalse(catalogTarget.queryParam("keywords", "java ee o'reilly").request(APPLICATION_JSON)
				.get(type).isEmpty());
		assertTrue(catalogTarget.queryParam("keywords", "java ee goncalves o'reilly").request(APPLICATION_JSON)
				.get(type).isEmpty());
	}

	@Test
	public void registerCustomer() throws Exception {
		Long number = customersTarget.request(TEXT_PLAIN).post(Entity.entity(customer, APPLICATION_JSON))
				.readEntity(Long.class);
		customer.setNumber(number);
		assertNotNull(customersTarget.path(String.valueOf(customer.getNumber())).request(APPLICATION_JSON).get());
		assertNotNull(customersTarget.queryParam("email", customer.getEmail()).request(APPLICATION_JSON).get());
	}

	@Test(dependsOnMethods = {"registerCustomer"})
	public void placeOrder() throws Exception {
		PurchaseOrder purchaseOrder = new PurchaseOrder(customer.getNumber(), items);
		order = ordersTarget.request().post(Entity.entity(purchaseOrder, APPLICATION_JSON))
				.readEntity(SalesOrder.class);
		assertNotNull(ordersTarget.path(String.valueOf(order.getNumber())).request(APPLICATION_JSON).get());
	}

	@Test(dependsOnMethods = "placeOrder")
	public void searchOrders() throws Exception {
		GenericType<List<OrderInfo>> type = new GenericType<List<OrderInfo>>() {
		};
		Integer year = Calendar.getInstance().get(Calendar.YEAR);
		assertTrue(ordersTarget.queryParam("customerNr", customer.getNumber()).queryParam("year", year - 1)
				.request(APPLICATION_JSON).get(type).isEmpty());
		assertFalse(ordersTarget.queryParam("customerNr", customer.getNumber()).queryParam("year", year)
				.request(APPLICATION_JSON).get(type).isEmpty());
		assertTrue(ordersTarget.queryParam("customerNr", customer.getNumber()).queryParam("year", year + 1)
				.request(APPLICATION_JSON).get(type).isEmpty());
	}
}
