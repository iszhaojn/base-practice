package com.simonvon.basepractice.spring.transaction;

import com.simonvon.basepractice.spring.dao.CustomerRepository;
import com.simonvon.basepractice.spring.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Biz1Service {


    @Autowired
    private CustomerRepository customerRepository;

    public void batchSaveNoTxWithError(){
        customerRepository.save(new Customer("Tim", "von"));
        customerRepository.save(new Customer("Simon", "von"));
        throw new RuntimeException();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void batchSaveTxWithError(){
        customerRepository.save(new Customer("Tim", "von"));
        customerRepository.save(new Customer("Simon", "von"));
        throw new RuntimeException();
    }



    public void batchSaveNoTxWithSuccess(){
        customerRepository.save(new Customer("Tim", "von"));
        customerRepository.save(new Customer("Simon", "von"));
    }



}
