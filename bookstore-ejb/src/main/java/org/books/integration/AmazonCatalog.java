package org.books.integration;

import com.amazon.webservices.AWSECommerceService;
import com.amazon.webservices.AWSECommerceServicePortType;
import com.amazon.webservices.Errors;
import com.amazon.webservices.Item;
import com.amazon.webservices.ItemAttributes;
import com.amazon.webservices.ItemLookup;
import com.amazon.webservices.ItemLookupRequest;
import com.amazon.webservices.ItemLookupResponse;
import com.amazon.webservices.ItemSearch;
import com.amazon.webservices.ItemSearchRequest;
import com.amazon.webservices.ItemSearchResponse;
import com.amazon.webservices.Items;
import com.amazon.webservices.OperationRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.Singleton;
import org.books.persistence.entity.Book;
import org.books.persistence.enumeration.BookBinding;

import static javax.ejb.LockType.WRITE;

@Singleton
public class AmazonCatalog {

	private static final String ASSOCIATE_TAG = "test0953-20";
	private static final String SEARCH_INDEX = "Books";
	private static final String ID_TYPE = "ISBN";
	private static final String RESPONSE_GROUP = "ItemAttributes";
	private static final String AVAILABLITY = "Available";
	private static final long DELAY_TIME = 1000;
	private static final Logger logger = Logger.getLogger(AmazonCatalog.class.getName());

	private AWSECommerceServicePortType amazonPort;
	private long timestamp = System.currentTimeMillis();

	@PostConstruct
	public void init() {
		AWSECommerceService service = new AWSECommerceService();
		amazonPort = service.getAWSECommerceServicePort();
	}

	@Lock(WRITE)
	public Book itemLookup(String isbn) throws AmazonException {
		logger.log(Level.INFO, "Item lookup with isbn ''{0}''", isbn);

		// create lookup request
		ItemLookup lookup = new ItemLookup();
		lookup.setAssociateTag(ASSOCIATE_TAG);
		ItemLookupRequest request = new ItemLookupRequest();
		request.setSearchIndex(SEARCH_INDEX);
		request.setIdType(ID_TYPE);
		request.getItemId().add(isbn);
		request.getResponseGroup().add(RESPONSE_GROUP);
		lookup.getRequest().add(request);

		// invoke service
		throttle();
		ItemLookupResponse response = amazonPort.itemLookup(lookup);
		checkErrors(response.getOperationRequest());
		for (Items items : response.getItems()) {
			for (Item item : items.getItem()) {
				Book book = convertItem(item);
				if (book != null) {
					return book;
				}
			}
		}
		return null;
	}

	@Lock(WRITE)
	public List<Book> itemSearch(String keywords) throws AmazonException {
		logger.log(Level.INFO, "Item search with keywords ''{0}''", keywords);
		List<Book> results = new ArrayList<>();

		// create search request
		ItemSearch search = new ItemSearch();
		search.setAssociateTag(ASSOCIATE_TAG);
		ItemSearchRequest request = new ItemSearchRequest();
		request.setSearchIndex(SEARCH_INDEX);
		request.setKeywords(keywords);
		request.getResponseGroup().add(RESPONSE_GROUP);
		request.setAvailability(AVAILABLITY);
		search.getRequest().add(request);

		// invoke service
		throttle();
		ItemSearchResponse response = amazonPort.itemSearch(search);
		checkErrors(response.getOperationRequest());

		// assemble results
		for (Items items : response.getItems()) {
			for (Item item : items.getItem()) {
				Book book = convertItem(item);
				if (book != null) {
					results.add(book);
				}
			}
		}
		return results;
	}

	private void checkErrors(OperationRequest request) throws AmazonException {
		if (request.getErrors() != null) {
			Errors.Error error = request.getErrors().getError().get(0);
			throw new AmazonException(error.getMessage(), error.getCode());
		}
	}

	private Book convertItem(Item item) {
		ItemAttributes attrs = item.getItemAttributes();
		if (attrs.getISBN() == null || attrs.getTitle() == null
				|| attrs.getAuthor() == null || attrs.getAuthor().isEmpty()
				|| attrs.getPublisher() == null || attrs.getListPrice() == null) {
			return null;
		}
		Book book = new Book();
		book.setIsbn(attrs.getISBN());
		book.setTitle(attrs.getTitle());
		String authors = attrs.getAuthor().toString();
		book.setAuthors(authors.substring(1, authors.length() - 1));
		book.setPublisher(attrs.getPublisher());
		if (attrs.getPublicationDate() != null) {
			book.setPublicationYear(new Integer(attrs.getPublicationDate().substring(0, 4)));
		}
		if (attrs.getBinding() != null) {
			book.setBinding(convertBinding(attrs.getBinding()));
		}
		if (attrs.getNumberOfPages() != null) {
			book.setNumberOfPages(attrs.getNumberOfPages().intValue());
		}
		book.setPrice(new BigDecimal(attrs.getListPrice().getAmount(), 2));
		return book;
	}

	private BookBinding convertBinding(String binding) {
		if (binding.contains("Hardcover")) {
			return BookBinding.HARDCOVER;
		} else if (binding.contains("Paperback")) {
			return BookBinding.PAPERBACK;
		} else if (binding.contains("Kindle")) {
			return BookBinding.EBOOK;
		} else {
			return BookBinding.UNKNOWN;
		}
	}

	private void throttle() {
		try {
			Thread.sleep(Math.max(0, timestamp + DELAY_TIME - System.currentTimeMillis()));
			timestamp = System.currentTimeMillis();
		} catch (InterruptedException ex) {
		}
	}
}
