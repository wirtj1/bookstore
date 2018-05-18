package org.books.presentation.rest;

import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.*;

import org.books.application.exception.BookNotFoundException;
import org.books.application.service.CatalogService;
import org.books.persistence.entity.Book;

@Path(value = "books")
public class CatalogResource {

    @EJB
    private CatalogService catalogService;

    @GET
    @Path(value = "{isbn}")
    @Produces(value = "application/json")
    public Book findBook(@PathParam(value = "isbn") String isbn) throws BookNotFoundException {
        return catalogService.findBook(isbn);
    }

    @GET
    @Produces(value = "application/json")
    public List<Book> searchBooks(@QueryParam(value = "keywords") String keywords) {
        return catalogService.searchBooks(keywords);
    }
}
