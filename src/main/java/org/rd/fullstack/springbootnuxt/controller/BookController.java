/*
 * Copyright 2023; Réal Demers.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.rd.fullstack.springbootnuxt.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.rd.fullstack.springbootnuxt.dao.BookRepository;
import org.rd.fullstack.springbootnuxt.dto.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@CrossOrigin
@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "SecureAPI")
public class BookController {

    public BookController() {
        super();
    }

    @Autowired
    private BookRepository bookRepository;

    @PreAuthorize("hasRole('ROLE_SELECT')")
    @GetMapping(value = "/books", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get the list of books.", description = "Book.class")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success|OK."),
        @ApiResponse(responseCode = "204", description = "No books."),
        @ApiResponse(responseCode = "401", description = "Authentication/Authorization required."),
        @ApiResponse(responseCode = "500", description = "Exception/Internal error. Call support.")
    })
    public ResponseEntity<List<Book>> getAllBooks(@RequestParam(name="title", required = false) String title) {
        try {
            List<Book> books = new ArrayList<Book>();

            if (title == null)
                bookRepository.findAll().forEach(books::add);
            else
                bookRepository.findByTitleContaining(title).forEach(books::add);

            if (books.isEmpty())
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            return new ResponseEntity<>(books, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_SELECT')")
    @GetMapping(value = "/books/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a book by its identifier.", description = "Book.class")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success|OK."),
        @ApiResponse(responseCode = "401", description = "Authentication/Authorization required."),
        @ApiResponse(responseCode = "404", description = "Unknown book."),
        @ApiResponse(responseCode = "500", description = "Exception/Internal error. Call support.")
    })
    public ResponseEntity<Book> getBookById(@PathVariable("id") long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent())
            return new ResponseEntity<>(book.get(), HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasRole('ROLE_INSERT')")
    @PostMapping(value = "/books", consumes = MediaType.APPLICATION_JSON_VALUE,
                                   produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Insert a book.", description = "Book.class")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Success|Created."),
        @ApiResponse(responseCode = "401", description = "Authentication/Authorization required."),
        @ApiResponse(responseCode = "500", description = "Exception/Internal error. Call support.")
    })
    public ResponseEntity<Book> createBook(@RequestBody Book newBook) {
        try {
            Book book = bookRepository.save(new Book(newBook.getTitle(), newBook.getDescription()));
            return new ResponseEntity<>(book, HttpStatus.CREATED);
        } catch (Exception e) {
           return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_UPDATE')")
    @PutMapping(value = "/books/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
                                       produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update a book via its identifier.", description = "Book.class")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success|OK."),
        @ApiResponse(responseCode = "401", description = "Authentication/Authorization required."),
        @ApiResponse(responseCode = "404", description = "Unknown book."),
        @ApiResponse(responseCode = "500", description = "Exception/Internal error. Call support.")
    })
    public ResponseEntity<Book> updateBook(@PathVariable("id") long id, @RequestBody Book newBook) {
        Optional<Book> findBook = bookRepository.findById(id);
        if (findBook.isPresent()) {
            Book book = findBook.get();
            book.setTitle(newBook.getTitle());
            book.setDescription(newBook.getDescription());
            return new ResponseEntity<>(bookRepository.save(book), HttpStatus.OK);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasRole('ROLE_DELETE')")
    @DeleteMapping(value = "/books/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Destroy a book using its identifier.", description = "Book.class")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Destruction done."),
        @ApiResponse(responseCode = "401", description = "Authentication/Authorization required."),
        @ApiResponse(responseCode = "404", description = "Unknown book."),
        @ApiResponse(responseCode = "500", description = "Exception/Internal error. Call support.")
    })
    public ResponseEntity<HttpStatus> deleteBook(@PathVariable("id") long id) {
        Optional<Book> findBook = bookRepository.findById(id);
        if (! findBook.isPresent())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        try {
            bookRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_DELETE')")
    @DeleteMapping(value = "/books", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Destroy all books.", description = "Book.class")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Destruction of all books made."),
        @ApiResponse(responseCode = "401", description = "Authentication/Authorization required."),
        @ApiResponse(responseCode = "500", description = "Exception/Internal error. Call support.")
    })
    public ResponseEntity<HttpStatus> deleteAllBooks() {
        try {
            bookRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}