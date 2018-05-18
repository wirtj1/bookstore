package org.books.application.service;

import java.util.List;
import javax.ejb.Remote;
import org.books.application.dto.PurchaseOrder;
import org.books.application.dto.SalesOrder;
import org.books.application.exception.BookNotFoundException;
import org.books.application.exception.CustomerNotFoundException;
import org.books.application.exception.OrderAlreadyShippedException;
import org.books.application.exception.OrderNotFoundException;
import org.books.application.exception.PaymentFailedException;
import org.books.persistence.dto.OrderInfo;

@Remote
public interface OrderServiceRemote {

	public SalesOrder placeOrder(PurchaseOrder order)
			throws CustomerNotFoundException, BookNotFoundException, PaymentFailedException;

	public SalesOrder findOrder(Long number) throws OrderNotFoundException;

	public List<OrderInfo> searchOrders(Long customerNr, Integer year) throws CustomerNotFoundException;

	public void cancelOrder(Long number) throws OrderNotFoundException, OrderAlreadyShippedException;
}
