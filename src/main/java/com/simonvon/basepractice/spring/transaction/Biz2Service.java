package com.simonvon.basepractice.spring.transaction;

import com.simonvon.basepractice.spring.dao.CustomerRepository;
import com.simonvon.basepractice.spring.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Biz2Service {

    @Autowired
    private CustomerRepository customerRepository;

    public void batchSaveNoTxWithError(){
        customerRepository.save(new Customer("Tim", "von"));
        customerRepository.save(new Customer("Simon", "von"));
        Integer a = null;
        a.toString();
    }


    public void batchSaveNoTxWithSuccess(){
        customerRepository.save(new Customer("Tim", "von"));
        customerRepository.save(new Customer("Simon", "von"));
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void txRequiresNewSuccess(){
        customerRepository.save(new Customer("Tim", "von"));
    }


    @Transactional(propagation = Propagation.NESTED)
    public void txNestedSuccess(){
        customerRepository.save(new Customer("Tim", "von"));
    }


    @Transactional(propagation = Propagation.NESTED)
    public void txNestedError(){
        customerRepository.save(new Customer("Tim", "von"));
        throw new RuntimeException();
    }

    @Transactional(propagation = Propagation.NEVER)
    public void txNever(){
        customerRepository.save(new Customer("Tim", "von"));
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void txRequiresNewError(){
        customerRepository.save(new Customer("Simon", "von"));
        throw new RuntimeException();
    }


    /**
     * 执行一些不需要事务的业务
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void doSomeThingNotNeedTx(){
        for (int i = 0; i < 500; i++) {

        }
    }


}
