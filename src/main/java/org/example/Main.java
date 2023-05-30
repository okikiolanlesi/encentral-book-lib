package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.entity.Book;
import org.example.entity.Student;
import org.example.entity.StudentHasBook;
import org.example.utils.HibernateUtil;
import org.hibernate.Transaction;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;

import java.util.List;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static String createBook(String name, String author, String isbn ){
        String returnMessage = "Book created successfully";
        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();
            Book book = new Book(name, author, isbn);
            session.persist(book);

            transaction.commit();
            logger.info("Book with id '" + book.getId() + "' created successfully");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            returnMessage = e.getMessage();
            logger.error("Error executing database operations: " + e);
        } finally {
            HibernateUtil.closeSession(session);
        }

        return returnMessage;
    }

    public static String createStudent(String name){
        String returnMessage = "Student created successfully";
        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();
            String sql = "SELECT * FROM student where name = '"+ name +"';";
            NativeQuery query = session.createNativeQuery(sql, Student.class);

            List<Student> isStudentExists = query.getResultList();

            System.out.println(isStudentExists.size());

            if(isStudentExists.size() != 0){
                throw new Exception("Student already exists with that name") ;
            }

            Student student = new Student(name);
            session.persist(student);

            transaction.commit();
            logger.info("Student with id '" + student.getId() + "' created successfully");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            returnMessage = e.getMessage();
            logger.error("Error executing database operations: " + e);
        } finally {
            HibernateUtil.closeSession(session);
        }

        return returnMessage;
    }

    public static String borrowBook(String studentName, Long bookId){
        String returnMessage = "Book borrowed successfully";
        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

//          Check if student exists
            String sql = "SELECT * FROM student where name = '"+ studentName +"';";
            NativeQuery query = session.createNativeQuery(sql, Student.class);
            List<Student> isStudentExists = query.getResultList();

            if(isStudentExists.size() == 0){
                throw new Exception("No student exists with that name");
            }

//          Check if student already borrowed book
            String sql2 = "SELECT * FROM student_has_book where student_id = "+ isStudentExists.get(0).getId() +";";
            NativeQuery studentHasBookQuery = session.createNativeQuery(sql2, StudentHasBook.class);
            List<StudentHasBook> isAlreadyBorrowedBook = studentHasBookQuery.getResultList();

            if(isAlreadyBorrowedBook.size() != 0){
                throw new Exception("You already borrowed a book, kindly return that one before you try to borrow another one");
            }

//          Check if book exists
            Book book= session.get(Book.class, bookId);

            if(book == null){
                throw new Exception("Book does not exist");
            }

            if(!book.getAvailable()){
                throw new Exception("Book is not available at the moment");
            }

//          Borrow book
            book.setAvailable(false);
            session.persist(book);

            Book bookToRef = session.get(Book.class, bookId);
            Student studentToRef = session.get(Student.class, isStudentExists.get(0).getId());

            StudentHasBook newEntry = new StudentHasBook();
            newEntry.setBook(bookToRef);
            newEntry.setStudent(studentToRef);

            session.persist(newEntry);

            transaction.commit();
            logger.info("Student with name '" + studentName + "' borrowed "+ book.getName() + " book successfully");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            returnMessage = e.getMessage();
            logger.error("Error executing database operations: " + e);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return returnMessage;
    }

    public static String returnBook(String studentName){
        String returnMessage = "Book returned successfully";
        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

//            Check if student exists
            String sql = "SELECT * FROM student where name = '"+ studentName +"';";
            NativeQuery query = session.createNativeQuery(sql, Student.class);
            List<Student> isStudentExists = query.getResultList();

            if(isStudentExists.size() == 0){
                throw new Exception("No student exists with that name");
            }

//          Check if student already borrowed book
            String studentHasBookSql = "SELECT * FROM student_has_book where student_id = "+ isStudentExists.get(0).getId() +";";
            NativeQuery studentHasBookQuery = session.createNativeQuery(studentHasBookSql, StudentHasBook.class);
            List<StudentHasBook> isAlreadyBorrowedBook = studentHasBookQuery.getResultList();

            if(isAlreadyBorrowedBook.size() == 0){
                throw new Exception("You have no borrowed book to return");
            }

//          Return book
            Book book = isAlreadyBorrowedBook.get(0).getBook();
            book.setAvailable(true);
            session.update(book);

            session.remove(isAlreadyBorrowedBook.get(0));
            transaction.commit();

            logger.info("Student with id '" + isStudentExists.get(0).getId() + "' returned book successfully");
        } catch (Exception e) {

            if (transaction != null) {
                transaction.rollback();
            }
            returnMessage = e.getMessage();
            logger.error("Error executing database operations: " + e);
        } finally {
            HibernateUtil.closeSession(session);
        }

        return returnMessage;
    }

    public static void main(String[] args){
        createBook("okiki", "auto", "343212");
        createStudent("Okiki");
        createStudent("test");
        borrowBook("test", 1L);
        borrowBook("Okiki", 1L);
        returnBook("Okiki");
//        returnBook("Okiki");

    }
}
