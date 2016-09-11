package in.thiru.project.archive.test.misc;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcSun15HttpTransportFactory;
import org.apache.xmlrpc.client.XmlRpcSunHttpTransport;
import org.apache.xmlrpc.client.XmlRpcTransport;
import org.xml.sax.SAXException;

public class CustomXmlRpcCommonsTransportFactory
        extends XmlRpcSun15HttpTransportFactory {

    Logger log =
            Logger.getLogger(CustomXmlRpcCommonsTransportFactory.class.getName());

    public CustomXmlRpcCommonsTransportFactory(XmlRpcClient pClient) {
        super(pClient);
        // TODO Auto-generated constructor stub
    }

    @Override
    public XmlRpcTransport getTransport() {
        return new LoggingTransport(getClient());
    }

    private class LoggingTransport extends XmlRpcSunHttpTransport {

        public LoggingTransport(XmlRpcClient pClient) {
            super(pClient);
            // TODO Auto-generated constructor stub
        }

        /**
         * Logs the request content in addition to the actual work.
         */
        @Override
        protected void writeRequest(final ReqWriter pWriter)
                throws XmlRpcException {

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                pWriter.write(out);
                System.out.println(out.toString());
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (SAXException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            try {
                super.writeRequest(pWriter);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SAXException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        /**
         * Logs the response from the server, and returns the contents of the
         * response as a ByteArrayInputStream.
         */
        @Override
        protected InputStream getInputStream() throws XmlRpcException {
            InputStream istream = super.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();
            try {
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(istream));
                while (true) {
                    String string = reader.readLine();
                    if (string != null)
                        stringBuffer.append(string).append("\n");
                    else
                        break;
                }
            } catch (IOException e) {
            }
            // if (logger.isDebugEnabled()) {
            // return new ByteArrayInputStream(
            // CustomLoggingUtils.logResponse(logger, istream).getBytes());
            // } else {
            // }
            System.out.println(stringBuffer.toString());
            return istream;
        }
    }
}
