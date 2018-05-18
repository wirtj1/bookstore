package org.books.application.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true, inherited = true)
public class BookstoreException extends Exception {
}
