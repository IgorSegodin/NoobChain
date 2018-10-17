package org.isegodin.noob.chain.util;

import org.isegodin.noob.chain.data.Block;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author i.segodin
 */
public class BlockUtilTest {

    @Test
    public void testValidChain() {
        Block genesisBlock = BlockUtil.createNewBlock("Genesis block", "0");
        Block block2 = BlockUtil.createNewBlock("Block 2", genesisBlock.getHash());
        Block block3 = BlockUtil.createNewBlock("Block 3", block2.getHash());

        List<Block> chain = Arrays.asList(genesisBlock, block2, block3);

        Assert.assertTrue(BlockUtil.isValidChain(chain));
    }

    @Test
    public void testInValidChainOrder() {
        Block genesisBlock = BlockUtil.createNewBlock("Genesis block", "0");
        Block block2 = BlockUtil.createNewBlock("Block 2", genesisBlock.getHash());
        Block block3 = BlockUtil.createNewBlock("Block 3", block2.getHash());

        List<Block> chain = Arrays.asList(genesisBlock, block3, block2);

        Assert.assertFalse(BlockUtil.isValidChain(chain));
    }

    @Test
    public void testInValidChainHash() {
        Block genesisBlock = BlockUtil.createNewBlock("Genesis block", "0");
        Block block2 = BlockUtil.createNewBlock("Block 2", genesisBlock.getHash());
        Block block3 = BlockUtil.createNewBlock("Block 3", block2.getHash());

        Block block2New = Block.builder()
                .data(block2.getData())
                .timestamp(0)
                .previousHash(block2.getPreviousHash())
                .hash(block2.getHash())
                .build();

        List<Block> chain = Arrays.asList(genesisBlock, block2New, block3);

        Assert.assertFalse(BlockUtil.isValidChain(chain));
    }
}
