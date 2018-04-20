package org.books.application.service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.jms.*;

import org.books.application.dto.PurchaseOrder;
import org.books.application.dto.PurchaseOrderItem;
import org.books.application.dto.SalesOrder;
import org.books.application.exception.BookNotFoundException;
import org.books.application.exception.CustomerNotFoundException;
import org.books.application.exception.OrderAlreadyShippedException;
import org.books.application.exception.OrderNotFoundException;
import org.books.application.exception.PaymentFailedException;
import org.books.application.exception.PaymentFailedException.Code;
import org.books.persistence.dto.CustomerInfo;
import org.books.persistence.dto.OrderInfo;
import org.books.persistence.entity.Book;
import org.books.persistence.entity.CreditCard;
import org.books.persistence.entity.Customer;
import org.books.persistence.entity.Order;
import org.books.persistence.entity.OrderItem;
import org.books.persistence.enumeration.OrderStatus;
import org.books.persistence.repository.OrderRepository;

import static javax.ejb.TransactionAttributeType.REQUIRED;

@LocalBean
@Stateless(name = "OrderService")
@TransactionAttribute(REQUIRED)
public class OrderService implements OrderServiceRemote {

	private static final Logger logger = Logger.getLogger(OrderService.class.getName());

	@EJB
	private CatalogService catalogService;
	@EJB
	private CustomerService customerService;
	@EJB
	private OrderRepository orderRepository;

	@Resource(name = "creditCardNumberPattern")
	private String creditCardNumberPattern;
	@Resource(name = "creditCardPaymentLimit")
	private Long creditCardPaymentLimit;

	@Inject
	@JMSConnectionFactory("jms/connectionFactory")
	private JMSContext jmsContext;

	@Resource(lookup="jms/orderQueue")
	private Queue orderqueue;

	@Override
	public SalesOrder placeOrder(PurchaseOrder purchaseOrder)
			throws CustomerNotFoundException, BookNotFoundException, PaymentFailedException {
		logger.log(Level.INFO, "Placing order for customer with number ''{0}''", purchaseOrder.getCustomerNr());
		Order order = createOrder(purchaseOrder);
		makePayment(order);
		processOrder(order);
		return convertOrder(order);
	}

	@Override
	public SalesOrder findOrder(Long number) throws OrderNotFoundException {
		logger.log(Level.INFO, "Finding order with number ''{0}''", number);
		Order order = orderRepository.find(number);
		if (order == null) {
			logger.log(Level.INFO, "Order not found");
			throw new OrderNotFoundException();
		}
		return convertOrder(order);
	}

	@Override
	public List<OrderInfo> searchOrders(Long customerNr, Integer year) throws CustomerNotFoundException {
		logger.log(Level.INFO, "Searching orders of customer with number ''{0}''", customerNr);
		Customer customer = customerService.findCustomer(customerNr);
		return orderRepository.search(customer, year);
	}

	@Override
	public void cancelOrder(Long number) throws OrderNotFoundException, OrderAlreadyShippedException {
		logger.log(Level.INFO, "Canceling order with number ''{0}''", number);
		Order order = orderRepository.find(number);
		if (order == null) {
			logger.log(Level.INFO, "Order not found");
			throw new OrderNotFoundException();
		}
		if (order.getStatus() == OrderStatus.SHIPPED) {
			logger.log(Level.INFO, "Order already shipped");
			throw new OrderAlreadyShippedException();
		}
		order.setStatus(OrderStatus.CANCELED);
	}

	private Order createOrder(PurchaseOrder purchaseOrder)
			throws CustomerNotFoundException, BookNotFoundException {
		Customer customer = customerService.findCustomer(purchaseOrder.getCustomerNr());
		Order order = new Order();
		Calendar calendar = Calendar.getInstance();
		order.setDate(calendar.getTime());
		order.setStatus(OrderStatus.ACCEPTED);
		order.setCustomer(customer);
		order.setAddress(customer.getAddress());
		order.setCreditCard(customer.getCreditCard());
		BigDecimal totalPrice = BigDecimal.ZERO;
		for (PurchaseOrderItem item : purchaseOrder.getItems()) {
			Book book = catalogService.findBook(item.getIsbn());
			order.getItems().add(new OrderItem(book, item.getQuantity(), book.getPrice()));
			totalPrice = totalPrice.add(book.getPrice().multiply(new BigDecimal(item.getQuantity())));
		}
		order.setAmount(totalPrice);
		return orderRepository.persist(order);
	}

	private void makePayment(Order order) throws PaymentFailedException {
		logger.log(Level.INFO, "Making payment for customer with number ''{0}''", order.getCustomer().getNumber());
		CreditCard creditCard = order.getCustomer().getCreditCard();
		if (!creditCard.getNumber().matches(creditCardNumberPattern)) {
			logger.log(Level.INFO, "Invalid credit card");
			throw new PaymentFailedException(Code.INVALID_CREDIT_CARD);
		}
		Calendar calendar = Calendar.getInstance();
		if (creditCard.getExpirationYear() < calendar.get(Calendar.YEAR)
				|| (creditCard.getExpirationYear() == calendar.get(Calendar.YEAR)
				&& creditCard.getExpirationMonth() < calendar.get(Calendar.MONTH) + 1)) {
			logger.log(Level.INFO, "Credit card expired");
			throw new PaymentFailedException(Code.CREDIT_CARD_EXPIRED);
		}
		if (order.getAmount().compareTo(BigDecimal.valueOf(creditCardPaymentLimit)) > 0) {
			throw new PaymentFailedException(Code.PAYMENT_LIMIT_EXCEEDED);
		}
	}

	private SalesOrder convertOrder(Order order) {
		return new SalesOrder(order.getNumber(), order.getDate(), order.getAmount(), order.getStatus(),
				convertCustomer(order.getCustomer()), order.getAddress(), order.getCreditCard(), order.getItems());
	}

	private CustomerInfo convertCustomer(Customer customer) {
		return new CustomerInfo(customer.getNumber(), customer.getFirstName(), customer.getLastName(), customer.getEmail());
	}

	private void processOrder(Order order){
		JMSProducer producer = jmsContext.createProducer();
		Message msg = jmsContext.createTextMessage(order.getOrderString());
		producer.send(orderqueue, msg);
	}
}
