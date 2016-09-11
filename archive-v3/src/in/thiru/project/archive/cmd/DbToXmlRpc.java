package in.thiru.project.archive.cmd;

import in.thiru.project.archive.MessageDetails;
import in.thiru.project.archive.store.impl.XmlRpcStoreImpl;
import in.thiru.project.archive.store.impl.db.DbStoreImpl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class DbToXmlRpc {

    private static Options getOptions() {
        Options options = new Options();
        options.addOption("a", "from-store-class", true,
                "JDBC Driver from we need to copy from");
        options.addOption("b", "from-jdbc-string", true,
                "JDBC String from we need to copy from");
        options.addOption("c", "from-table-prefix", true, "Table prefix to use");
        
        options.addOption("d", "to-store-class", true,
                "JDBC Driver from we need to copy to");
        options.addOption("x", "url", true, "XML RPC url");
        options.addOption("u", "user-id", true, "XML RPC User Id");
        options.addOption("p", "password", true, "XML RPC Password");
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
        options.put("url",
                commandLine.getOptionValue("url"));
        options.put("user-id",
                commandLine.getOptionValue("user-id"));
        options.put("password",
                commandLine.getOptionValue("password"));

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
        /*
         * --from-jdbc-driver --from-jdbc-string --from-table-prefix
         * 
         */
        Map<String, String> options = getOptions(args);
        DbStoreImpl fromStore =
                (DbStoreImpl)Class.forName(options.get("from-store-class"))
                        .newInstance();
        fromStore.setJdbcString(options.get("from-jdbc-string"));
        fromStore.setTablePrefix(options.get("from-table-prefix"));
        fromStore.setMaxRows(10);
        fromStore.setFromMessageKey("40920");
        fromStore.open();

        XmlRpcStoreImpl toStore =
                (XmlRpcStoreImpl)Class.forName(options.get("to-store-class"))
                        .newInstance();
        
        toStore.setXmlRpcUrl(options.get("url"));
        toStore.setUserId(options.get("user-id"));
        toStore.setPassword(options.get("password"));
        toStore.open();

        for (MessageDetails message : fromStore) {
            System.out.println("Copying message " + message.getMessageKey());
            try {
                toStore.addMessage(message);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        fromStore.close();
        toStore.close();
    }
}
