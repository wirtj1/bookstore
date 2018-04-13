package org.books.persistence.repository;

import org.books.persistence.dto.CustomerInfo;
import org.books.persistence.entity.Customer;

import java.util.List;
import javax.ejb.Remote;

@Remote
public interface CustomerRepositoryRemote extends RepositoryRemote<Customer> {

	public Customer find(Long number);

	public Customer find(String email);

	public List<CustomerInfo> search(String name);
}
