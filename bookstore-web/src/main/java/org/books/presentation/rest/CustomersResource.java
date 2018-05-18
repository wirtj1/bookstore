package org.books.presentation.rest;

import org.books.application.exception.CustomerAlreadyExistsException;
import org.books.application.exception.CustomerNotFoundException;
import org.books.application.service.CustomerService;
import org.books.persistence.entity.Customer;

import javax.ejb.EJB;
import javax.ws.rs.*;

@Path(value = "customers")
public class CustomersResource {

    @EJB
    private CustomerService customerService;

    @POST
    @Consumes(value = "application/json")
    @Produces(value = "text/plain")
    public Long registerCustomer(Customer customer) throws CustomerAlreadyExistsException {
        return customerService.registerCustomer(customer);
    }

    @GET
    @Path(value = "{number}")
    @Produces(value = "application/json")
    public Customer findCustomer(@PathParam(value = "number") Long number) throws CustomerNotFoundException {
        Customer customer = customerService.findCustomer(number);
        customer.setPassword(null);
        return customer;
    }

    @GET
    @Produces(value = "application/json")
    public Customer findCustomer(@QueryParam(value = "email") String email) throws CustomerNotFoundException {
        Customer customer = customerService.findCustomer(email);
        customer.setPassword(null);
        return customer;
    }

    @PUT
    @Path(value="{number}")
    @Consumes(value="application/json")
    public void updateCustomer(@PathParam(value="number") Long number, Customer customer)
            throws CustomerNotFoundException, CustomerAlreadyExistsException {
        if (customer.getNumber() == null) {
            customer.setNumber(number);
        }
        customerService.updateCustomer(customer);
    }
}
