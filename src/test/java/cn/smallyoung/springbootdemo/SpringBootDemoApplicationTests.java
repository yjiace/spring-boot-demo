package cn.smallyoung.springbootdemo;

import cn.hutool.crypto.asymmetric.RSA;
import org.junit.jupiter.api.Test;

//@SpringBootTest
class SpringBootDemoApplicationTests {

    @Test
    public void contextLoads() {
        RSA rsa = new RSA();
        System.out.println("PrivateKey: " + rsa.getPrivateKeyBase64());
        System.out.println("PublicKey: " + rsa.getPublicKeyBase64());
    }

}
