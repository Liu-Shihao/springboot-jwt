package com.lsh.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class RSAKeyProvider {

    @Value("${jwt.private-key-path}")
    private String privateKeyPath;

    @Value("${jwt.public-key-path}")
    private String publicKeyPath;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public void init() throws Exception {
        // 检查密钥文件是否存在，如果不存在则生成新的密钥对
        if (!areKeysPresent()) {
            generateKeyPair();
        }
        loadKeys();
    }

    private boolean areKeysPresent() {
        File privateKeyFile = new File(privateKeyPath);
        File publicKeyFile = new File(publicKeyPath);
        return privateKeyFile.exists() && publicKeyFile.exists();
    }

    private void generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // 保存私钥
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded());
        String privateKeyContent = Base64.getEncoder().encodeToString(pkcs8EncodedKeySpec.getEncoded());
        Files.write(new File(privateKeyPath).toPath(), privateKeyContent.getBytes());

        // 保存公钥
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyPair.getPublic().getEncoded());
        String publicKeyContent = Base64.getEncoder().encodeToString(x509EncodedKeySpec.getEncoded());
        Files.write(new File(publicKeyPath).toPath(), publicKeyContent.getBytes());
    }

    private void loadKeys() throws Exception {
        // 加载私钥
        byte[] privateKeyBytes = Base64.getDecoder().decode(Files.readString(new File(privateKeyPath).toPath()));
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        privateKey = keyFactory.generatePrivate(privateKeySpec);

        // 加载公钥
        byte[] publicKeyBytes = Base64.getDecoder().decode(Files.readString(new File(publicKeyPath).toPath()));
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        publicKey = keyFactory.generatePublic(publicKeySpec);
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}