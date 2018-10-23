package org.isegodin.noob.chain.util;

import org.isegodin.noob.chain.data.Block;
import org.isegodin.noob.chain.data.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author i.segodin
 */
public class BlockUtil {

    public static int difficulty = 1;

    public static Block createNewBlock(List<Transaction> transactions, String previousHash) {
        long timestamp = System.currentTimeMillis();
        return mineBlock(transactions, previousHash, timestamp);
    }

    public static boolean isValidChain(List<Block> chain) {
        String previousHash = "0";
        for (Block block : chain) {

            if(!block.getPreviousHash().equals(previousHash)) {
                return false;
            }

            String calcHash = generateBlockHash(generateMerkleRoot(block.getTransactions()), block.getPreviousHash(), block.getTimestamp(), block.getNonce());

            if (!block.getHash().equals(calcHash)) {
                return false;
            }

            for (Transaction transaction : block.getTransactions()) {
                if (!TransactionUtil.isValidTransaction(transaction)) {
                    return false;
                }
            }

            previousHash = calcHash;
        }
        return true;
    }

    private static String generateBlockHash(String merkleRoot, String previousHash, long timestamp, int nonce) {
        return HashUtil.sha256(merkleRoot + previousHash + timestamp + nonce);
    }

    private static Block mineBlock(List<Transaction> transactions, String previousHash, long timestamp) {
        String target = new String(new char[difficulty]).replace('\0', '0');

        int nonce = 0;

        String merkleRoot = generateMerkleRoot(transactions);

        String newHash = generateBlockHash(merkleRoot, previousHash, timestamp, nonce);

        while (!newHash.substring(0, difficulty).equals(target)) {
            nonce++;
            newHash = generateBlockHash(merkleRoot, previousHash, timestamp, nonce);
        }

        return Block.builder()
                .transactions(transactions)
                .previousHash(previousHash)
                .timestamp(timestamp)
                .nonce(nonce)
                .hash(newHash)
                .build();
    }

    private static String generateMerkleRoot(List<Transaction> transactions) {
        int count = transactions.size();
        List<String> previousTreeLayer = new ArrayList<>();
        for(Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.getId().toString());
        }
        List<String> treeLayer = previousTreeLayer;
        while(count > 1) {
            treeLayer = new ArrayList<>();
            for(int i=1; i < previousTreeLayer.size(); i++) {
                treeLayer.add(HashUtil.sha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }
        return (treeLayer.size() == 1) ? treeLayer.get(0) : "";
    }
}

