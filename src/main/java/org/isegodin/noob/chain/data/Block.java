package org.isegodin.noob.chain.data;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * @author i.segodin
 */
@Value
@Builder
public class Block {

    String hash;
    String previousHash;
    String merkleRoot;
    List<Transaction> transactions;
    long timestamp;
    int nonce;
}
