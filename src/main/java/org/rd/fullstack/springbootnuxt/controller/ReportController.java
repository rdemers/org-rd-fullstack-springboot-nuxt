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

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rd.fullstack.springbootnuxt.dao.BookRepository;
import org.rd.fullstack.springbootnuxt.dto.Book;
import org.rd.fullstack.springbootnuxt.dto.BooksReport;
import org.rd.fullstack.springbootnuxt.util.JasperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.json.data.JsonDataSource;

@CrossOrigin
@RestController
@RequestMapping("/report")
@SecurityRequirement(name = "SecureAPI")
public class ReportController {

    static final String CST_TITLE            = "Beautiful books";
    static final String CST_SUBTITLE         = "Beautiful books to collect";
    static final String CST_REPORT_BOOKS_SRC = "jasper/book-report.jrxml";
    static final String CST_DATE_FORMAT      = "yyyy-MM-dd HH:mm:ss";

    public ReportController() {
        super();
    }

    @Autowired
    private BookRepository bookRepository;

    @SuppressWarnings("null")
    @PreAuthorize("hasRole('ROLE_SELECT')")
    @GetMapping(value = "/books-report", produces = { MediaType.APPLICATION_PDF_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    @Operation(summary = "Book List Report.", description = "jasper/book-report.jrxml")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Report created|OK."),
        @ApiResponse(responseCode = "401", description = "Authentication/Authorization required."),
        @ApiResponse(responseCode = "406", description = "No support for the desired format."),
        @ApiResponse(responseCode = "500", description = "Exception/Internal error. Call support.")
    })
    public ResponseEntity<byte[]> getBooksReport(@RequestHeader(HttpHeaders.ACCEPT) String mediaType,
                                                 @RequestParam(name="title", required = false) String title,
                                                 @RequestParam(name="sub-title", required = false) String subTitle) {
        
        // Response.
        HttpHeaders headers = new HttpHeaders();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            // Check all parameters.
            if ((title == null) || (title.length() == 0))        title = CST_TITLE;
            if ((subTitle == null) || (subTitle.length() == 0))  subTitle = CST_SUBTITLE;
            if ((mediaType == null) || (subTitle.length() == 0)) mediaType = MediaType.APPLICATION_PDF_VALUE;
            
            // Get report data.
            List<Book> books = new ArrayList<Book>();
            bookRepository.findAll().forEach(books::add);

            // Setting up header (parameters).
            Map<String, Object> header = new HashMap<>();
            header.put("title", title);
            header.put("sub-title", subTitle);

            // Setting up footer.
            Map<String, Object> footer = new HashMap<>();
            footer.put("date", (new SimpleDateFormat(CST_DATE_FORMAT).format(Calendar.getInstance().getTime())));

            // Production of the report/state with the requested media.
            switch (mediaType) {
                case MediaType.APPLICATION_PDF_VALUE:
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    generatePDF(baos, header, books);  
                    break;
                case MediaType.APPLICATION_JSON_VALUE:
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    generateJSON(baos, header, books, footer);
                    break;
                case MediaType.APPLICATION_XML_VALUE:
                    headers.setContentType(MediaType.APPLICATION_XML);
                    generateXML(baos, header, books, footer);
                    break;
                default:
                    throw new InvalidMediaTypeException(mediaType, MediaType.APPLICATION_PDF_VALUE  + "|" + 
                                                                   MediaType.APPLICATION_JSON_VALUE + "|" + 
                                                                   MediaType.APPLICATION_XML_VALUE);
            }
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }   
        return new ResponseEntity<byte[]> (baos.toByteArray(), headers, HttpStatus.OK);
    }

    private void generatePDF(ByteArrayOutputStream baos, Map<String, Object> parameters, List<Book> books) throws Exception {
        String strJSONReport = new ObjectMapper().writeValueAsString(books);
        JsonDataSource datasource = new JsonDataSource(new java.io.ByteArrayInputStream(strJSONReport.getBytes("UTF-8")));
        JasperReport jasperReport = JasperUtils.compileReport(CST_REPORT_BOOKS_SRC, false);
        JasperPrint  jasperPrint  = JasperFillManager.fillReport(jasperReport, parameters, datasource);
        JasperUtils.exportToPdf(jasperPrint, baos);
    }

    private void generateJSON(ByteArrayOutputStream baos, Map<String, Object> parameters, List<Book> books, Map<String, Object> footer) throws Exception {
        BooksReport report = new BooksReport(Arrays.asList(parameters), books, Arrays.asList(footer));
        baos.write(new ObjectMapper().writeValueAsString(report).getBytes());
    }

    private void generateXML(ByteArrayOutputStream baos, Map<String, Object> parameters, List<Book> books, Map<String, Object> footer) throws Exception {
        BooksReport report = new BooksReport(Arrays.asList(parameters), books,  Arrays.asList(footer));
        baos.write("<?xml version=\"1.0\" encoding=\"UTF-8\">".getBytes());
        baos.write(new XmlMapper().writeValueAsString(report).getBytes());
    }
}