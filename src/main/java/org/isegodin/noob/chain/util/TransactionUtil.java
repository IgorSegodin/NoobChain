package org.isegodin.noob.chain.util;

import java.security.PublicKey;
import java.util.UUID;

/**
 * @author i.segodin
 */
public class TransactionUtil {

    public static String generateHash(PublicKey sender, PublicKey receiver, float value, UUID id) {
        return HashUtil.sha256(
                KeyUtil.keyToString(sender) +
                        KeyUtil.keyToString(receiver) +
                        value +
                        id
        );
    }
}
