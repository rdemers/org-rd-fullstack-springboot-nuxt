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

import org.rd.fullstack.springbootnuxt.dao.BookRepository;
import org.rd.fullstack.springbootnuxt.dto.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
public class GraphQLController {

    public GraphQLController() {
        super();
    }

    @Autowired
    private BookRepository bookRepository;

    @PreAuthorize("hasRole('ROLE_SELECT')")
    @QueryMapping
    public List<Book> readBooks(@Argument int count, @Argument int offset) {
        List<Book> books = new ArrayList<Book>();
        bookRepository.findAll().forEach(books::add);
        return books;
    }

    @PreAuthorize("hasRole('ROLE_SELECT')")
    @QueryMapping
    public List<Book> searchByTitle(@Argument String title) {
        return bookRepository.findByTitleContaining(title);
    }

    // No other diagram ;-) ... For the moment.
    //@SchemaMapping(typeName="Book", field="title")
    //public String getFirstAuthor(Book post) {
    //    return null;
    //}

    @PreAuthorize("hasRole('ROLE_INSERT')")
    @MutationMapping
    public Book createBook(@Argument String title, @Argument String description) {
        Book book = bookRepository.save(new Book(title, description));
        return book;
   }
}