package org.isegodin.noob.chain.data;

import lombok.Builder;
import lombok.Value;

/**
 * @author i.segodin
 */
@Value
@Builder
public class TransactionInput {

    String transactionOutputHash;
}
