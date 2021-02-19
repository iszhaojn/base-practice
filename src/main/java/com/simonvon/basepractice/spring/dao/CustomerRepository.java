package com.simonvon.basepractice.spring.dao;

import com.simonvon.basepractice.spring.model.Customer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

  List<Customer> findByLastName(String lastName);

  List<Customer> findByFirstName(String firstName);

  Customer findById(long id);


}