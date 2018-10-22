package org.isegodin.noob.chain.util;

import org.junit.Assert;
import org.junit.Test;

import java.security.KeyPair;

/**
 * @author i.segodin
 */
public class CryptoUtilTest {

    private static final KeyPair senderKeyPair = CryptoUtil.generateKeyPair();
    private static final KeyPair receiverKeyPair = CryptoUtil.generateKeyPair();

    @Test
    public void testEncryptVerify() {
        String transactionData = TransactionUtil.buildSignatureData(
                senderKeyPair.getPublic(),
                receiverKeyPair.getPublic(),
                10
        );

        byte[] sign = CryptoUtil.signECDSA(senderKeyPair.getPrivate(), transactionData);

        Assert.assertTrue(CryptoUtil.verifyECDSA(senderKeyPair.getPublic(), transactionData, sign));
    }

    @Test
    public void testEncryptVerifyFail() {
        String transactionData = TransactionUtil.buildSignatureData(
                senderKeyPair.getPublic(),
                receiverKeyPair.getPublic(),
                10
        );

        String changedTransactionData = TransactionUtil.buildSignatureData(
                senderKeyPair.getPublic(),
                receiverKeyPair.getPublic(),
                11
        );

        byte[] sign = CryptoUtil.signECDSA(senderKeyPair.getPrivate(), transactionData);

        Assert.assertFalse(CryptoUtil.verifyECDSA(senderKeyPair.getPublic(), changedTransactionData, sign));
    }
}
