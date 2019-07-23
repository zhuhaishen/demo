package rabbitmq.test;

import java.io.IOException;
import java.util.Date;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

@Component
public class ConfirmLisener {

    @RabbitHandler
    @RabbitListener(queues = "q-message-1", containerFactory = "ManualAckListenerContainerFactory")
    public void onMessage1(Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        String data = new String(message.getBody(), "utf8");
        System.out.println("onMessage1\t" + Thread.currentThread().getName() + " ManualAck recived message: " + data
                + ", " + message.getMessageProperties().getTimestamp());
        channel.basicAck(tag, false);
    }

    @RabbitHandler
    @RabbitListener(queues = "q-message-2", containerFactory = "ManualAckListenerContainerFactory")
    public void onMessage2(Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        String data = new String(message.getBody(), "utf8");
        System.out.println("onMessage2\t" + Thread.currentThread().getName() + " ManualAck recived message: " + data
                + ", " + message.getMessageProperties().getTimestamp());
        channel.basicAck(tag, false);
    }

    @RabbitHandler
    @RabbitListener(queues = "q-message-all", containerFactory = "ManualAckListenerContainerFactory")
    public void onMessageALL(Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        String data = new String(message.getBody(), "utf8");
        System.out.println("onMessageALL\t" + Thread.currentThread().getName() + " ManualAck recived message: " + data
                + ", " + message.getMessageProperties().getTimestamp());
        channel.basicAck(tag, false);
    }

    @RabbitHandler
    @RabbitListener(queues = "q-message-name-all", containerFactory = "ManualAckListenerContainerFactory")
    public void onMessageNameAll(Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        String data = new String(message.getBody(), "utf8");
        System.out.println("onMessageNameAll\t" + Thread.currentThread().getName() + " ManualAck recived message: "
                + data + ", " + message.getMessageProperties().getTimestamp());
        channel.basicAck(tag, false);
    }

    @RabbitHandler
    @RabbitListener(queues = "q-delay-to-run", containerFactory = "ManualAckListenerContainerFactory")
    public void onMessageDeley(Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        String data = new String(message.getBody(), "utf8");
        System.out.println("onMessageDeley\t" + new Date().toLocaleString() + " " + Thread.currentThread().getName()
                + " ManualAck recived message: " + data + ", " + message.getMessageProperties().getTimestamp());
        channel.basicAck(tag, false);
    }

    // @RabbitListener(queues = "cs-trans", id = "Message", containerFactory =
    // "altoAckListenerContainerFactory")
    // public void altoAck(String message) throws UnsupportedEncodingException {
    // System.out.println(Thread.currentThread().getName() + " altoAck recived
    // message: " + message);
    // synchronized (this.wait2) {
    // try {
    // this.wait2.wait(1000);
    // } catch (InterruptedException e) {
    // }
    // }
    // }

    // @RabbitListener(bindings = @QueueBinding(key = "#", value = @Queue(name =
    // "cs-trans", durable = "true"), exchange = @Exchange(name = "trans", type
    // = "topic", durable = "true")), id = "Message2", containerFactory =
    // "altoAckListenerContainerFactory")
    // public void altoAck2(String message) {
    // System.out.println(Thread.currentThread().getName() + " altoAck recived
    // message: " + message);
    // synchronized (this.wait2) {
    // try {
    // this.wait2.wait(1000);
    // } catch (InterruptedException e) {
    // }
    // }
    // }

}
