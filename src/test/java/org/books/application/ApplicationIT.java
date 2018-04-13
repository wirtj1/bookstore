package org.books.application;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.books.application.dto.PurchaseOrder;
import org.books.application.dto.PurchaseOrderItem;
import org.books.application.dto.SalesOrder;
import org.books.application.service.CatalogServiceRemote;
import org.books.application.service.CustomerServiceRemote;
import org.books.application.service.OrderServiceRemote;
import org.books.persistence.entity.Address;
import org.books.persistence.entity.Book;
import org.books.persistence.entity.CreditCard;
import org.books.persistence.entity.Customer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.books.persistence.enumeration.BookBinding.PAPERBACK;
import static org.books.persistence.enumeration.CreditCardType.MASTERCARD;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class ApplicationIT {

	private static final String JNDI_BASE_NAME = "java:global/bookstore-ejb/";

	private CatalogServiceRemote catalogService;
	private CustomerServiceRemote customerService;
	private OrderServiceRemote orderService;

	private List<Book> books;
	private Customer customer;
	private SalesOrder order;

	@BeforeClass
	public void lookupServices() throws Exception {
		Context jndiContext = new InitialContext();
		catalogService = (CatalogServiceRemote) jndiContext.lookup(
				JNDI_BASE_NAME + "CatalogService!" + CatalogServiceRemote.class.getName());
		customerService = (CustomerServiceRemote) jndiContext.lookup(
				JNDI_BASE_NAME + "CustomerService!" + CustomerServiceRemote.class.getName());
		orderService = (OrderServiceRemote) jndiContext.lookup(
				JNDI_BASE_NAME + "OrderService!" + OrderServiceRemote.class.getName());
	}

	@BeforeClass
	public void initData() {
		books = Arrays.asList(
				new Book("143024626X", "Beginning Java EE 7", "Antonio Goncalves", "Apress", 2013,
						PAPERBACK, 608, new BigDecimal("49.99")),
				new Book("1449370179", "Java EE 7 Essentials", "Arun Gupta", "O'Reilly Media", 2013,
						PAPERBACK, 362, new BigDecimal("49.99")));
		customer = new Customer("Alice", "Smith", "alice@example.org", "alice",
				new Address("123 Maple Street", "Mill Valley", "CA-90952", "US"),
				new CreditCard(MASTERCARD, "5400000000000005", 01, 2020));
	}

	@Test
	public void addBooks() throws Exception {
		for (Book book : books) {
			catalogService.addBook(book);
			assertNotNull(catalogService.findBook(book.getIsbn()));
		}
	}

	@Test
	public void searchBooks() throws Exception {
		assertFalse(catalogService.searchBooks("java ee").isEmpty());
		assertFalse(catalogService.searchBooks("java ee goncalves").isEmpty());
		assertFalse(catalogService.searchBooks("java ee o'reilly").isEmpty());
		assertTrue(catalogService.searchBooks("java ee goncalves o'reilly").isEmpty());
	}

	@Test
	public void registerCustomer() throws Exception {
		Long number = customerService.registerCustomer(customer);
		customer.setNumber(number);
		assertNotNull(customerService.findCustomer(customer.getNumber()));
		assertNotNull(customerService.findCustomer(customer.getEmail()));
	}

	@Test(dependsOnMethods = "registerCustomer")
	public void searchCustomers() throws Exception {
		assertFalse(customerService.searchCustomers("alice").isEmpty());
		assertFalse(customerService.searchCustomers("smith").isEmpty());
	}

	@Test(dependsOnMethods = {"addBooks", "registerCustomer"})
	public void placeOrder() throws Exception {
		List<PurchaseOrderItem> items = new ArrayList<>();
		for (Book book : books) {
			items.add(new PurchaseOrderItem(book.getIsbn(), 1));
		}
		order = orderService.placeOrder(new PurchaseOrder(customer.getNumber(), items));
		assertNotNull(orderService.findOrder(order.getNumber()));
	}

	@Test(dependsOnMethods = "placeOrder")
	public void searchOrders() throws Exception {
		Integer year = Calendar.getInstance().get(Calendar.YEAR);
		assertTrue(orderService.searchOrders(customer.getNumber(), year - 1).isEmpty());
		assertFalse(orderService.searchOrders(customer.getNumber(), year).isEmpty());
		assertTrue(orderService.searchOrders(customer.getNumber(), year + 1).isEmpty());
	}
}
