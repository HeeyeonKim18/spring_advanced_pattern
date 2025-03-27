package hello.proxy.postprocessor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 빈 후처리기는 빈을 조작하고 변경할 수 있는 후킹 포인트다
 * 이것은 빈 객체를 조작하거나 심지어 다른 객체로 바꾸어 버릴 수 있을 정도로 막강하다
 * 여기서 조작이라는 것은 해당 객체의 특정 메서드를 호출하는 것을 뜻한다
 * 이를 사용하면 개발자가 등록하는 모든 빈을 중간에서 조작할 수 있다
 * 즉, 빈 객체를 프록시로 교체하는 것도 가능하다는 뜻이다
 */
public class BeanPostProcessorTest {

    @Test
    void basicConfig() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(BeanPostProcessorConfig.class);

        // 빈 후처리기 A -> B
        // beanA의 이름으로 B 객체가 빈으로 등록된다
        B b = applicationContext.getBean("beanA", B.class);
        b.helloB();

        // A는 빈으로 등록되지 않는다
        Assertions.assertThrows(NoSuchBeanDefinitionException.class, () -> applicationContext.getBean(A.class));
    }

    @Slf4j
    @Configuration
    static class BeanPostProcessorConfig {

        @Bean(name = "beanA")
        public A a(){
            return new A();
        }

        @Bean
        public AToBPostProcessor beanPostProcessor(){
            return new AToBPostProcessor();
        }
    }

    @Slf4j
    static class A{
        public void helloA(){
            log.info("helloA");
        }
    }

    @Slf4j
    static class B{
        public void helloB(){
            log.info("helloB");
        }
    }

    // 빈후처리기
    // A -> B로 객체를 변경
    @Slf4j
    static class AToBPostProcessor implements BeanPostProcessor {

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            log.info("beanName={} bean={}", beanName, bean);
            if(bean instanceof A){
                return new B();
            }
            return bean;
        }
    }
}
