package org.books.application.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import org.books.application.exception.CustomerAlreadyExistsException;
import org.books.application.exception.CustomerNotFoundException;
import org.books.persistence.dto.CustomerInfo;
import org.books.persistence.entity.Customer;
import org.books.persistence.repository.CustomerRepository;

import static javax.ejb.TransactionAttributeType.REQUIRED;

@LocalBean
@Stateless(name = "CustomerService")
@TransactionAttribute(REQUIRED)
public class CustomerService implements CustomerServiceRemote {

	private static final Logger logger = Logger.getLogger(CustomerService.class.getName());

	@EJB
	private CustomerRepository customerRepository;

	@Override
	public Long registerCustomer(Customer customer) throws CustomerAlreadyExistsException {
		logger.log(Level.INFO, "Registering customer with email ''{0}''", customer.getEmail());
		if (customerRepository.find(customer.getEmail()) != null) {
			logger.log(Level.INFO, "Customer with same email already exists");
			throw new CustomerAlreadyExistsException();
		}
		customer = customerRepository.persist(customer);
		return customer.getNumber();
	}

	@Override
	public Customer findCustomer(Long number) throws CustomerNotFoundException {
		logger.log(Level.INFO, "Finding customer with number ''{0}''", number);
		Customer customer = customerRepository.find(number);
		if (customer == null) {
			logger.log(Level.INFO, "Customer not found");
			throw new CustomerNotFoundException();
		}
		return customer;
	}

	@Override
	public Customer findCustomer(String email) throws CustomerNotFoundException {
		logger.log(Level.INFO, "Finding customer with email ''{0}''", email);
		Customer customer = customerRepository.find(email);
		if (customer == null) {
			logger.log(Level.INFO, "Customer not found");
			throw new CustomerNotFoundException();
		}
		return customer;
	}

	@Override
	public List<CustomerInfo> searchCustomers(String name) {
		logger.log(Level.INFO, "Searching customers with name ''{0}''", name);
		return customerRepository.search(name);
	}

	@Override
	public void updateCustomer(Customer customer) throws CustomerNotFoundException, CustomerAlreadyExistsException {
		logger.log(Level.INFO, "Updating customer with number ''{0}''", customer.getNumber());
		Customer oldCustomer = findCustomer(customer.getNumber());
		if (!customer.getEmail().equals(oldCustomer.getEmail()) && customerRepository.find(customer.getEmail()) != null) {
			logger.log(Level.INFO, "Customer with same email already exists");
			throw new CustomerAlreadyExistsException();
		}
		customerRepository.update(customer);
	}
}
