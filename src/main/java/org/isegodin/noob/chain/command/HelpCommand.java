package org.isegodin.noob.chain.command;

/**
 * @author isegodin
 */
public class HelpCommand implements Command {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String execute(String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append("Help is not implemented")
                .append(System.lineSeparator())
                .append("Muahaha");
        return sb.toString();
    }
}
