package org.books.persistence.repository;

import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.TypedQuery;
import org.books.persistence.dto.OrderInfo;
import org.books.persistence.entity.Customer;
import org.books.persistence.entity.Order;

import static javax.ejb.TransactionAttributeType.SUPPORTS;

@Stateless
public class OrderRepository extends Repository<Order> {

	@TransactionAttribute(SUPPORTS)
	public Order find(Long number) {
		return super.find(Order.class, number);
	}

	@TransactionAttribute(SUPPORTS)
	public List<OrderInfo> search(Customer customer, Integer year) {
		TypedQuery<OrderInfo> query = entityManager.createNamedQuery("searchOrders", OrderInfo.class);
		query.setParameter("customer", customer);
		query.setParameter("startDate", new GregorianCalendar(year, 0, 1).getTime());
		query.setParameter("endDate", new GregorianCalendar(year + 1, 0, 1).getTime());
		return query.getResultList();
	}
}
