package org.isegodin.noob.chain.command;

import org.isegodin.noob.chain.BlockChain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @author isegodin
 */
public class BalanceCommand implements Command {

    private final BlockChain blockChain;

    public BalanceCommand(BlockChain blockChain) {
        this.blockChain = blockChain;
    }

    @Override
    public String getName() {
        return "balance";
    }

    @Override
    public String execute(String[] args) {
        if (args == null || args.length < 1) {
            return "Wrong parameters count, should be 1.";
        }

        switch (args[0].toLowerCase()) {
            case "bob":
                return format(blockChain.getBalance(BlockChain.bobKeyPair.getPublic()));
            case "steve":
                return format(blockChain.getBalance(BlockChain.steveKeyPair.getPublic()));
            case "noob":
                return format(blockChain.getBalance(BlockChain.noobKeyPair.getPublic()));
            default:
                return "0";
        }
    }

    private String format(double value) {
        DecimalFormat df = new DecimalFormat("# ###0.0##########");
        return df.format(BigDecimal.valueOf(value).round(new MathContext(11, RoundingMode.HALF_EVEN)));
    }
}
