package org.books.persistence.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import org.books.persistence.enumeration.BookBinding;

@Entity
@Table(name = "BOOK")
public class Book implements Serializable {

	@Id
	@Column(name = "ISBN")
	private String isbn;
	@Column(name = "TITLE", nullable = false)
	private String title;
	@Column(name = "AUTHORS", nullable = false)
	private String authors;
	@Column(name = "PUBLISHER", nullable = false)
	private String publisher;
	@Column(name = "PUB_YEAR")
	private Integer publicationYear;
	@Column(name = "BINDING")
	@Enumerated(EnumType.STRING)
	private BookBinding binding;
	@Column(name = "NUM_PAGES")
	private Integer numberOfPages;
	@Column(name = "PRICE", precision = 7, scale = 2, nullable = false)
	private BigDecimal price;

	public Book() {
	}

	public Book(String isbn, String title, String authors, String publisher, Integer publicationYear,
			BookBinding binding, Integer numberOfPages, BigDecimal price) {
		this.isbn = isbn;
		this.title = title;
		this.authors = authors;
		this.publisher = publisher;
		this.publicationYear = publicationYear;
		this.binding = binding;
		this.numberOfPages = numberOfPages;
		this.price = price;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthors() {
		return authors;
	}

	public void setAuthors(String authors) {
		this.authors = authors;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public Integer getPublicationYear() {
		return publicationYear;
	}

	public void setPublicationYear(Integer publicationYear) {
		this.publicationYear = publicationYear;
	}

	public BookBinding getBinding() {
		return binding;
	}

	public void setBinding(BookBinding binding) {
		this.binding = binding;
	}

	public Integer getNumberOfPages() {
		return numberOfPages;
	}

	public void setNumberOfPages(Integer numberOfPages) {
		this.numberOfPages = numberOfPages;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public void setBindingAsString(String bindingAsString) {
		try {
			this.binding = BookBinding.valueOf(bindingAsString.trim().toUpperCase());
		}catch(IllegalArgumentException e){
			this.binding=BookBinding.UNKNOWN;
		}
	}
}
