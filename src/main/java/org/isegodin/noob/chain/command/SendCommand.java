package org.isegodin.noob.chain.command;

import org.isegodin.noob.chain.BlockChain;

import java.security.KeyPair;

/**
 * @author isegodin
 */
public class SendCommand implements Command {

    private final BlockChain blockChain;

    public SendCommand(BlockChain blockChain) {
        this.blockChain = blockChain;
    }

    @Override
    public String getName() {
        return "send";
    }

    @Override
    public String execute(String[] args) {
        if (args == null || args.length < 3) {
            return "Wrong parameters count, should be 3.";
        }

        KeyPair from = getKeysForName(args[0]);
        KeyPair to = getKeysForName(args[2]);

        if (from == null) {
            return "Unknown from: " + args[0];
        }

        if (to == null) {
            return "Unknown to: " + args[2];
        }

        float value = 0;

        try {
            value = Float.parseFloat(args[1]);
        } catch (NumberFormatException e) {
            return "Bad number format: " + args[1];
        }

        blockChain.send(from, to.getPublic(), value);

        return "Ok";
    }

    private KeyPair getKeysForName(String name) {
        switch (name) {
            case "bob":
                return BlockChain.bobKeyPair;
            case "steve":
                return BlockChain.steveKeyPair;
            case "noob":
                return BlockChain.noobKeyPair;
            default:
                return null;
        }
    }
}
