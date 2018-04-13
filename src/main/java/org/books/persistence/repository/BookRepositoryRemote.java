package org.books.persistence.repository;

import org.books.persistence.entity.Book;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface BookRepositoryRemote extends RepositoryRemote<Book> {

	public Book find(String isbn);

	public List<Book> search(String keywords);
}
