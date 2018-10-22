package org.isegodin.noob.chain.data.dto;

import lombok.Builder;
import lombok.Value;
import org.isegodin.noob.chain.data.TransactionInput;

import java.security.PublicKey;
import java.util.List;

/**
 * @author isegodin
 */
@Value
@Builder
public class NewTransaction {

    PublicKey sender;
    PublicKey receiver;
    float value;
    byte[] signature;
    List<TransactionInput> inputs;
}
