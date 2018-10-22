package org.isegodin.noob.chain.util;

import java.security.PublicKey;
import java.util.UUID;

/**
 * @author i.segodin
 */
public class TransactionUtil {

    public static String generateHash(PublicKey sender, PublicKey receiver, float value, UUID id) {
        return HashUtil.sha256(
                CryptoUtil.keyToString(sender) +
                        CryptoUtil.keyToString(receiver) +
                        value +
                        id
        );
    }

    public static String buildSignatureData(PublicKey sender, PublicKey receiver, float value) {
        return CryptoUtil.keyToString(sender) + CryptoUtil.keyToString(receiver) + value;
    }
}
