package org.isegodin.noob.chain.util;

import org.isegodin.noob.chain.data.Transaction;
import org.isegodin.noob.chain.data.TransactionOutput;
import org.isegodin.noob.chain.data.dto.NewTransaction;

import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author i.segodin
 */
public class TransactionUtil {

    public static String generateTransactionHash(PublicKey sender, PublicKey receiver, float value, UUID txId) {
        return HashUtil.sha256(
                CryptoUtil.keyToString(sender) +
                        CryptoUtil.keyToString(receiver) +
                        value +
                        txId
        );
    }

    public static String generateTransactionOutputHash(PublicKey receiver, float value, UUID parentTxId) {
        return HashUtil.sha256(
                CryptoUtil.keyToString(receiver) +
                        Float.toString(value) +
                        parentTxId
        );
    }

    public static String generateTransactionSignatureData(PublicKey sender, PublicKey receiver, float value) {
        return CryptoUtil.keyToString(sender) + CryptoUtil.keyToString(receiver) + value;
    }

    public static Transaction process(NewTransaction newTx, Map<String, TransactionOutput> unspentOutputMap) {
        String txSignData = generateTransactionSignatureData(newTx.getSender(), newTx.getReceiver(), newTx.getValue());

        if (!CryptoUtil.verifyECDSA(newTx.getReceiver(), txSignData, newTx.getSignature())) {
            throw new RuntimeException("Invalid transaction signature: " + newTx);
        }

        List<TransactionOutput> unspentOutputs = Optional.ofNullable(newTx.getInputs()).orElse(new LinkedList<>())
                .stream()
                .map(input -> unspentOutputMap.get(input.getTransactionOutputHash()))
                .collect(Collectors.toList());

        List<TransactionOutput> invalidOutputs = unspentOutputs.stream().filter(out -> !out.getReceiver().equals(newTx.getSender())).collect(Collectors.toList());

        if (!invalidOutputs.isEmpty()) {
            throw new RuntimeException("Transaction inputs refer to other owner outputs");
        }

        float outputValue = BigDecimal.valueOf(
                unspentOutputs.stream()
                        .mapToDouble(TransactionOutput::getValue)
                        .sum()
        ).floatValue();

        float senderRemain = outputValue - newTx.getValue();

        if (senderRemain < 0) {
            throw new RuntimeException("Negative sender balance");
        }

        Transaction tx = createTransaction(newTx, senderRemain);

        tx.getOutputs().forEach(out -> unspentOutputMap.put(out.getHash(), out));

        unspentOutputs.forEach(out -> unspentOutputMap.remove(out.getHash()));

        return tx;
    }

    public static Transaction createTransaction(NewTransaction newTx, float senderRemain) {
        UUID txId = UUID.randomUUID();
        String txHash = generateTransactionHash(newTx.getSender(), newTx.getReceiver(), newTx.getValue(), txId);

        List<TransactionOutput> outputs = new LinkedList<>();

        outputs.add(
                TransactionOutput.builder()
                        .hash(generateTransactionOutputHash(newTx.getReceiver(), newTx.getValue(), txId))
                        .receiver(newTx.getReceiver())
                        .value(newTx.getValue())
                        .parentTransactionId(txId)
                        .build()
        );

        if (senderRemain > 0) {
            outputs.add(
                    TransactionOutput.builder()
                            .hash(generateTransactionOutputHash(newTx.getSender(), senderRemain, txId))
                            .receiver(newTx.getSender())
                            .value(senderRemain)
                            .parentTransactionId(txId)
                            .build()
            );
        }

        return Transaction.builder()
                .id(txId)
                .sender(newTx.getSender())
                .receiver(newTx.getReceiver())
                .value(newTx.getValue())
                .signature(newTx.getSignature())
                .hash(txHash)
                .inputs(newTx.getInputs())
                .outputs(outputs)
                .build();
    }

    public static boolean isValidTransaction(Transaction transaction) {
        String txHash = generateTransactionHash(transaction.getSender(), transaction.getReceiver(), transaction.getValue(), transaction.getId());

        if (!txHash.equals(transaction.getHash())) {
            return false;
        }

        String txSignData = generateTransactionSignatureData(transaction.getSender(), transaction.getReceiver(), transaction.getValue());

        if (!CryptoUtil.verifyECDSA(transaction.getSender(), txSignData, transaction.getSignature())) {
            return false;
        }

        for (TransactionOutput output : transaction.getOutputs()) {
            String outputHash = generateTransactionOutputHash(output.getReceiver(), output.getValue(), transaction.getId());

            if (!outputHash.equals(output.getHash())) {
                return false;
            }
        }

        return true;
    }

}
