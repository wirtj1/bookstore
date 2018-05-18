package org.books.application.dto;

import java.io.Serializable;

public class PurchaseOrderItem implements Serializable {

	private String isbn;
	private Integer quantity;

	public PurchaseOrderItem() {
	}

	public PurchaseOrderItem(String isbn, Integer quantity) {
		this.isbn = isbn;
		this.quantity = quantity;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}
