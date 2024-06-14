package manager_system;

import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletResponse;
import manager_system.controller.ManagerSystemController;
import manager_system.model.UserInfo;
import manager_system.utils.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static manager_system.controller.ManagerSystemController.userInfoKey;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class ManagerSystemApplicationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private ManagerSystemController controller;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
        assertThat(controller).isNotNull();
    }

    @Test
    void testAddUserByRoleAdmin() {
        // header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/json");

        UserInfo info = new UserInfo();
        info.setUserId(3L);
        info.setAccountName("Three");
        info.setRole("admin");

        String jstr = JSON.toJSONString(info);
        String b64 = Base64.getEncoder().encodeToString(jstr.getBytes(StandardCharsets.UTF_8));
        headers.add(userInfoKey, b64);

        // body
        Map<String, Object> body = new HashMap<>();
        body.put("userId", 3);
        body.put("endpoint", new String[]{"resource3", "resource5", "resource7"});
        String bodyStr = JSON.toJSONString(body);

        HttpEntity<String> entity = new HttpEntity<>(bodyStr, headers);

        String url = "http://localhost:" + port + "/admin/addUser";
        ResponseEntity<String> res = restTemplate.postForEntity(url, entity, String.class);

        assertThat(res.getStatusCode().value() == HttpServletResponse.SC_OK).isTrue();
        assertThat(res.getBody().equals(Message.AddUserSuccess)).isTrue();
    }

    @Test
    void testAddUserByRoleUser() {
        // header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/json");

        UserInfo info = new UserInfo();
        info.setUserId(5L);
        info.setAccountName("Five");
        info.setRole("user");

        String jstr = JSON.toJSONString(info);
        String b64 = Base64.getEncoder().encodeToString(jstr.getBytes(StandardCharsets.UTF_8));
        headers.add(userInfoKey, b64);

        // body
        Map<String, Object> body = new HashMap<>();
        body.put("userId", 5);
        body.put("endpoint", new String[]{"resource13", "resource15", "resource17"});
        String bodyStr = JSON.toJSONString(body);

        HttpEntity<String> entity = new HttpEntity<>(bodyStr, headers);

        String url = "http://localhost:" + port + "/admin/addUser";
        ResponseEntity<String> res = restTemplate.postForEntity(url, entity, String.class);

        assertThat(res.getStatusCode().value() == HttpServletResponse.SC_FORBIDDEN).isTrue();
        assertThat(res.getBody().equals(Message.RoleHasNoAuthority)).isTrue();
    }

    @Test
    void testGetResourceSuccess() {
        testAddUserByRoleAdmin();

        String resourceName = "resource3";

        // header
        HttpHeaders headers = new HttpHeaders();
        UserInfo info = new UserInfo();
        info.setUserId(3L);
        info.setAccountName("Three");
        info.setRole("user");

        String jstr = JSON.toJSONString(info);
        String b64 = Base64.getEncoder().encodeToString(jstr.getBytes(StandardCharsets.UTF_8));
        headers.add(userInfoKey, b64);

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        String url = "http://localhost:" + port + "/user/" + resourceName;
        ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        assertThat(res.getStatusCode().value() == HttpServletResponse.SC_OK).isTrue();
        assertThat(res.getBody().equals(String.format(Message.GetResourceSuccessTemplate, 3L, resourceName))).isTrue();
    }

    @Test
    void testGetResourceFailure() {
        testAddUserByRoleAdmin();

        String resourceName = "resource23";

        // header
        HttpHeaders headers = new HttpHeaders();
        UserInfo info = new UserInfo();
        info.setUserId(3L);
        info.setAccountName("Three");
        info.setRole("user");

        String jstr = JSON.toJSONString(info);
        String b64 = Base64.getEncoder().encodeToString(jstr.getBytes(StandardCharsets.UTF_8));
        headers.add(userInfoKey, b64);

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        String url = "http://localhost:" + port + "/user/" + resourceName;
        ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        assertThat(res.getStatusCode().value() == HttpServletResponse.SC_NOT_FOUND).isTrue();
        assertThat(res.getBody().equals(String.format(Message.GetResourceFailureTemplate, 3L, resourceName))).isTrue();
    }
}
