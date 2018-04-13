package org.books.application.service;

import java.util.List;
import javax.ejb.Remote;
import org.books.application.exception.CustomerAlreadyExistsException;
import org.books.application.exception.CustomerNotFoundException;
import org.books.persistence.dto.CustomerInfo;
import org.books.persistence.entity.Customer;

@Remote
public interface CustomerServiceRemote {

	public Long registerCustomer(Customer customer) throws CustomerAlreadyExistsException;

	public Customer findCustomer(Long number) throws CustomerNotFoundException;

	public Customer findCustomer(String email) throws CustomerNotFoundException;

	public List<CustomerInfo> searchCustomers(String name);

	public void updateCustomer(Customer customer) throws CustomerNotFoundException, CustomerAlreadyExistsException;
}
