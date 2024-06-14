package manager_system;

import jakarta.annotation.PostConstruct;
import manager_system.model.UserEndpoints;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class ManagerSystemApplication {
    public static void main(String[] args) throws IOException {
        SpringApplication.run(ManagerSystemApplication.class, args);
    }

    @PostConstruct
    private void init() throws IOException {
        UserEndpoints.loadProperties();
    }
}
