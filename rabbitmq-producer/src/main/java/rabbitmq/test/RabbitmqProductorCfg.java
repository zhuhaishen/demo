package rabbitmq.test;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqProductorCfg {
    final Logger log = LoggerFactory.getLogger(this.getClass());

    @Bean(name = "ConnectionFactory-producer")
    // @Primary
    public ConnectionFactory connectionFactory1(@Value("${spring.rabbitmq.1.host:192.168.1.227}") String host,
            @Value("${spring.rabbitmq.1.port:5672}") int port,
            @Value("${spring.rabbitmq.1.username:test}") String username,
            @Value("${spring.rabbitmq.1.password:test}") String password,
            @Value("${spring.rabbitmq.1.vhost:com.test}") String vhost) {
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
        connectionFactory.setPublisherReturns(true);
        connectionFactory.setPublisherConfirms(true);
        return connectionFactory;
    }

    @Bean("RabbitAdmin-producer")
    public RabbitAdmin customerRabbitAdmin(
            @Qualifier("ConnectionFactory-producer") ConnectionFactory invokeContainerFactory) {
        return new RabbitAdmin(invokeContainerFactory);
    }

    // 因为使用了自定义mq队列，所以不能使用@Bean的方式注册exchange
    @Autowired
    public void basicExchange(@Qualifier("RabbitAdmin-producer") RabbitAdmin admin) {
        admin.declareExchange(new TopicExchange("test.message", true, false));
    }

    // 定义延时队列
    @Autowired
    public void delayQueue(@Qualifier("RabbitAdmin-producer") RabbitAdmin admin) {
        TopicExchange delay = new TopicExchange("test.delay", true, false);
        admin.declareExchange(delay);
        admin.declareExchange(new TopicExchange("test.delay.torun", true, false));
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("x-dead-letter-exchange", "test.delay.torun");
        args.put("x-dead-letter-routing-key", "#");
        Queue q = new Queue("q-delay-no-run", true, false, false, args);
        admin.declareQueue(q);
        admin.declareBinding(BindingBuilder.bind(q).to(delay).with("#"));

        TopicExchange delay5 = new TopicExchange("test.delay.5s", true, false);
        admin.declareExchange(delay5);
        Map<String, Object> args5s = new LinkedHashMap<>();
        args5s.put("x-dead-letter-exchange", "test.delay.torun");
        args5s.put("x-dead-letter-routing-key", "#");
        args5s.put("x-message-ttl", 5000);
        Queue q5 = new Queue("q-delay-no-run5s", true, false, false, args5s);
        admin.declareQueue(q5);
        admin.declareBinding(BindingBuilder.bind(q5).to(delay5).with("#"));

        TopicExchange delay60 = new TopicExchange("test.delay.60s", true, false);
        admin.declareExchange(delay60);
        Map<String, Object> args60 = new LinkedHashMap<>();
        args60.put("x-dead-letter-exchange", "test.delay.torun");
        args60.put("x-dead-letter-routing-key", "#");
        args60.put("x-message-ttl", 60000);
        Queue q60 = new Queue("q-delay-no-run60s", true, false, false, args60);
        admin.declareQueue(q60);
        admin.declareBinding(BindingBuilder.bind(q60).to(delay60).with("#"));
    }

    @Bean(name = "RabbitTemplate-producer")
    public RabbitTemplate rabbitTemplate1(
            @Qualifier("ConnectionFactory-producer") ConnectionFactory connectionFactory) {
        RabbitTemplate myRabbitTemplate = new RabbitTemplate(connectionFactory);
        myRabbitTemplate.setMandatory(true);
        myRabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        myRabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                RabbitmqProductorCfg.this.log.info("消息发送成功:correlationData({}),ack({}),cause({})", correlationData, ack,
                        cause);
            }
        });
        myRabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange,
                    String routingKey) {
                RabbitmqProductorCfg.this.log.info("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}",
                        exchange, routingKey, replyCode, replyText, message);

            }
        });

        return myRabbitTemplate;
    }
}
