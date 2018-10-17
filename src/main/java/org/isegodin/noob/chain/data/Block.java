package org.isegodin.noob.chain.data;

import lombok.Builder;
import lombok.Value;

/**
 * @author i.segodin
 */
@Value
@Builder
public class Block {

    String data;
    long timestamp;
    String previousHash;
    int nonce;
    String hash;
}
