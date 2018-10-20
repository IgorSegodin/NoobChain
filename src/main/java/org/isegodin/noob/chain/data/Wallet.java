package org.isegodin.noob.chain.data;

import lombok.Builder;
import lombok.Data;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author i.segodin
 */
@Data
@Builder
public class Wallet {

    PrivateKey privateKey;
    PublicKey publicKey;
}
