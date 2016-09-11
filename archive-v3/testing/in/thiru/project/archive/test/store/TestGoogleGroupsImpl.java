package in.thiru.project.archive.test.store;

import in.thiru.project.archive.store.AbstractStore;
import in.thiru.project.archive.store.impl.GoogleGroupsImpl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestGoogleGroupsImpl {

    public static void main(String args[]) {
        AbstractStore googleGroups = new GoogleGroupsImpl();
        googleGroups.open();
        StringBuffer stringBuffer = new StringBuffer();
        try {
           BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
           while (true)
           {
              String string = reader.readLine();
              if (string != null) stringBuffer.append(string).append("\n");
              else break;
           }
        }
        catch (IOException e) {}

        String contents = stringBuffer.toString();
        googleGroups.addMessage("110", contents.trim());
        googleGroups.close();
    }
}
