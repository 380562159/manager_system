package manager_system.model;

import com.alibaba.fastjson.JSON;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class UserInfo {
    private Long userId;
    private String accountName;
    private String role;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    public static UserInfo getUserInfoByBase64(String b64) throws UnsupportedEncodingException {
        byte[] bytes = Base64.getDecoder().decode(b64);
        String s = new String(bytes, "utf-8");
        UserInfo info = JSON.parseObject(s, UserInfo.class);
        return info;
    }
}
