package org.isegodin.noob.chain.util;

import org.isegodin.noob.chain.data.Block;

import java.util.List;

/**
 * @author i.segodin
 */
public class BlockUtil {

    public static int difficulty = 5;

    public static Block createNewBlock(String data, String previousHash) {
        long timestamp = System.currentTimeMillis();
        return mineBlock(data, previousHash, timestamp);
    }

    public static boolean isValidChain(List<Block> chain) {
        String previousHash = "0";
        for (Block block : chain) {

            if(!block.getPreviousHash().equals(previousHash)) {
                return false;
            }

            String calcHash = calcBlockHash(block.getData(), block.getPreviousHash(), block.getTimestamp(), block.getNonce());

            if (!block.getHash().equals(calcHash)) {
                return false;
            }
            previousHash = calcHash;
        }
        return true;
    }

    private static String calcBlockHash(String data, String previousHash, long timestamp, int nonce) {
        return HashUtil.sha256(data + previousHash + timestamp + nonce);
    }

    private static Block mineBlock(String data, String previousHash, long timestamp) {
        String target = new String(new char[difficulty]).replace('\0', '0');

        int nonce = 0;
        String newHash = calcBlockHash(data, previousHash, timestamp, nonce);

        while (!newHash.substring(0, difficulty).equals(target)) {
            nonce++;
            newHash = calcBlockHash(data, previousHash, timestamp, nonce);
        }

        return Block.builder()
                .data(data)
                .previousHash(previousHash)
                .timestamp(timestamp)
                .nonce(nonce)
                .hash(newHash)
                .build();
    }
}

