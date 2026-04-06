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
package org.rd.fullstack.springbootnuxt.config;

import java.math.BigDecimal;
import org.rd.fullstack.springbootnuxt.dao.InventoryRepository;
import org.rd.fullstack.springbootnuxt.dao.PersonRepository;
import org.rd.fullstack.springbootnuxt.dao.ProductRepository;
import org.rd.fullstack.springbootnuxt.dto.Inventory;
import org.rd.fullstack.springbootnuxt.dto.Person;
import org.rd.fullstack.springbootnuxt.dto.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class DatabaseConfig {

    @Bean
    CommandLineRunner loadData(PersonRepository personRepository,
                               ProductRepository productRepository,
                               InventoryRepository inventoryRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.info("Generating data...");

            // The persons entities.
            logger.info("Persons ...");

            personRepository.save(new Person("John", "Wick", new BigDecimal("1000.00")));
            personRepository.save(new Person("Jack", "Sparrow", new BigDecimal("1000.00")));
            personRepository.save(new Person("Conan", "Barbarian", new BigDecimal("1000.00")));
            personRepository.save(new Person("Suzan", "Storm", new BigDecimal("1000.00")));
            personRepository.save(new Person("Johnny", "Cash", new BigDecimal("1000.00")));
            personRepository.save(new Person("Tony", "Stark", new BigDecimal("1000.00")));
            personRepository.save(new Person("Sophia", "Madria", new BigDecimal("1000.00")));
            personRepository.save(new Person("James", "Bond", new BigDecimal("1000.00")));
            personRepository.save(new Person("Jolene", "Spinoza", new BigDecimal("1000.00")));
            personRepository.save(new Person("Monica", "Spears", new BigDecimal("1000.00")));
            personRepository.flush();

            // The products entities.
            logger.info("Products ...");

            productRepository.save(new Product("Apple", "Mcinstosh Apple",new BigDecimal("9.99")));
            productRepository.save(new Product("Banana", "Banana split",new BigDecimal("4.99")));
            productRepository.save(new Product("Steak", "T-Bone Steak 4pack",new BigDecimal("49.99")));
            productRepository.save(new Product("Oats", "Quaker Quick Oats",new BigDecimal("9.99")));
            productRepository.save(new Product("Soup", "Campbells chicken soup",new BigDecimal("0.99")));
            productRepository.save(new Product("Milk", "Milk 2% - Lactose free",new BigDecimal("4.99")));
            productRepository.save(new Product("Bread", "Multigrain bread",new BigDecimal("5.99")));
            productRepository.save(new Product("Juice", "Grapefruit juice",new BigDecimal("3.99")));
            productRepository.save(new Product("Patato", "Bag of patatos",new BigDecimal("4.99")));
            productRepository.save(new Product("Fish", "Atlantic Cod fish",new BigDecimal("29.99")));
            productRepository.flush();

            // The inventories entities.
            productRepository.findAll().forEach(product -> 
                inventoryRepository.save(new Inventory(product.getProductId(),100L)));

            logger.info("Generation completed.");
        };
    }
}