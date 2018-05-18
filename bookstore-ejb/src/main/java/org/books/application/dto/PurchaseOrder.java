package org.books.application.dto;

import java.io.Serializable;
import java.util.List;

public class PurchaseOrder implements Serializable {

	private Long customerNr;
	private List<PurchaseOrderItem> items;

	public PurchaseOrder() {
	}

	public PurchaseOrder(Long customerNr, List<PurchaseOrderItem> items) {
		this.customerNr = customerNr;
		this.items = items;
	}

	public Long getCustomerNr() {
		return customerNr;
	}

	public void setCustomerNr(Long customerNr) {
		this.customerNr = customerNr;
	}

	public List<PurchaseOrderItem> getItems() {
		return items;
	}

	public void setItems(List<PurchaseOrderItem> items) {
		this.items = items;
	}
}
