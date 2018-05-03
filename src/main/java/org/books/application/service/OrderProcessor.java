package org.books.application.service;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import org.books.persistence.entity.Order;
import org.books.persistence.enumeration.OrderStatus;
import org.books.persistence.repository.OrderRepository;

import static javax.ejb.TransactionAttributeType.REQUIRED;

@MessageDriven(activationConfig = {
	@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
	@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/orderQueue")
})
@TransactionAttribute(REQUIRED)
public class OrderProcessor implements MessageListener {

	private static final Logger logger = Logger.getLogger(OrderProcessor.class.getName());

	@EJB
	private OrderRepository orderRepository;
	@Resource
	private TimerService timerService;
	@Resource(name = "orderProcessingTime")
	private Long orderProcessingTime;

	@Override
	public void onMessage(Message message) {
		try {
			Long orderNumber = new Long(((TextMessage) message).getText());
			Order order = orderRepository.find(orderNumber);
			if (order.getStatus() != OrderStatus.CANCELED) {
				logger.log(Level.INFO, "Processing order with number ''{0}''", orderNumber);
				order.setStatus(OrderStatus.PROCESSING);
				timerService.createSingleActionTimer(orderProcessingTime, new TimerConfig(orderNumber, true));
			} else {
				logger.log(Level.INFO, "Order with number ''{0}'' has been canceled", orderNumber);
			}
		} catch (JMSException ex) {
			throw new EJBException(ex);
		}
	}

	@Timeout
	public void shipOrder(Timer timer) {
		Long orderNumber = (Long) timer.getInfo();
		Order order = orderRepository.find(orderNumber);
		if (order.getStatus() != OrderStatus.CANCELED) {
			logger.log(Level.INFO, "Shipping order with number ''{0}''", orderNumber);
			order.setStatus(OrderStatus.SHIPPED);
		} else {
			logger.log(Level.INFO, "Order with number ''{0}'' has been canceled", orderNumber);
		}
	}
}
