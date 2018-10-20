package org.isegodin.noob.chain.util;

import lombok.SneakyThrows;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

/**
 * @author i.segodin
 */
public class KeyUtil {

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    @SneakyThrows
    public static KeyPair generateKeyPair() {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("ECDSA", BouncyCastleProvider.PROVIDER_NAME);
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

        ECGenParameterSpec spec = new ECGenParameterSpec("prime192v1");

        generator.initialize(spec, random);

        return generator.generateKeyPair();
    }

    public static String keyToString(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

}
