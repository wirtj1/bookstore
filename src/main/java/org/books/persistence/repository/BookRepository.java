package org.books.persistence.repository;

import org.books.persistence.entity.Book;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Stateless(mappedName="BookRepository")
public class BookRepository extends Repository<Book> implements BookRepositoryRemote {

	public Book find(String isbn) {
		return super.find(Book.class, isbn);
	}

	public List<Book> search(String keywords) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Book> query = builder.createQuery(Book.class);
		Root<Book> book = query.from(Book.class);
		query.select(book);
		String[] tokens = keywords.toLowerCase().split("\\s+");
		Predicate[] predicates = new Predicate[tokens.length];
		for (int i = 0; i < tokens.length; i++) {
			String pattern = "%" + tokens[i] + "%";
			predicates[i] = builder.or(
					builder.like(builder.lower(book.<String>get("title")), pattern),
					builder.like(builder.lower(book.<String>get("authors")), pattern),
					builder.like(builder.lower(book.<String>get("publisher")), pattern));
		}
		query.where(builder.and(predicates));
		return entityManager.createQuery(query).getResultList();
	}
}
