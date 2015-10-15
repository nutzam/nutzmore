package org.nutz.plugins.oauth2.server.service;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.oauth2.server.entity.OAuthUser;

@IocBean
public class PasswordHelper {

    private RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();

    private String algorithmName = "md5";
    private int hashIterations = 2;

    public void setRandomNumberGenerator(RandomNumberGenerator randomNumberGenerator) {
        this.randomNumberGenerator = randomNumberGenerator;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public void setHashIterations(int hashIterations) {
        this.hashIterations = hashIterations;
    }

    public void encryptPassword(OAuthUser user) {
        user.setSalt(randomNumberGenerator.nextBytes().toHex());
        String newPassword = new SimpleHash(algorithmName,
                                            user.getPassword(),
                                            ByteSource.Util.bytes(user.getCredentialsSalt()),
                                            hashIterations).toHex();
        user.setPassword(newPassword);
    }

    /**
     * 根据用户名和盐值加密
     * 
     * @param username
     * @param password
     * @param salt
     */
    public String encryptPassword(String username, String password, String salt) {
        String pwd = new SimpleHash(algorithmName,
                                    password,
                                    ByteSource.Util.bytes(username + salt),
                                    hashIterations).toHex();

        return pwd;
    }

}
