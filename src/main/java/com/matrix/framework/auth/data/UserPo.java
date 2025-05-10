package com.matrix.framework.auth.data;

import com.matrix.framework.core.annotation.validation.Length;
import com.matrix.framework.core.annotation.validation.Required;
import com.matrix.framework.core.common.enums.auth.UserLock;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "auth_user")
public class UserPo {
    @Id
    private Long id;

    @Required
    @Length(min = 2, max = 20)
    private String username;

    private String password;

    private String realName;

    private String email;

    private String phone;

    private Long deptId = 1L;

    private String attr = "rw";

    private String status = UserLock.NORMAL.name();

    private String language = "zh_CN";

    private Long lastLoginTime = 0L;

    private Long createTime = System.currentTimeMillis();

    private Long updateTime = System.currentTimeMillis();

    
    public Long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealName() {
        return this.realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getDeptId() {
        return this.deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return  username +
                ", " + realName +
                ", " + email +
                (phone == null? "" : ", " + phone);
    }
}
