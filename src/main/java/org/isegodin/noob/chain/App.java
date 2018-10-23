package org.isegodin.noob.chain;

import lombok.SneakyThrows;
import org.isegodin.noob.chain.command.BalanceCommand;
import org.isegodin.noob.chain.command.Command;
import org.isegodin.noob.chain.command.HelpCommand;
import org.isegodin.noob.chain.command.SendCommand;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author i.segodin
 */
public class App {

    @SneakyThrows
    public static void main(String[] args) {
        boolean processing = true;

        System.out.println("Type 'help' for help");

        Map<String, Command> commandMap = initCommandMap();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (processing) {
                String line = reader.readLine().trim();
                processing = processLine(line, commandMap);
            }
        }

        System.out.println("End");
    }

    private static boolean processLine(String line, Map<String, Command> commandMap) {
        String commandName = line;
        String[] commandArgs = null;

        int delimeterIdx = line.indexOf(' ');
        if (delimeterIdx > 0) {
            commandName = line.substring(0, delimeterIdx);

            if (line.length() > delimeterIdx + 1) {
                String argsString = line.substring(delimeterIdx + 1);
                commandArgs = Stream.of(argsString.split(" "))
                        .map(String::trim)
                        .filter(s -> s.length() > 0)
                        .toArray(String[]::new);
            }
        }

        if ("exit".equals(commandName)) {
            return false;
        }

        Command command = commandMap.get(commandName);

        if (command == null) {
            System.out.println("Unknown command: " + commandName);
            return true;
        }

        try {
            Optional.ofNullable(command.execute(commandArgs))
                    .ifPresent(System.out::println);
        } catch (Exception e) {
            StringWriter writer = new StringWriter();
            writer.append("Command error ").append(commandName).append(System.lineSeparator());
            e.printStackTrace(new PrintWriter(writer));

            System.err.println(writer.toString());
        }

        return true;
    }

    private static Map<String, Command> initCommandMap() {
        BlockChain blockChain = new BlockChain();

        return Stream.of(
                new HelpCommand(),
                new SendCommand(blockChain),
                new BalanceCommand(blockChain)
        )
                .collect(Collectors.toMap(
                        Command::getName,
                        c -> c
                ));
    }
}
