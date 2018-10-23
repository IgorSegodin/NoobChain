package org.isegodin.noob.chain.data;

import lombok.Builder;
import lombok.Value;

import java.security.PublicKey;
import java.util.UUID;

/**
 * @author i.segodin
 */
@Value
@Builder
public class TransactionOutput {

    String hash;
    PublicKey receiver;
    double value;
    UUID parentTransactionId;
}
