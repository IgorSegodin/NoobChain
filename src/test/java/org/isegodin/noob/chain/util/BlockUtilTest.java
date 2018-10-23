package org.isegodin.noob.chain.util;

import org.isegodin.noob.chain.data.Block;
import org.isegodin.noob.chain.data.dto.NewTransaction;
import org.junit.Assert;
import org.junit.Test;

import java.security.KeyPair;
import java.util.Arrays;

/**
 * @author i.segodin
 */
public class BlockUtilTest {

    private static final KeyPair noobKeyPair = CryptoUtil.generateKeyPair();
    private static final KeyPair bobKeyPair = CryptoUtil.generateKeyPair();
    private static final KeyPair steveKeyPair = CryptoUtil.generateKeyPair();

    @Test
    public void testValidChain() {
        Block genesisBlock = createGenesisBlock();

        Assert.assertTrue(BlockUtil.isValidChain(Arrays.asList(genesisBlock)));
    }

    @Test
    public void testSendCoins() {
        Assert.assertTrue(false);
    }

    public Block createGenesisBlock() {
        return BlockUtil.createNewBlock(
                Arrays.asList(
                        TransactionUtil.createTransaction(
                                NewTransaction.builder()
                                        .sender(noobKeyPair.getPublic())
                                        .receiver(noobKeyPair.getPublic())
                                        .value(100)
                                        .signature(
                                                CryptoUtil.signECDSA(
                                                        noobKeyPair.getPrivate(),
                                                        TransactionUtil.generateTransactionSignatureData(noobKeyPair.getPublic(), noobKeyPair.getPublic(), 100)
                                                )
                                        )
                                        .build(), 0
                        )
                ), "0");

    }


}
