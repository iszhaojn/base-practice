package com.simonvon.basepractice.spring.transaction;

import com.simonvon.basepractice.spring.dao.CustomerRepository;
import com.simonvon.basepractice.spring.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionService {

    @Autowired
    private Biz1Service biz1Service;
    @Autowired
    private Biz2Service biz2Service;

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * 支持当前事务，如果当前没有事务，则新建一个事务，
     * 这是最常见的选择，也是 Spring 默认的一个事务传播属性
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void propagationRequired() {
        customerRepository.save(new Customer("Vivi", "von"));
        biz1Service.batchSaveTxWithError();
    }

    /**
     * 支持当前事务，如果当前没有事务，则以非事务方式执行。
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public void propagationSupports() {
        biz1Service.batchSaveNoTxWithError();
    }

    /**
     * 该级别的事务要求上下文中必须要存在事务，否则就会抛出异常！
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void propagationMandatory() {
        System.out.println("test propagationMandatory start");
        biz1Service.batchSaveNoTxWithError();
    }


    /**
     * 该传播级别的特点是，每次都会新建一个事务，并且同时将上下文中的事务挂起，执行当前新建事务完成以后，上下文事务恢复再执行
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void txRequiredInCloudRequiresNew() {
        customerRepository.save(new Customer("Tim", "von"));
        biz2Service.txRequiresNewError();
    }

    /**
     * 该传播级别的特点是，每次都会新建一个事务，并且同时将上下文中的事务挂起，执行当前新建事务完成以后，上下文事务恢复再执行
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void txPropagationNotSupported() {
        customerRepository.save(new Customer("Tim", "von"));
        biz2Service.doSomeThingNotNeedTx();
        customerRepository.save(new Customer("Simon", "von"));
    }


    /**
     * PROPAGATION_NEVER传播级别要求上下文中不能存在事务，一旦有事务，就抛出runtime异常，强制停止执行
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void testPropagationNever() {
        biz2Service.txNever();
    }


    @Transactional(propagation = Propagation.NESTED)
    public void testPropagationNested() {
        customerRepository.save(new Customer("Vivi", "von"));
        biz2Service.txRequiresNewError();
    }
}
