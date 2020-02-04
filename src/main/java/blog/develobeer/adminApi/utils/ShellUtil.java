package blog.develobeer.adminApi.utils;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

import java.io.IOException;

public class ShellUtil {
    public static void byCommonsExec(String ...command) throws IOException {
        DefaultExecutor executor = new DefaultExecutor();
        CommandLine cmdLine = CommandLine.parse(command[0]);
        for (int i=1, n=command.length ; i<n ; i++ ) {
            cmdLine.addArgument(command[i]);
        }
        executor.execute(cmdLine);
    }
}
