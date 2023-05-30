package org.example;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.entity.Book;
import org.example.entity.Student;
import org.example.entity.StudentHasBook;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import org.example.utils.HibernateUtil;
import static org.junit.jupiter.api.Assertions.*;

public class BookLibraryTest {
    Student student ;
    Book book;
    String name = "testName";
    String author = "testAuthor";
    String isbn = "12345";

    private static final Logger logger = LogManager.getLogger(BookLibraryTest.class);

    @BeforeEach
    public void setup(){
        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();
            book = new Book(name, author, isbn);
            session.persist(book);
            student = new Student(name);
            session.persist(student);

            transaction.commit();
            logger.info("Book with id '" + book.getId() + "' created successfully");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error(e.getMessage());
            logger.error("Error executing database operations: " + e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }



    @AfterEach
    public void teardown(){
        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            String sql2 = "DELETE FROM student_has_book;";
            NativeQuery query2 = session.createNativeQuery(sql2, StudentHasBook.class);
            query2.executeUpdate();

            String sql = "DELETE FROM student;";
            NativeQuery query = session.createNativeQuery(sql, Student.class);
            query.executeUpdate();


            String sql3 = "DELETE FROM book;";
            NativeQuery query3 = session.createNativeQuery(sql3, Book.class);
            query3.executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error(e.getMessage());
            logger.error("Error executing database operations: " + e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }


    @Test
    public void testCreateBook(){
       String response = Main.createBook("Okiki", "oki", "12345");
        assertEquals(response, "Book created successfully");
    }

    @Test
    public void testCreateStudent(){
        String response = Main.createStudent("test");
        assertEquals(response, "Student created successfully");

    }

    @Test
    public void testBorrowBookWhenBookIsAvailable(){

        String response = Main.borrowBook(name, book.getId() );
        assertEquals(response, "Book borrowed successfully");
    }

    @Test
    public void testReturnBook(){
        Main.borrowBook(name, book.getId());
        String response = Main.returnBook(name);
        assertEquals(response, "Book returned successfully");
    }

    @Test
    public void testReturnBookWhenStudentDidNotBorrowBook(){
        String response = Main.returnBook(name);
        assertEquals(response, "You have no borrowed book to return");
    }

    @Test
    public void testBorrowBookWhenBookIsNotAvailable(){

        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();
            book.setAvailable(false);
            session.update(book);
            System.out.println(book.getAvailable());
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error(e.getMessage());
            logger.error("Error executing database operations: " + e);
        } finally {
            HibernateUtil.closeSession(session);
        }

        String response = Main.borrowBook(name, book.getId());
        assertEquals(response, "Book is not available at the moment");
    }

    @Test
    public void testBorrowBookWhenStudentHasAlreadyBorrowedBook(){
        Main.borrowBook(name, book.getId());
        String response = Main.borrowBook(name, book.getId());
        assertEquals(response, "You already borrowed a book, kindly return that one before you try to borrow another one");

    }

}
