package com.simonvon.basepractice.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.simonvon.basepractice.base.BaseSpringTest;
import com.simonvon.basepractice.spring.transaction.Biz1Service;
import org.junit.Assert;
import org.junit.Test;

import java.util.function.Supplier;

public class HystrixDemoTest extends BaseSpringTest {

    private final Biz1Service biz1Service;

    public HystrixDemoTest(Biz1Service biz1Service) {
        this.biz1Service = biz1Service;
    }

    @Test
    public void testMe() {
        Supplier<String> nameSupplier = biz1Service::sleepAndReturn;
        HelloWordCommand cmd = new HelloWordCommand(nameSupplier);
        String name = cmd.execute();
        Assert.assertEquals("Pass", name);
    }


    public static class HelloWordCommand extends HystrixCommand<String> {

        private final Supplier<String> func;

        public HelloWordCommand(Supplier<String> func) {
            super(HystrixCommand.Setter
                    .withGroupKey(HystrixCommandGroupKey.Factory.asKey("UserService"))
                    .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(5_000)
                    ));
            this.func = func;
        }

        @Override
        protected String run() {
            return func.get();
        }

        @Override
        protected String getFallback() {
            return "Fault";
        }
    }

}


