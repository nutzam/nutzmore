package org.nutz.plugins.oauth2.server.service;

import java.util.List;

import org.nutz.plugins.oauth2.server.entity.OAuthUser;

public interface OAuthUserService {
    /**
     * 创建用户
     * @param user
     */
    public OAuthUser createUser(OAuthUser user);

    public OAuthUser updateUser(OAuthUser user);

    public void deleteUser(Long userId);

    /**
     * 修改密码
     * @param userId
     * @param newPassword
     */
    public void changePassword(Long userId, String newPassword);

    OAuthUser findOne(Long userId);

    List<OAuthUser> findAll();

    /**
     * 根据用户名查找用户
     * @param username
     * @return
     */
    public OAuthUser findByUsername(String username);

    /**
     * 验证登录
     * @param username 用户名
     * @param password 密码
     * @param salt 盐
     * @param encryptpwd 加密后的密码
     * @return
     */
    boolean checkUser(String username, String password, String salt, String encryptpwd);
}
