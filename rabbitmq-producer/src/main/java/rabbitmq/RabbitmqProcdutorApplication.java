package rabbitmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("rabbitmq.test")
public class RabbitmqProcdutorApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitmqProcdutorApplication.class, args);
    }

}
