package rabbitmq.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqCustomerCfg {
    final Logger log = LoggerFactory.getLogger(this.getClass());

    @Bean(name = "ConnectionFactory-customer")
    public ConnectionFactory connectionFactory2(@Value("${spring.rabbitmq.2.host:192.168.1.227}") String host,
            @Value("${spring.rabbitmq.2.port:5672}") int port,
            @Value("${spring.rabbitmq.2.username:test}") String username,
            @Value("${spring.rabbitmq.2.password:test}") String password,
            @Value("${spring.rabbitmq.2.vhost:com.test}") String vhost) {
        return this.connectionFactory(host, port, username, password, vhost);
    }

    public CachingConnectionFactory connectionFactory(String host, int port, String username, String password,
            String vhost) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(vhost);
        return connectionFactory;
    }

    @Bean("RabbitAdmin-customer")
    public RabbitAdmin customerRabbitAdmin(
            @Qualifier("ConnectionFactory-customer") ConnectionFactory invokeContainerFactory) {
        return new RabbitAdmin(invokeContainerFactory);
    }

    /*
     * 声明消息队列 启动创建，停止自动删除
     */
    @Autowired
    public void basicQueue1(@Qualifier("RabbitAdmin-customer") RabbitAdmin admin) {
        Queue q = new Queue("q-message-1", true, false, false);
        admin.declareQueue(q);
        admin.declareBinding(
                BindingBuilder.bind(q).to(new TopicExchange("test.message", true, false)).with("topic.name.message1"));
    }

    @Autowired
    public void basicQueue2(@Qualifier("RabbitAdmin-customer") RabbitAdmin admin) {
        Queue q = new Queue("q-message-2", true, false, false);
        admin.declareQueue(q);
        admin.declareBinding(
                BindingBuilder.bind(q).to(new TopicExchange("test.message", true, false)).with("topic.name.message2"));
    }

    @Autowired
    public void basicQueueAll(@Qualifier("RabbitAdmin-customer") RabbitAdmin admin) {
        Queue q = new Queue("q-message-all", true, false, false);
        admin.declareQueue(q);
        // 队列绑定交换机,rountingkey=#匹配所有
        admin.declareBinding(BindingBuilder.bind(q).to(new TopicExchange("test.message", true, false)).with("#"));
    }

    @Autowired
    public void basicQueueNameAll(@Qualifier("RabbitAdmin-customer") RabbitAdmin admin) {
        Queue q = new Queue("q-message-name-all", true, false, false);
        admin.declareQueue(q);
        // 队列绑定交换机
        admin.declareBinding(
                BindingBuilder.bind(q).to(new TopicExchange("test.message", true, false)).with("topic.name.*"));
    }

    @Autowired
    public void basicDelayToRun(@Qualifier("RabbitAdmin-customer") RabbitAdmin admin) {
        Queue q = new Queue("q-delay-to-run", true, false, false);
        admin.declareQueue(q);
        // 队列绑定交换机
        admin.declareBinding(BindingBuilder.bind(q).to(new TopicExchange("test.delay.torun", true, false)).with("#"));
    }

    @Bean(name = "altoAckListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory multiListenerContainer(
            SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer,
            @Qualifier("ConnectionFactory-customer") ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factoryConfigurer.configure(factory, connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        factory.setPrefetchCount(1);
        return factory;
    }

    @Bean(name = "ManualAckListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory listenerContainer(
            SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer,
            @Qualifier("ConnectionFactory-customer") ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factoryConfigurer.configure(factory, connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        factory.setPrefetchCount(1);// 允许多少个未确认的消息数量
        return factory;
    }

}
