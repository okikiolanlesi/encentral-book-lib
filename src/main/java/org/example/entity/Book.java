package org.example.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String author;
    private String isbn;
    private Boolean available;

    public Book(){

    }
    public Book(String name, String author, String isbn){
        this.name = name;
        this.author = author;
        this.isbn = isbn;
        this.available = true;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setAvailable(Boolean bool) {
        this.available = bool;
    }

    public Boolean getAvailable() {
        return available;
    }
}
