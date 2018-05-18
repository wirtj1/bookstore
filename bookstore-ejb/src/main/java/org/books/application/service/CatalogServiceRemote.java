package org.books.application.service;

import java.util.List;
import javax.ejb.Remote;
import org.books.application.exception.BookAlreadyExistsException;
import org.books.application.exception.BookNotFoundException;
import org.books.persistence.entity.Book;

@Remote
public interface CatalogServiceRemote {

	public void addBook(Book book) throws BookAlreadyExistsException;

	public Book findBook(String isbn) throws BookNotFoundException;

	public List<Book> searchBooks(String keywords);

	public void updateBook(Book book) throws BookNotFoundException;
}
