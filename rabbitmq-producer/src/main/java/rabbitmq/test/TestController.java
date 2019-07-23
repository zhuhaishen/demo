package rabbitmq.test;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Resource(name = "RabbitTemplate-producer")
    RabbitTemplate rabbitTemplate;
    int count;

    @RequestMapping(path = "/send1")
    public void send1(@RequestParam("m") String m) {
        this.rabbitTemplate.convertAndSend("test.message", "topic.name.message1",
                "send1:" + m + ", " + this.count++ + ", " + new Date().toLocaleString());
    }

    @RequestMapping(path = "/send2")
    public void send2(@RequestParam("m") String m) {
        this.rabbitTemplate.convertAndSend("test.message", "topic.name.message2",
                "send2:" + m + ", " + this.count++ + ", " + new Date().toLocaleString());
    }

    @RequestMapping(path = "/send3")
    public void send3(@RequestParam("m") String m) {
        this.rabbitTemplate.convertAndSend("test.message", "topic.name1.message1",
                "send3:" + m + ", " + this.count++ + ", " + new Date().toLocaleString());
    }

    @RequestMapping(path = "/delay")
    public void delay(@RequestParam("t") int t) throws UnsupportedEncodingException {// 以秒为单位
        MessageProperties mp = new MessageProperties();
        mp.setExpiration(String.valueOf(t * 1000));
        Message m = new Message(
                ("delay:" + t + ", " + this.count++ + ", " + new Date().toLocaleString()).getBytes("utf8"), mp);
        this.rabbitTemplate.send("test.delay", "topic.delay.1", m);
    }

    @RequestMapping(path = "/delay5")
    public void delay5() throws UnsupportedEncodingException {// 以秒为单位
        this.rabbitTemplate.convertAndSend("test.delay.5s", "topic.delay.5s",
                "delay5, " + this.count++ + ", " + new Date().toLocaleString());
    }

    @RequestMapping(path = "/delay60")
    public void delay60() throws UnsupportedEncodingException {// 以秒为单位
        this.rabbitTemplate.convertAndSend("test.delay.60s", "topic.delay.60s",
                "delay60, " + this.count++ + ", " + new Date().toLocaleString());
    }
}
