package org.books.persistence.repository;

import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.books.persistence.entity.Book;

import static javax.ejb.TransactionAttributeType.SUPPORTS;

@Stateless
public class BookRepository extends Repository<Book> {

	@TransactionAttribute(SUPPORTS)
	public Book find(String isbn) {
		return super.find(Book.class, isbn);
	}

	@TransactionAttribute(SUPPORTS)
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
