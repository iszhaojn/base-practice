package com.simonvon.basepractice;


import com.simonvon.basepractice.spring.dao.CustomerRepository;
import com.simonvon.basepractice.spring.transaction.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.util.Assert;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
@RunWith(SpringRunner.class)
class BasePracticeApplicationTests {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private CustomerRepository customerRepository;

    /**
     * 支持当前事务，如果当前没有事务，则新建一个事务，
     */
    @Test
    void testPropagationRequired() {
        try {
            transactionService.propagationRequired();
        } catch (Exception e) {

        }
        Assert.isTrue(customerRepository.findByFirstName("Vivi").size() == 0, "事务成功,Vivi保存失败");
        Assert.isTrue(customerRepository.findByFirstName("Simon").size() == 0, "事务成功,Simon保存失败");
        Assert.isTrue(customerRepository.findByFirstName("Tim").size() == 0, "事务成功,Tim保存失败");

    }

    /**
     * 支持当前事务，如果当前没有事务，则以非事务方式执行。
     */
    @Test
    void testPropagationSupports() {
        try {
            transactionService.propagationSupports();
        } catch (Exception e) {
//            e.printStackTrace();
        }
        Assert.isTrue(customerRepository.findByFirstName("Tim").size() > 0, "事务失败,Tim保存成功");
    }

    /**
     * 该级别的事务要求上下文中必须要存在事务，否则就会抛出异常！
     */
    @Test
    void testPropagationMandatory() {
        Exception exception = null;
        try {
            transactionService.propagationMandatory();
        } catch (Exception e) {
            exception = e;
        }
        Assert.isTrue(customerRepository.findByFirstName("Tim").size() == 0, "事务未开始,执行前抛出异常");
        Assert.isTrue(exception instanceof IllegalTransactionStateException, "上下文中必须要存在事务");
    }


    /**
     * 该传播属性不支持事务，该级别的特点就是上下文中存在事务，则挂起事务，执行当前逻辑，结束后恢复上下文的事务
     * 这个级别有什么好处？可以帮助你将事务极可能的缩小。因为一个事务越大，它存在的风险也就越多。所以在处理事务的过程中，要保证尽可能的缩小范围
     * 比如一段代码，是每次逻辑操作都必须调用的，比如循环1000次的某个非核心业务逻辑操作。这样的代码如果包在事务中，势必造成事务太大，
     * 导致出现一些难以考虑周全的异常情况。所以这个事务这个级别的传播级别就派上用场了。用当前级别的事务模板包起来就可以了
     */
    @Test
    void testPropagationNotSupported(){
        transactionService.txPropagationNotSupported();
        Assert.isTrue(customerRepository.findByFirstName("Tim").size() > 0, "事务成功,Tim保存成功");
        Assert.isTrue(customerRepository.findByFirstName("Simon").size() > 0, "事务成功,Simon保存成功");
    }

    /**
     * 该事务更严格，NOT_SUPPORTED事务传播级别只是不支持而已，有事务就挂起，
     * 而PROPAGATION_NEVER传播级别要求上下文中不能存在事务，一旦有事务，就抛出runtime异常，强制停止执行
     */
    @Test
    void testPropagationNever(){
        Exception exception = null;

        try {
            transactionService.testPropagationNever();
        } catch (Exception e) {
            exception = e;
        }
        Assert.isTrue(exception instanceof IllegalTransactionStateException,"该方法上下文不能包含事务");
    }


    /**
     * 该传播级别的特点是，每次都会新建一个事务，并且同时将上下文中的事务挂起，执行当前新建事务完成以后，上下文事务恢复再执行
     * 外层事务A为Required,内层事务B为Require_New,A内部调用事务B,内层B事务失败回滚,外层A事务执行成功
     */
    @Test
    void testPropagationRequiresNew() {
        try {
            transactionService.txRequiredInCloudRequiresNew();
        } catch (Exception e) {

        }
        Assert.isTrue(customerRepository.findByFirstName("Tim").size() > 0, "外层事务成功");
        Assert.isTrue(customerRepository.findByFirstName("Simon").size() == 0, "内层事务失败");
    }

    /**
     * RequiresNew和Nested的区别就是,当外层事务回滚的时候,内部事务RequiresNew是独立新事务,不会回滚;Nested是嵌套事务,外部回滚,内部事务也回滚
     */
    @Test
    void testPropagationRequiresNew2() {
        try {
            transactionService.txRequiredInCloudRequiresNew2();
        } catch (Exception e) {
        }
        Assert.isTrue(customerRepository.findByFirstName("Tim").size() == 0, "外层事务回滚,Tim保存失败");
        Assert.isTrue(customerRepository.findByFirstName("Simon").size() > 0, "内层事务提交成功,Simon保存成功");
    }


    /**
     * 嵌套级别事务。该传播级别特征是，如果上下文中存在事务，则嵌套事务执行，如果不存在事务，则新建事务。
     * PROPAGATION_REQUIRES_NEW 启动一个新的, 不依赖于环境的 "内部" 事务. 这个事务将被完全 commited 或 rolled back 而不依赖于外部事务, 它拥有自己的隔离范围, 自己的锁, 等等. 当内部事务开始执行时, 外部事务将被挂起, 内务事务结束时, 外部事务将继续执行.
     * 另一方面, PROPAGATION_NESTED 开始一个 "嵌套的" 事务,  它是已经存在事务的一个真正的子事务. 潜套事务开始执行时,  它将取得一个 savepoint. 如果这个嵌套事务失败, 我们将回滚到此 savepoint. 潜套事务是外部事务的一部分, 只有外部事务结束后它才会被提交.
     */
    @Test
    void testPropagationNested(){
        try {
            transactionService.testPropagationNested();
        } catch (Exception e) {
        }
        Assert.isTrue(customerRepository.findByFirstName("Vivi").size() > 0, "外层事务提交,Vivi保存成功");
        Assert.isTrue(customerRepository.findByFirstName("Tim").size() == 0, "内层事务回滚,Tim保存失败");
    }

    @Test
    void testPropagationNested2(){
        try {
            transactionService.testPropagationNested2();
        } catch (Exception e) {
        }
        Assert.isTrue(customerRepository.findByFirstName("Vivi").size() == 0, "外层事务回滚,Vivi保存失败");
        Assert.isTrue(customerRepository.findByFirstName("Tim").size() == 0, "内层事务回滚,Tim保存失败");
    }

}
