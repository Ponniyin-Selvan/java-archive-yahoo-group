package in.thiru.project.archive.cmd;

import in.thiru.project.archive.MessageDetails;
import in.thiru.project.archive.store.impl.db.DbStoreImpl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class CopyDbStore {

    private static Options getOptions() {
        Options options = new Options();
        options.addOption("a", "from-store-class", true,
                "JDBC Driver from we need to copy from");
        options.addOption("b", "from-jdbc-string", true,
                "JDBC String from we need to copy from");
        options.addOption("c", "from-table-prefix", true, "Table prefix to use");
        options.addOption("d", "to-store-class", true,
                "JDBC Driver from we need to copy to");
        options.addOption("d", "to-jdbc-string", true,
                "JDBC String from we need to copy to");
        options.addOption("e", "to-table-prefix", true, "Table prefix to use");
        options.addOption("h", "help", false, "print this message");
        return options;
    }

    private static Map<String, String> getOptions(String[] args) {

        // create the command line parser
        CommandLineParser parser = new PosixParser();
        CommandLine commandLine = null;

        try {
            // parse the command line arguments
            commandLine = parser.parse(getOptions(), args);
        } catch (ParseException exp) {
            System.out.println("Unexpected exception:" + exp.getMessage());
        }

        Map<String, String> options = new HashMap<String, String>();

        options.put("from-store-class",
                commandLine.getOptionValue("from-store-class"));
        options.put("from-jdbc-string",
                commandLine.getOptionValue("from-jdbc-string"));
        options.put("from-table-prefix",
                commandLine.getOptionValue("from-table-prefix"));

        options.put("to-store-class",
                commandLine.getOptionValue("to-store-class"));
        options.put("to-jdbc-string",
                commandLine.getOptionValue("to-jdbc-string"));
        options.put("to-table-prefix",
                commandLine.getOptionValue("to-table-prefix"));

        return options;
    }

    /**
     * @param args
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void main(String[] args) throws InstantiationException,
            IllegalAccessException, ClassNotFoundException {
        // TODO Auto-generated method stub
        /*
         * --from-jdbc-driver --from-jdbc-string --from-table-prefix
         * 
         * --to-jdbc-driver --to-jdbc-string --to-table-prefix
         */
        Map<String, String> options = getOptions(args);
        DbStoreImpl fromStore =
                (DbStoreImpl)Class.forName(options.get("from-store-class"))
                        .newInstance();
        fromStore.setJdbcString(options.get("from-jdbc-string"));
        fromStore.setTablePrefix(options.get("from-table-prefix"));
        fromStore.open();

        DbStoreImpl toStore =
                (DbStoreImpl)Class.forName(options.get("to-store-class"))
                        .newInstance();
        toStore.setJdbcString(options.get("to-jdbc-string"));
        toStore.setTablePrefix(options.get("to-table-prefix"));
        toStore.open();

        for (MessageDetails message : fromStore) {
            System.out.println("Copying message " + message.getMessageKey());
            toStore.addMessage(message);
        }
        fromStore.close();
        toStore.close();
    }
}
