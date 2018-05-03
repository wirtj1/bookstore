package org.books.integration;

import java.util.List;
import javax.ejb.Singleton;
import org.books.persistence.entity.Book;

@Singleton
public class AmazonCatalog {

	public Book itemLookup(String isbn) throws AmazonException {
		throw new UnsupportedOperationException();
	}

	public List<Book> itemSearch(String keywords) throws AmazonException {
		throw new UnsupportedOperationException();
	}
}
