package org.isegodin.noob.chain;

import org.isegodin.noob.chain.data.Block;
import org.isegodin.noob.chain.data.Transaction;
import org.isegodin.noob.chain.data.TransactionInput;
import org.isegodin.noob.chain.data.TransactionOutput;
import org.isegodin.noob.chain.data.dto.NewTransaction;
import org.isegodin.noob.chain.util.BlockUtil;
import org.isegodin.noob.chain.util.CryptoUtil;
import org.isegodin.noob.chain.util.TransactionUtil;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author isegodin
 */
public class BlockChain {

    public static final KeyPair noobKeyPair = new KeyPair(
            CryptoUtil.stringToPublicKey("MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAEjzI4mJkxOk6ueOBKsI980D7rBkDje2vvo/LpPdZPKqaQGLCM2LUEa7EVUPSiXO6W"),
            CryptoUtil.stringToPrivateKey("MHsCAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQEEYTBfAgEBBBj7GLgjbvHDyTMivUi83MwEn40g5NrT4QKgCgYIKoZIzj0DAQGhNAMyAASPMjiYmTE6Tq544Eqwj3zQPusGQON7a++j8uk91k8qppAYsIzYtQRrsRVQ9KJc7pY=")
    );

    public static final KeyPair bobKeyPair = new KeyPair(
            CryptoUtil.stringToPublicKey("MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAEX13exb1w3WmICXKuiZ498orOU5kJ+GgISvM6m6U4l2qSrYoszA4WP9Wyn0oYP9oQ"),
            CryptoUtil.stringToPrivateKey("MHsCAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQEEYTBfAgEBBBiB9DUdT+GKOVldk+Ufa+Frc7LMzrZ7HM6gCgYIKoZIzj0DAQGhNAMyAARfXd7FvXDdaYgJcq6Jnj3yis5TmQn4aAhK8zqbpTiXapKtiizMDhY/1bKfShg/2hA=")
    );

    public static final KeyPair steveKeyPair = new KeyPair(
            CryptoUtil.stringToPublicKey("MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAEPMtBmmcgJq39WKYHgYcLBc7XfBWD9/7+qIMRG+NlDGiqxhZ776fOTGgeBj4XohkW"),
            CryptoUtil.stringToPrivateKey("MHsCAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQEEYTBfAgEBBBjcKtaIh5BztxaFwV3DLi3x+dL5+Vj+gF6gCgYIKoZIzj0DAQGhNAMyAAQ8y0GaZyAmrf1YpgeBhwsFztd8FYP3/v6ogxEb42UMaKrGFnvvp85MaB4GPheiGRY=")
    );

    private final Map<String, TransactionOutput> unspentTransactionOutputMap = new HashMap<>();

    private final Queue<Block> blockQueue = new ConcurrentLinkedQueue<>();

    private final Deque<Transaction> transactionQueue = new ConcurrentLinkedDeque<>();

    private Timer timer;

    public BlockChain() {
        init();
    }

    private void init() {
        Block genesisBlock = createGenesisBlock();

        blockQueue.add(genesisBlock);

        for (Transaction transaction : genesisBlock.getTransactions()) {
            for (TransactionOutput output : transaction.getOutputs()) {
                unspentTransactionOutputMap.put(output.getHash(), output);
            }
        }
    }

    private Block createGenesisBlock() {
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

    public synchronized double getBalance(PublicKey publicKey) {
        return unspentTransactionOutputMap.values().stream()
                .filter(output -> output.getReceiver().equals(publicKey))
                .map(TransactionOutput::getValue)
                .mapToDouble(value -> value)
                .sum();
    }

    public synchronized void send(KeyPair senderKeyPair, PublicKey receiverPublicKey, float value) {
        Transaction transaction = TransactionUtil.process(
                NewTransaction.builder()
                        .sender(senderKeyPair.getPublic())
                        .receiver(receiverPublicKey)
                        .value(value)
                        .signature(
                                CryptoUtil.signECDSA(
                                        senderKeyPair.getPrivate(),
                                        TransactionUtil.generateTransactionSignatureData(senderKeyPair.getPublic(), receiverPublicKey, value)
                                )
                        )
                        .inputs(
                                unspentTransactionOutputMap.values().stream()
                                        .filter(output -> output.getReceiver().equals(senderKeyPair.getPublic()))
                                        .map(output -> TransactionInput.builder().transactionOutputHash(output.getHash()).build())
                                        .collect(Collectors.toList())

                        )
                        .build(),

                unspentTransactionOutputMap
        );

        Optional.ofNullable(timer).ifPresent(Timer::cancel);

        transactionQueue.add(transaction);

        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                List<Transaction> transactionsForBlock = new LinkedList<>();
                for (int i = 0; i < 3; i++) {
                    Transaction tx = transactionQueue.pollFirst();
                    if (tx == null) {
                        break;
                    }
                    transactionsForBlock.add(tx);
                }

                if (transactionsForBlock.isEmpty()) {
                    cancel();
                    return;
                }

                Block newBlock = BlockUtil.createNewBlock(transactionsForBlock, blockQueue.peek().getHash());

                blockQueue.add(newBlock);
            }
        }, TimeUnit.SECONDS.toMillis(5), TimeUnit.SECONDS.toMillis(5));

    }
}
