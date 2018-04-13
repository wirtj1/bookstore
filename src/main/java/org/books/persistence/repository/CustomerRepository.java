package org.books.persistence.repository;

import org.books.persistence.dto.CustomerInfo;
import org.books.persistence.entity.Customer;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

@Stateless
public class CustomerRepository extends Repository<Customer> implements CustomerRepositoryRemote {

	public Customer find(Long number) {
		return super.find(Customer.class, number);
	}

	public Customer find(String email) {
		try {
			TypedQuery<Customer> query = entityManager.createNamedQuery("findCustomer", Customer.class);
			query.setParameter("email", email);
			return query.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	public List<CustomerInfo> search(String name) {
		TypedQuery<CustomerInfo> query = entityManager.createNamedQuery("searchCustomers", CustomerInfo.class);
		query.setParameter("pattern", "%" + name + "%");
		return query.getResultList();
	}
}
