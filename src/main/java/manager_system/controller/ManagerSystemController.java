package manager_system.controller;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager_system.exception.UserEndpointsException;
import manager_system.exception.UserInfoHeaderException;
import manager_system.model.UserEndpoints;
import manager_system.model.UserInfo;
import manager_system.utils.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
public class ManagerSystemController {
    private static final Logger log = LoggerFactory.getLogger(ManagerSystemController.class);
    public static final String userInfoKey = "userinfo";
    private static final String roleUser = "user";
    private static final String roleAdmin = "admin";
    private static String[] roleList = new String[]{roleUser, roleAdmin};

    public UserInfo getUserInfoByBase64(String str) {
        if (str == null || str.isEmpty()) {
            throw new UserInfoHeaderException(Message.UserInfoMissingInHeaders);
        }

        UserInfo info = null;
        try {
            info = UserInfo.getUserInfoByBase64(str);
        } catch (UnsupportedEncodingException e) {
            throw new UserInfoHeaderException(Message.UserInfoDecodeFailure);
        }

        return info;
    }

    @PostMapping("/admin/addUser")
    String addUser(@RequestBody UserEndpoints points, HttpServletRequest request, HttpServletResponse response) {
        String userInfo = request.getHeader(userInfoKey);
        String role = getUserInfoByBase64(userInfo).getRole();

        if (!roleAdmin.equals(role)) {
            throw new UserInfoHeaderException(Message.RoleHasNoAuthority);
        }

        String s = JSON.toJSONString(points);

        Integer userId = points.getUserId();
        if (userId == null) {
            throw new UserEndpointsException(Message.UserIdMissing);
        }


        try {
            UserEndpoints.updateEndpoints(points);
        } catch (IOException e) {
            throw new UserEndpointsException("update Endpoints exception: " + e.getMessage());
        }

        return Message.AddUserSuccess; // "addUser success"
    }


    @GetMapping("/user/{resource}")
    String getResource(@PathVariable(name="resource", required=true) String resource, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userInfo = request.getHeader(userInfoKey);
        Long userId = getUserInfoByBase64(userInfo).getUserId();

        List<String> endpoints = UserEndpoints.getEndpointsByUserId(userId);
        for (String endpoint : endpoints) {
            if (resource.equals(endpoint)) {
                return String.format(Message.GetResourceSuccessTemplate, userId, resource);
            }
        }

        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return String.format(Message.GetResourceFailureTemplate, userId, resource);
    }
}
