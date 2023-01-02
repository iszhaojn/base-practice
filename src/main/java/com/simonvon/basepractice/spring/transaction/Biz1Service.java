package com.simonvon.basepractice.spring.transaction;

import com.simonvon.basepractice.spring.dao.CustomerRepository;
import com.simonvon.basepractice.spring.model.Customer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Biz1Service {


    private final CustomerRepository customerRepository;

    public Biz1Service(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void batchSaveNoTxWithError() {
        customerRepository.save(new Customer("Tim", "von"));
        customerRepository.save(new Customer("Simon", "von"));
        throw new RuntimeException();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void batchSaveTxWithError() {
        customerRepository.save(new Customer("Tim", "von"));
        customerRepository.save(new Customer("Simon", "von"));
        throw new RuntimeException();
    }


    public void batchSaveNoTxWithSuccess() {
        customerRepository.save(new Customer("Tim", "von"));
        customerRepository.save(new Customer("Simon", "von"));
    }


    public String sleepAndReturn() {
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "Pass";
    }


}
