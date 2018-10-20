package org.isegodin.noob.chain.data;

import lombok.Data;

import java.security.PublicKey;
import java.util.List;
import java.util.UUID;

/**
 * @author i.segodin
 */
@Data
public class Transaction {

    UUID id;
    PublicKey sender;
    PublicKey receiver;
    float value;
    String hash;
    byte[] signature;
    List<TransactionInput> inputs;
    List<TransactionOutput> outputs;

}
