package org.isegodin.noob.chain.command;

/**
 * @author isegodin
 */
public interface Command {

    String getName();

    String execute(String[] args);
}
