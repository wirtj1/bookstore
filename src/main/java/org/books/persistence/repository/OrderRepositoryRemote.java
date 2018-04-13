package org.books.persistence.repository;

import java.util.List;
import javax.ejb.Remote;
import org.books.persistence.dto.OrderInfo;
import org.books.persistence.entity.Customer;
import org.books.persistence.entity.Order;

@Remote
public interface OrderRepositoryRemote extends RepositoryRemote<Order> {

	public Order find(Long number);

	public List<OrderInfo> search(Customer customer, Integer year);
}
