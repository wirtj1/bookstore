package org.books.integration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import com.amazon.webservices.*;
import org.books.persistence.entity.Book;
import org.books.persistence.enumeration.BookBinding;

@Singleton
public class AmazonCatalog {

    private static final String ASSOCIATE_TAG = "test0953-20";
    private static final String SEARCH_INDEX = "Books";
    private static final String ID_TYPE = "ISBN";
    private static final String RESPONSE_GROUP_SEARCH = "ItemAttributes";
    private static final String AVAILABLE = "Available";
    private static final String RESPONSE_GROUP_LOOKUP = "Medium";

    private AWSECommerceServicePortType awseCommerceServicePort;


    @PostConstruct
    public void init(){
        AWSECommerceService awseCommerceService = new AWSECommerceService();
        awseCommerceServicePort = awseCommerceService.getAWSECommerceServicePort();
    }
    public Book itemLookup(String isbn) throws AmazonException {
        AWSECommerceService awseCommerceService = new AWSECommerceService();
        AWSECommerceServicePortType awseCommerceServicePort = awseCommerceService.getAWSECommerceServicePort();

        ItemLookup itemLookup = new ItemLookup();
        itemLookup.setAssociateTag(ASSOCIATE_TAG);

        ItemLookupRequest lookupRequest = new ItemLookupRequest();
        lookupRequest.setIdType(ID_TYPE);
        lookupRequest.setSearchIndex(SEARCH_INDEX);
        lookupRequest.getItemId().add(isbn);
        lookupRequest.getResponseGroup().add(RESPONSE_GROUP_LOOKUP);

        itemLookup.getRequest().add(lookupRequest);

        ItemLookupResponse itemLookupResponse = awseCommerceServicePort.itemLookup(itemLookup);
        List<Item> itemList = itemLookupResponse.getItems().get(0).getItem();

        List<Book> bookList = mapItemListToBookList(itemList);
        if (!bookList.isEmpty()) {
            return bookList.get(0);
        } else {
            return null;
        }

    }

    public List<Book> itemSearch(String keywords) throws AmazonException {
        AWSECommerceService awseCommerceService = new AWSECommerceService();
        AWSECommerceServicePortType awseCommerceServicePort = awseCommerceService.getAWSECommerceServicePort();

        ItemSearch itemSearch = new ItemSearch();
        itemSearch.setAssociateTag(ASSOCIATE_TAG);

        ItemSearchRequest itemSearchRequest = new ItemSearchRequest();
        itemSearchRequest.setAvailability(AVAILABLE);
        itemSearchRequest.setKeywords(keywords);
        itemSearchRequest.setSearchIndex(SEARCH_INDEX);
        itemSearchRequest.getResponseGroup().add(RESPONSE_GROUP_SEARCH);

        itemSearch.getRequest().add(itemSearchRequest);

        ItemSearchResponse itemSearchResponse = awseCommerceServicePort.itemSearch(itemSearch);
        List<Item> itemList = itemSearchResponse.getItems().get(0).getItem();

        return mapItemListToBookList(itemList);
    }

    private List<Book> mapItemListToBookList(List<Item> itemList) {
        List<Book> bookList = new ArrayList<>();
        for (Item item : itemList) {
            Book b = new Book();
            ItemAttributes attributes = item.getItemAttributes();
            b.setAuthors(attributes.getAuthor().stream().collect(Collectors.joining(",")));
            b.setBindingAsString(attributes.getBinding());
            b.setIsbn(attributes.getISBN());
            b.setNumberOfPages(attributes.getNumberOfPages() != null ? attributes.getNumberOfPages().intValue() : null);
            b.setPrice(attributes.getListPrice() != null ? new BigDecimal(attributes.getListPrice().getFormattedPrice().substring(1)) : null);
            b.setPublicationYear(attributes.getPublicationDate() != null ? Integer.parseInt(attributes.getPublicationDate().substring(0, 4)) : null);
            b.setPublisher(attributes.getPublisher());
            b.setTitle(attributes.getTitle());
            bookList.add(b);
        }
        return bookList;
    }
}
