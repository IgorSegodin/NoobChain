package org.isegodin.noob.chain.data;

import lombok.Builder;
import lombok.Value;

import java.security.PublicKey;
import java.util.List;
import java.util.UUID;

/**
 * @author i.segodin
 */
@Value
@Builder
public class Transaction {

    UUID id;
    PublicKey sender;
    PublicKey receiver;
    float value;
    byte[] signature;
    String hash;
    List<TransactionInput> inputs;
    List<TransactionOutput> outputs;

}
