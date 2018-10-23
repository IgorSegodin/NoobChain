package org.isegodin.noob.chain.util;

import org.junit.Assert;
import org.junit.Test;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author i.segodin
 */
public class CryptoUtilTest {

    private static final KeyPair senderKeyPair = CryptoUtil.generateKeyPair();
    private static final KeyPair receiverKeyPair = CryptoUtil.generateKeyPair();

    @Test
    public void testEncryptVerify() {
        String transactionData = TransactionUtil.generateTransactionSignatureData(
                senderKeyPair.getPublic(),
                receiverKeyPair.getPublic(),
                10
        );

        byte[] sign = CryptoUtil.signECDSA(senderKeyPair.getPrivate(), transactionData);

        Assert.assertTrue(CryptoUtil.verifyECDSA(senderKeyPair.getPublic(), transactionData, sign));
    }

    @Test
    public void testEncryptVerifyFail() {
        String transactionData = TransactionUtil.generateTransactionSignatureData(
                senderKeyPair.getPublic(),
                receiverKeyPair.getPublic(),
                10
        );

        String changedTransactionData = TransactionUtil.generateTransactionSignatureData(
                senderKeyPair.getPublic(),
                receiverKeyPair.getPublic(),
                11
        );

        byte[] sign = CryptoUtil.signECDSA(senderKeyPair.getPrivate(), transactionData);

        Assert.assertFalse(CryptoUtil.verifyECDSA(senderKeyPair.getPublic(), changedTransactionData, sign));
    }

    @Test
    public void testStringToPublicKey() {
        String publicKeyString = CryptoUtil.keyToString(senderKeyPair.getPublic());
        PublicKey publicKey = CryptoUtil.stringToPublicKey(publicKeyString);

        Assert.assertEquals(senderKeyPair.getPublic(), publicKey);
    }

    @Test
    public void testStringToPrivateKey() {
        String privateKeyString = CryptoUtil.keyToString(senderKeyPair.getPrivate());
        PrivateKey privateKey = CryptoUtil.stringToPrivateKey(privateKeyString);

        Assert.assertEquals(senderKeyPair.getPrivate(), privateKey);
    }
}
