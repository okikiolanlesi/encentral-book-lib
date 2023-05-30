package org.example.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "student_has_book")
public class StudentHasBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private Book book;

    @OneToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    private Student student;

    public StudentHasBook(){

    }

    public StudentHasBook(Book book, Student student){
        this.book = book;
        this.student = student;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

}
