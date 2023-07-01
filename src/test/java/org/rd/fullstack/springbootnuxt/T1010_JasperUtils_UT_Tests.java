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

package org.rd.fullstack.springbootnuxt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.rd.fullstack.springbootnuxt.config.Application;
import org.rd.fullstack.springbootnuxt.dao.BookRepository;
import org.rd.fullstack.springbootnuxt.dto.Book;
import org.rd.fullstack.springbootnuxt.util.JasperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JsonDataSource;

/*
 * See POM.XML file
 * - Plugins section: maven-surefire-plugin
 * - Unit tests VS integrated tests.
 */
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
// @ActiveProfiles("test") If specific configuration file. Example: application-test.yml
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Utility tests for generating reports.")
public class T1010_JasperUtils_UT_Tests {

    private static final Logger logger = LoggerFactory.getLogger(T1010_JasperUtils_UT_Tests.class);

    private static final String imageInput  = "classpath:jasper/booksBase64.txt";
    private static final String imageOutput = "./target/image.jpg";
    private static final String target      = "./target/BooksReport";

    private static final String jrxmlReport = "jasper/book-report.jrxml";
    
    @Autowired
    private BookRepository bookRepository;
    
    public T1010_JasperUtils_UT_Tests() {
        super();
    }

    @BeforeAll
    static public void setUpBeforeAll() {
        logger.info("@BeforeAll - Runs once before all test methods of this class.");
    }

    @AfterAll
    static void tearDownAfterAll() {
         logger.info("@BeforeAll - Run once after all test methods of this class.");
    }

    @BeforeEach
    void setUpBeforeEach() {
        logger.info("@BeforeEach - Executes before each test method of this class.");
    }

    @AfterEach
    void tearDownAfterEach() {
        logger.info("@BeforeEach - Executes after each test method of this class.");
    }

    @Test
    public void generateBooksImage() {

        InputStream imageStream = null;
        try {
            File file = ResourceUtils.getFile(imageInput);
            imageStream = new FileInputStream(file);
            String base64Image = new String(imageStream.readAllBytes()); 
            imageStream.close(); imageStream = null;

            logger.info("Base64 image : " + base64Image);

            // The code to put in Jasper:
            // --------------------------------------------------------------------------------------
            // - new java.io.ByteArrayInputStream(java.util.Base64.getDecoder().decode(base64Image));
            // --------------------------------------------------------------------------------------
            // - Important: jasper does not import. Classes must be specified with the full name.
            // Doesn't seem to work with this version. Awaiting a fix.
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(java.util.Base64.getDecoder().decode(base64Image));

            // Rebuild the image and let's see the results manually.
            File outputFile = new File(imageOutput);
            Files.write(outputFile.toPath(), bais.readAllBytes());

            } catch (Exception ex) {
                logger.info(ex.getMessage());
            } finally {
                if (imageStream != null)
                    try { imageStream.close(); } catch (Exception ex) {}
            }
    }

    @Test
    public void generateBooksReport() throws Exception {
        logger.info("Start of execution.");

        // Get the free ones for our report.
        List<Book> books = new ArrayList<Book>();
        bookRepository.findAll().forEach(books::add);

        // Jackson can serialize/deserialize most of your classes without requiring configuration
        // particular. We construct a series of POJO/JAVA objects to produce a JSON string.
        // This string is then used to reconstruct the object.
        ObjectMapper objectMapper = new ObjectMapper();
        String strJSONReport = objectMapper.writeValueAsString(books);
        logger.info(strJSONReport); // Produce a JSON string.

        // JASPER ...
        // A report (.JRXML) must be compiled by JASPER in order to use it to produce a report.
        // This process is long and can be done at compile time. We have included a plugin that
        // do this work. See: POM.XML. For demonstration purposes, we perform a compilation
        // by hand. This compilation is normally kept with the same name as the source. However, the extension
        // will be (.JASPER).

        JasperReport jasperReport = null;
        try {
            jasperReport = JasperUtils.compileReport(jrxmlReport, true);
        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }
        Assert.notNull(jasperReport, "jasperReport == NULL");

        // Produce the report
        // =====================================================================================================
        // The production of the report consists of using the compiled version (.JASPER) with a data source and
        // parameters (optional). This production will produce a report in (.JRPRINT) format. The latter is used
        // in order to produce the final output: pdf, xls, html or csv.

        // Data source. JRDatasource. See documentation. PDF attached; doc directory.
        // Our JSON datasource has multiple branches. We specify the branch to use.

        JsonDataSource datasource = new JsonDataSource(new java.io.ByteArrayInputStream(strJSONReport.getBytes("UTF-8")));

        // Our settings. The title only for demonstration.
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("title", "Beautiful books");
        parameters.put("sub-title", "Beautiful books to collect");

        // Production of the report.
        JasperPrint jasperPrint = null;
        try {
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, datasource);
        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }
        Assert.notNull(jasperPrint, "jasperPrint == NULL");

        // Our report is complete. There are different ways to export it.
        JasperUtils.exportToPdf(jasperPrint, target + ".pdf");
        JasperUtils.exportToXlsx(jasperPrint,target + ".xlsx", "Report");
        JasperUtils.exportToCsv(jasperPrint,target + ".csv");
        JasperUtils.exportToHtml(jasperPrint,target + ".html");
 
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.reset(); // Make sure you are at zero.

        JasperUtils.exportToPdf(jasperPrint, baos);
        Assert.isTrue((baos.toByteArray().length != 0), "No report in buffer.");

        logger.info("End of execution.");
    }
}