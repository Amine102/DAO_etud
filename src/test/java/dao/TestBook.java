package dao;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Test class using Surefire Maven plugin
 * Use 'mvn test' to run it
 * The test order is significant.
 */
public class TestBook {
    private static BookMapper bm = BookMapper.getMapper();
    private static Book JPAbook = new Book("978-1430219569",
            "JPA 2: Mastering the Java™ Persistence API",
            "Keith",
            37.49f);
    private Book sameJPABookObject = null;

    /* clear db before tests */
    public void testInit() throws BookException, AppException {
        bm.deleteAll();
        assert(true);
    }

    public void testInsertNewBook() throws BookException, AppException {
        String book1Id = (String) bm.insert(JPAbook);
        assert(book1Id != null && book1Id.equals("978-1430219569"));
    }

    public void testInsertExistingBook() throws AppException {
        try {
            // the book already exists in DB...
            bm.insert(JPAbook);
            assert(false);
        }
        catch (BookException be) {
            // exception must be thrown...
            assert(true);
        }
    }

    public void testFindExistingBook() throws BookException, AppException {
        Book book1 = (Book) bm.find("978-1430219569");
        assert(book1.equals(JPAbook));
    }

    public void testFindManyExistingBook() throws BookException, AppException {
        Book anotherJPABook = new Book("978-1484234198",
                "ProJPA 2 in Java EE 8: An In-Depth Guide to Java Persistence APIs",
                "Keith",
                37.49f);
        bm.insert(anotherJPABook);
        Set<Book> books = bm.findManyByAuthor("Keith");
        Set<Book> expectedBooks = new HashSet<Book>(Arrays.asList(JPAbook,anotherJPABook));
        assert(books.containsAll(expectedBooks));
    }

    public void testFindNotExistingBook() throws BookException, AppException {
        Book book1 = (Book) bm.find("XXXXXXXXXXXXXX");
        assert(book1 == null);
    }

    public void testFindCacheManagement() throws BookException, AppException {
        Book book1 = (Book) bm.find("978-1430219569");
        sameJPABookObject = (Book) bm.find("978-1430219569");
        // if the cache is well managed then the book instance only exists once...
        assert(book1 == sameJPABookObject);
    }

    public void testUpdateExistingBook() throws BookException, AppException {
        Book updatedJPAbook = new Book("978-1430219569",
                "JPA 2: Mastering the Java™ Persistence API",
                "Schincariol",
                37.49f);
        bm.update(updatedJPAbook);
        Book updatedBook = (Book) bm.find("978-1430219569");
        assert(updatedBook.getAuthor().equals("Schincariol"));
    }

    public void testUpdateNotExistingBook() throws AppException {
        Book updatedJPAbook = new Book("xxxx",
                "Another book",
                "Doe",
                11f);
        try {
            bm.update(updatedJPAbook);
            assert(false);
        }
        catch(BookException be) {
            // exception must be thrown...
            assert(true);
        }
    }

    public void testDeleteExistingBook() throws BookException, AppException {
        bm.delete(sameJPABookObject);
        Book deletedBook = (Book) bm.find("978-1430219569");
        assert(deletedBook == null);
    }

    public void testDeleteCacheManagement() throws BookException, AppException {
        String book1Id = (String) bm.insert(JPAbook); // re-creation
        Book book1 = (Book) bm.find(book1Id);
        Book bk = new Book("978-1430219569",
                "JPA 2: Mastering the Java™ Persistence API",
                "Keith",
                37.49f);
        assert(sameJPABookObject != book1);
    }

    public void testDeleteNotExistingBook() throws AppException {
        Book updatedJPAbook = new Book("xxxx",
                "Another book",
                "Doe",
                11f);
        try {
            bm.delete(updatedJPAbook);
            assert(false);
        }
        catch(BookException be) {
            // exception must be thrown...
            assert(true);
        }
    }

}
