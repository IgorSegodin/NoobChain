package org.isegodin.noob.chain.util;

import lombok.SneakyThrows;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

/**
 * @author i.segodin
 */
public class CryptoUtil {

    public static final String ECDSA = "ECDSA";

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    @SneakyThrows
    public static KeyPair generateKeyPair() {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(ECDSA, BouncyCastleProvider.PROVIDER_NAME);
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        ECGenParameterSpec spec = new ECGenParameterSpec("prime192v1");
        generator.initialize(spec, random);
        return generator.generateKeyPair();
    }

    public static String keyToString(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    @SneakyThrows
    public static byte[] signECDSA(PrivateKey privateKey, String data) {
        Signature sign = Signature.getInstance(ECDSA, BouncyCastleProvider.PROVIDER_NAME);
        sign.initSign(privateKey);
        sign.update(data.getBytes());
        return sign.sign();
    }

    @SneakyThrows
    public static boolean verifyECDSA(PublicKey publicKey, String data, byte[] signature) {
        Signature sign = Signature.getInstance(ECDSA, BouncyCastleProvider.PROVIDER_NAME);
        sign.initVerify(publicKey);
        sign.update(data.getBytes());
        return sign.verify(signature);
    }
}
