package org.books.application.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;

import org.books.application.exception.BookAlreadyExistsException;
import org.books.application.exception.BookNotFoundException;
import org.books.integration.AmazonCatalog;
import org.books.persistence.entity.Book;
import org.books.persistence.repository.BookRepository;

import static javax.ejb.TransactionAttributeType.REQUIRED;

@LocalBean
@Stateless(name = "CatalogService")
@TransactionAttribute(REQUIRED)
public class CatalogService implements CatalogServiceRemote {

    private static final Logger logger = Logger.getLogger(CatalogService.class.getName());

    @EJB
    private BookRepository bookRepository;

    @EJB
    private AmazonCatalog amazonCatalog;

    @Override
    public void addBook(Book book) throws BookAlreadyExistsException {
        logger.log(Level.INFO, "Adding book with isbn ''{0}''", book.getIsbn());
        if (bookRepository.find(book.getIsbn()) != null) {
            logger.log(Level.INFO, "Book with same isbn already exists");
            throw new BookAlreadyExistsException();
        }
        bookRepository.persist(book);
    }

    @Override
    public Book findBook(String isbn) throws BookNotFoundException {
        logger.log(Level.INFO, "Finding book with isbn ''{0}''", isbn);
        Book book = bookRepository.find(isbn);
        if (book == null) {
            book = amazonCatalog.itemLookup(isbn);
            if (book == null) {
                logger.log(Level.INFO, "Book not found");
                throw new BookNotFoundException();
            }
        }
        return book;
    }

    @Override
    public List<Book> searchBooks(String keywords) {
        logger.log(Level.INFO, "Searching books with keywords ''{0}''", keywords);
        List<Book> books = amazonCatalog.itemSearch(keywords);
        books.addAll(bookRepository.search(keywords));
        return books;

    }

    @Override
    public void updateBook(Book book) throws BookNotFoundException {
        logger.log(Level.INFO, "Updating book with isbn ''{0}''", book.getIsbn());
        bookRepository.update(book);
    }
}
