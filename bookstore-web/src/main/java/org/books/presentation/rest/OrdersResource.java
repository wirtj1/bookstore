package org.books.presentation.rest;

import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.*;

import org.books.application.dto.PurchaseOrder;
import org.books.application.dto.SalesOrder;
import org.books.application.exception.BookNotFoundException;
import org.books.application.exception.CustomerNotFoundException;
import org.books.application.exception.OrderAlreadyShippedException;
import org.books.application.exception.OrderNotFoundException;
import org.books.application.exception.PaymentFailedException;
import org.books.application.service.OrderService;
import org.books.persistence.dto.OrderInfo;

@Path(value = "orders")
public class OrdersResource {

    @EJB
    private OrderService orderService;

    @POST
    @Consumes(value = "application/json")
    @Produces(value = "application/json")
    public SalesOrder placeOrder(PurchaseOrder purchaseOrder)
            throws CustomerNotFoundException, BookNotFoundException, PaymentFailedException {
        return orderService.placeOrder(purchaseOrder);
    }

    @GET
    @Path(value = "{number}")
    @Produces(value = "application/json")
    public SalesOrder findOrder(@PathParam(value = "number") Long number) throws OrderNotFoundException {
        return orderService.findOrder(number);
    }

    @GET
    @Produces(value = {"application/json", "application/xml"})
    public List<OrderInfo> searchOrders(@QueryParam(value = "customerNr") Long customerNr, @QueryParam(value = "year") Integer year) throws CustomerNotFoundException {
        return orderService.searchOrders(customerNr, year);
    }

    @DELETE
    @Path(value = "{number}")
    public void cancelOrder(@PathParam(value = "number") Long number) throws OrderNotFoundException, OrderAlreadyShippedException {
        orderService.cancelOrder(number);
    }
}
