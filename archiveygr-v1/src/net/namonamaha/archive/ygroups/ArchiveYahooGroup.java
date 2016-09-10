package net.namonamaha.archive.ygroups;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.httpclient.params.DefaultHttpParams;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.RefreshHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlBreak;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;
import com.gargoylesoftware.htmlunit.html.xpath.HtmlUnitXPath;

public class ArchiveYahooGroup implements RefreshHandler {

	Log log = LogFactory.getLog(ArchiveYahooGroup.class);

	private static final String groupName = System.getProperty("yag_group_name");

	private static final String PROXY_PASSWD = "proxyPasswd";
	private static final String PROXY_USER = "proxyUser";
	private static final String PROXY_PORT = "proxyPort";
	private static final String PROXY_SERVER = "proxyServer";
	private static final String NEXT_ANCHOR = "Next >";
	private static final String MULTIPART_ALTERNATIVE = "multipart/alternative";
	private static final String TEXT_HTML = "text/html";
	private static final String TEXT_PLAIN = "text/plain";
	private static final String UTF_8 = "UTF-8";
	private static final String SUBJECT_REG_EX = "Re:|RE:|Fw:|Fwd:|\\[" + groupName + "+\\]"; //ponniyinselvan+\\]";
	private static final String X_YAHOO_PROFILE = "X-Yahoo-Profile";
	private static final String YG_MESSAGE_TD = "*//td[@class=\"source user\"]";
	private static final String YG_LOGOUT_URI = "http://login.yahoo.com/config/login?logout=1&&.partner=&.intl=us&.done=http%3a%2f%2fmy.yahoo.com%2findex.html&.src=my";
	private static final String YG_LOGIN_URI = "https://login.yahoo.com/config/login";
	
	private static final String IP_INFO[] = {"X-Yahoo-Post-IP", "X-Sender-Ip", 
											 "X-eGroups-Remote-IP", 
											 "X-Originating-IP"};

	private static final String YG_UNAVAILABLE = "The group " + groupName + " is temporarily unavailable";
	
	private final String YG_LOGIN_SUCCESS_TEXT = "Welcome";

	private Session javaxMailSession = null;
	
	private Connection con = null;
	
	private WebClient webClient = null;
	
	private PreparedStatement insertMessage = null;
	private PreparedStatement updateThread = null;
	
	
	public void handleRefresh(Page page, URL url, int wait) throws IOException {
		// Yahoo Login page refreshes every 15 minutes, just ignore it.
	}


	/**
	 * 
	 */
	private void prepareWebClient() {

    	// Time out is set to 15 seconds
		DefaultHttpParams.getDefaultParams().setParameter("http.socket.timeout", 
    			new Integer(30000));

    	String proxyServer = System.getProperty(PROXY_SERVER);
    	int proxyPort = 8080;
    	
    	// If internet access it through proxy then set the appropriate settings
    	// in webclient
    	if (proxyServer != null) {
    		proxyPort = Integer.parseInt(System.getProperty(PROXY_PORT));
    		webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER_6_0, 
        			proxyServer, proxyPort);

        	// If user id and password is required to access the internet then
    		// add the credential to the webclient
    		String proxyUser = System.getProperty(PROXY_USER);
        	if (proxyUser != null) {
        		String proxyPassword = System.getProperty(PROXY_PASSWD);
    			DefaultCredentialsProvider credential = new DefaultCredentialsProvider();
    			credential.addProxyCredentials(proxyUser, proxyPassword, 
    					proxyServer, proxyPort); 
    			webClient.setCredentialsProvider(credential);
        	}
    	} else { // No Proxy, direct access to internet
    		webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER_6_0);
    	}

    	// Don't throw exception if any non html resources fail (css, js, etc.,)
    	webClient.setThrowExceptionOnScriptError(false);
    	// Disable Javascript - Consume less bandwidth
        webClient.setJavaScriptEnabled(false);

        // Refresh is disabled, Yahoo login page refreshes every 15 mins
        webClient.setRefreshHandler(this);
	}

	/**
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private void prepareDatabase() throws ClassNotFoundException, SQLException {
		
        log.info("Connecting to database...");
        
		Class.forName("com.mysql.jdbc.Driver");
		String url = System.getProperty("yag_jdbc_url"); //"jdbc:mysql://mysql.ps.namo-namaha.net:3306/ygrp_ps";
		con = DriverManager.getConnection(url, System.getProperty("jdbc_user_id"), System.getProperty("jdbc_pwd"));
		
		insertMessage = con.prepareStatement("INSERT INTO messages(id,"
					+ "from_member, from_email, subject, thread_subject,"
					+ "plain_text_message, html_message, original_source, "
					+ "sent_on, from_ip) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		updateThread = con.prepareStatement("UPDATE messages "
				+ "SET parent_id = ?, thread_id = ? WHERE id = ? AND "
				+ "parent_id = 0 AND thread_id = 0");
		
		log.info("done");
	}

	private void cleanUp() throws Exception {
		insertMessage.close();
		updateThread.close();
		con.close();
	}


	private HtmlPage login(WebClient webClient, 
    		String uri, String formName, 
    		String[][] parameters, String button) throws Exception {
		
		// Get the login page
        HtmlPage page = getPage(webClient, uri);
        // Get the login form
        HtmlForm form = page.getFormByName(formName);
        // Set all the required values - id, password, etc.
        for (int i = 0 ; i < parameters.length; i++) {
        	String[] param = parameters[i];
           	HtmlInput input = (HtmlInput)form.getInputByName(param[0]);
           	input.setValueAttribute(param[1]);
        }
       	HtmlInput click = (HtmlSubmitInput)form.getInputByName(button);
        // submit the login form
       	HtmlPage homePage = (HtmlPage)click.click();
       	return homePage;
    }

	/**
	 * @param userID
	 * @param password
	 * @throws Exception
	 */
	private void login(String userID, String password) throws Exception {

		log.info("Logging into Yahoo Groups...");

		// Login to Yahoo
		HtmlPage page = login(webClient, 
			  YG_LOGIN_URI, 
				  "login_form",
				  new String[][] {
			        		 	 {"login", userID},
			        		 	 {"passwd", password},
			        		 	 {".src", "ygrp"},
			        		 	 {".done", "http://my.yahoo.com"}
							},
				".save");
		log.info("Logged in");
		
//	        String loginPageText = page.asXml();
//	        if (textCheck(loginPageText, YG_LOGIN_SUCCESS_TEXT)) {
//	        	log.info("Successful"); 
//	        } else {
//	        	log.info("Failed");
//	        	proceed = false;
//	        }
	}

	private HtmlPage getPage(WebClient webClient, 
    		String uri) throws Exception {
		
        URL url = new URL(uri);
        HtmlPage page = null;
		try {
	        page = (HtmlPage)webClient.getPage(url);
		} catch(FailingHttpStatusCodeException fhsce) {
			log.error("Couldn't get the next message", fhsce);
			if (fhsce.getStatusCode() == 999) {
				log.info("Bandwidth Limit Exceeded, Sleeping for 4 hours");
				Thread.sleep(3600000 * 4);
			} else if (fhsce.getStatusCode() == 408) {
				log.info("Request Timed Out, Sleeping for 15 mins");
				Thread.sleep(15 * 60 * 1000);
			}
	        page = getPage(webClient, uri);
		} catch (Exception ex) {
			log.error("Exception Sleep for 15 mins", ex);
			Thread.sleep(5 * 60 * 1000);
	        page = getPage(webClient, uri);
		}
       	return page;
    }
    
	private boolean textCheck(String pageText, String textCheck) 
		throws Exception {
	
	  	return (pageText.indexOf(textCheck) >= 0);
	}

	private void updateThreadInfo(int[][] threadInfo) throws Exception {
		int id = 0;
		int parentId = 0;
		int threadId = threadInfo[0][0];
		for (int j = 0 ; j < threadInfo.length ; j++) {
			try {
				id = threadInfo[j][0];
				parentId = 0;
				if (threadInfo[j][1] > 0) {
					for (int k = j ; k >= 0  ; k--) {
						if (threadInfo[k][1] == (threadInfo[j][1] - 1)) {
							threadInfo[j][2] = threadInfo[k][0];
							break;
						}
					}
				} else {
					threadInfo[j][2] = parentId;
				}
				updateThread.setInt(1, threadInfo[j][2]);
				updateThread.setInt(2, threadId);
				updateThread.setInt(3, id);
				log.info("Updating message " + id + " with Parent " + threadInfo[j][2]);
				updateThread.executeUpdate();
			} catch(Exception ex) {
				log.error("Exception updateThreadInfo", ex);
			}
		}
    }
    
    private void updateThreadInfo(HtmlPage page) throws Exception {

    	HtmlUnitXPath xpathMess = new HtmlUnitXPath("*//table[@id=\"ygrp-msglist\"]//td[@class=\"message \"] | *//table[@id=\"ygrp-msglist\"]//td[@class=\"message footaction\"]");
    
        // Search for <td class="source user"> amd get the content using xpath
        List nodes = xpathMess.selectNodes(page);
        if (nodes != null || nodes.size() > 0) {
        	Object[] nodeArray = nodes.toArray();
        	if (nodeArray != null && nodeArray.length > 0) {
        		int[][] threads = new int[nodeArray.length][];
        		for (int i = 0 ; i < nodeArray.length; i++) {
        			HtmlTableDataCell cell = (HtmlTableDataCell)nodeArray[i];
        			HtmlUnitXPath messUri = new HtmlUnitXPath("*//a[@href]");
        	        
        	        List uriNodes = messUri.selectNodes(cell);
                	Object[] uriNodeArray = uriNodes.toArray();
           			HtmlAnchor messAnchor = (HtmlAnchor)uriNodeArray[0];
        			String uri = messAnchor.getHrefAttribute();
        			int locQ = uri.lastIndexOf("?");
        			int loc = uri.lastIndexOf("/", locQ);
    
        			DomNode child = cell.getFirstChild();
        			if (child.getNextSibling() instanceof HtmlDivision) {
	        			HtmlDivision div = (HtmlDivision)child.getNextSibling();
	        			String divClass = div.getAttributeValue("class");
	        			threads[i] = new int[] {Integer.parseInt(uri.substring(loc + 1, locQ)),
	        					Integer.parseInt(divClass.substring("ygrp-indent".length())), 0};
        			} else {
	        			threads[i] = new int[] {Integer.parseInt(uri.substring(loc + 1, locQ)), 0, 0};
        			}
        		}
            	log.info("Update Thread Info " + ToStringBuilder.reflectionToString(threads));
        		updateThreadInfo(threads);
        	}
        }
    }
	private String getMessageSource(HtmlPage page) throws Exception {
		
		String message = null;
        
		HtmlUnitXPath xpathMess = new HtmlUnitXPath(YG_MESSAGE_TD);
        
        // Search for <td class="source user"> amd get the content using xpath
        List nodes = xpathMess.selectNodes(page);
        
        if (nodes != null || nodes.size() > 0) {
        	Object[] nodeArray = nodes.toArray();
        	if (nodeArray != null && nodeArray.length > 0) {
		        HtmlTableDataCell cell = (HtmlTableDataCell)nodeArray[0];
		        DomNode child = cell.getFirstChild();
		        String line = "";
		        message = "";
		
		        // Go through child nodes and form a line whenever we find a 
		        // <br> tag
		        do {
		 	   	    if (!(child instanceof HtmlBreak)) {
		 	   	    	String content = child.asText(); 
		 	       	    line = line + content;
		 	       	    // add a space to the first line
		 	       	    if ("From".equals(content)) {
		 	       	    	line = line + " ";
		 	       	    }
		 	   	    } else {
		 	   	    	// add a new line if the node is <br>
		 	   	    	message = message + line + "\n";
		 	   	    	line = "";
		 	   	    }
		       		child = child.getNextSibling(); 
		        } while (child != null);
        	} else {
        		log.info("Couldn't get the Node Content");
        	}
        } else {
        	log.info("Couldn't find the Message Pattern using XPath");
        }
		return message;
	}
	
	private String getHeaderValue(MimeMessage message, String header, 
			String defaultValue) throws Exception {
		
		String headerValue = null;
		String[] headers = message.getHeader(header);
		if (headers == null) {
			headerValue = defaultValue;
		} else {
			headerValue = headers[0];
		}
		return headerValue;
	}

	private String getHeaderValue(MimeMessage message, String header) 
		throws Exception {
		return getHeaderValue(message, header, null);
	}

	private String getFromMail(MimeMessage message) throws Exception {
		String from = null;
		InternetAddress fromAddress = (InternetAddress)message.getFrom()[0];
		from = (fromAddress.getPersonal() == null) 
				? fromAddress.getAddress() : fromAddress.getPersonal() 
						+ " " + fromAddress.getAddress();
		return from;
	}
	
	private String getFrom(MimeMessage message) throws Exception {
		// Get the Yahoo id through which he posted the message
		String from = getHeaderValue(message, X_YAHOO_PROFILE);
		
		if (from == null) {
			// if there is no profile then probably he sent the mail through
			// mail, get the from address
			from = getFromMail(message);
		}
		return from;
	}
	
	// Remove all the Re:, RE:, Fw:, Fwd:, [ponniyinselvan] from the subject
	// This will help to see all the replies related to a thread
	// This may not be accurate if there is a duplicate thread
	private String getThreadSubject(String subject) {
		String threadSubject = (subject == null ? "" : subject);
		
		PatternCompiler compiler = new Perl5Compiler();
		PatternMatcher matcher = new Perl5Matcher();
		Pattern pattern = null;
		
		try {
			PatternMatcherInput matcherInput = new PatternMatcherInput(threadSubject);
			for (;;) {
				pattern = compiler.compile(SUBJECT_REG_EX);
				if (matcher.contains(matcherInput, pattern)) {
					MatchResult result = matcher.getMatch();
					for (int i = 0 ; i < result.groups(); i++) {
						threadSubject = threadSubject.substring(0, result.beginOffset(i)) +
							threadSubject.substring(result.endOffset(i));
						matcherInput = new PatternMatcherInput(threadSubject);
					}
				} else {
					break;
				}
			}
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return threadSubject.trim();
	}

	// Get the Mail content for each type (text, html)
	private String getMailContent(Part bodyPart) throws Exception {

		String content = null;
		try {
			content = (String)bodyPart.getContent();
        } catch (UnsupportedEncodingException uex) {
        	// If the encoding type is not understood by the JavaMail 
        	// then convert using UTF-8 and dump it. For eg., if the encoding 
        	// type is x-user-defined then it would throw this exception 
            InputStream is = bodyPart.getInputStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int b;
            while ((b = is.read()) != -1)
                bos.write(b);
            byte[] barray = bos.toByteArray(); //inefficient ! avoid this
            content = new String(barray, UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return content;
    }

	// Based on the type of message set the appropriate value in message bean
	private void setMessageContent(MessageBean messageBean, 
			Part message) throws Exception {
		
		Object messageContent = null;
        if (message.isMimeType(TEXT_PLAIN)) {
   			messageBean.setPlainTextMessage(getMailContent(message));
        } else {
        	messageContent = message.getContent();
	        if (messageContent instanceof MimeMultipart) {
	        	
	       		MimeMultipart multiPart = (MimeMultipart)messageContent;
	       		for (int i = 0 ; i < multiPart.getCount() ; i++) {
	           		Part bodyPart = multiPart.getBodyPart(i);
	           		if (bodyPart.isMimeType(TEXT_PLAIN)) {
	           			messageBean.setPlainTextMessage(getMailContent(bodyPart));
	           		} else if (bodyPart.isMimeType(TEXT_HTML)) {
	           			messageBean.setHtmlMessage(getMailContent(bodyPart));
	           		} else if (bodyPart.isMimeType(MULTIPART_ALTERNATIVE)) {
	           			setMessageContent(messageBean, bodyPart);
	           		} else {
	           			log.info("Ignoring Unknown Content Type " 
	           					+ bodyPart.getContentType() 
	           					+ " Object " + bodyPart.getClass().getName());
	           			//ignore the content, probably an attachment;
	           		}
	       		}
	        } else if (messageContent instanceof String) {
	        	messageBean.setPlainTextMessage((String)messageContent);
	        }
        }
	}
	
	private String getOrigiatingIp(MimeMessage message) throws Exception {

		String ip = null;
		
		for (int i = 0 ; i < IP_INFO.length && ip == null; i++) {
			ip = getHeaderValue(message, IP_INFO[i]);
		}
		if (ip != null) {
			ip = ip.trim();
			if (ip.indexOf("[") >= 0) {
				ip = ip.substring(1, ip.length() - 1);
			}
		}
		return ip;
	}

	// Form message bean from raw message content
	private MessageBean getMessageBean(String messageSource) throws Exception {
		MessageBean bean = null;
		
		if (javaxMailSession == null) {
			javaxMailSession = Session.getDefaultInstance(System.getProperties());
		}
        MimeMessage message = new MimeMessage(javaxMailSession, 
        		new StringBufferInputStream(messageSource));

        bean = new MessageBean();
        
        bean.setFromMember(getFrom(message));
        bean.setFromMail(getFromMail(message));
        bean.setSubject(message.getSubject());
        bean.setThreadSubject(getThreadSubject(message.getSubject()));

        setMessageContent(bean, message);

        bean.setOriginalSource(messageSource);
        bean.setFromIp(getOrigiatingIp(message));
        bean.setSentOn(message.getSentDate());
        
		return bean;
	}
	
	// Create offline file to troubleshoot or to do further processing
	private void storeMessagePage(HtmlPage page, int messageNo) {
		
		String msgNo = "00000" + messageNo;
		msgNo = msgNo.substring(msgNo.length() - 5);
		String fileName = "messages/Message_" + msgNo + ".html";
		try {
			FileWriter writer = new FileWriter(fileName);
			writer.write(page.getWebResponse().getContentAsString());
			writer.close();
		} catch(Exception ex) {
			log.error("Could't write message " + messageNo, ex);
		}
	}
	
	/**
	 * @param messagePage
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private HtmlPage getNextMessage(HtmlPage messagePage) throws IOException, InterruptedException {
		//ygMessageNo = Integer.parseInt((uri.substring(loc + 1)));

		// Get the next message by simulating the click
		// If the status is 999 then the Bandwidth limit exceeded
		// and Yahoo will not allow access to the group, it might
		// take atleast 2-4 hrs to access it again.
		HtmlPage nextMessage = null;
		HtmlAnchor anchor = null;
		boolean errorPage = false;
		try {
			anchor = messagePage.getFirstAnchorByText(NEXT_ANCHOR);
			// Get the Next Link, this will fail if there is no link
			// Assume the last message
			// TODO - Handle last message
			do {
				if (errorPage) {
					log.info("Yahoo Groups Temporarily Unavailable, sleep for 15 mins");
					Thread.sleep(15 * 60 * 1000);
				}
				nextMessage = (HtmlPage)anchor.click();
			} while (errorPage = isErrorPage(nextMessage));
		} catch(FailingHttpStatusCodeException fhsce) {
			log.error("Couldn't get the next message", fhsce);
			if (fhsce.getStatusCode() == 999) {
				log.info("Bandwidth Limit Exceeded, Sleeping for 4 hours");
				Thread.sleep(3600000 * 4);
			} else if (fhsce.getStatusCode() == 408) {
				log.info("Request Timed Out, Sleeping for 15 mins");
				Thread.sleep(15 * 60 * 1000);
			}
			nextMessage = getNextMessage(messagePage);
		} catch(ElementNotFoundException enf) {
			log.info("End of messages reached or some error page");
		} catch (Exception ex) {
			log.error("Exception Sleep for 15 mins", ex);
			Thread.sleep(5 * 60 * 1000);
			nextMessage = getNextMessage(messagePage);
		}
		return nextMessage;
	}
	
	private int getLastMessage() throws SQLException {
		
		PreparedStatement stmt 
			= con.prepareStatement(
					"SELECT IFNULL(MAX(id),1) last_id FROM messages");
		ResultSet rs = stmt.executeQuery();
		int lastMessageNo = -1;
		if (rs.next()) {
			lastMessageNo = rs.getInt("last_id");
		}
		rs.close();
		stmt.close();
		return lastMessageNo;
	}

	private boolean isErrorPage(HtmlPage page) {
		String text = page.asText();
		return (text.indexOf(YG_UNAVAILABLE) >= 0);
	}
	
	private HtmlPage getPage(int messageNo) throws Exception {
		String messageUri 
			= "http://groups.yahoo.com/group/" + groupName + "/message/" 
				+ messageNo + "?source=1&var=1&l=1";
		HtmlPage messagePage = null;
		for (int i = 0 ; i < 10 ; i++) {
	   		// Get the given message using the starting number
	   		messagePage = getPage(webClient, messageUri);
	   		if (!isErrorPage(messagePage)) {
	   			break;
	   		} else {
	   			log.error("Temporarily Unavailable");
	   		}
		}
		return messagePage;
	}
	
	private HtmlPage getLastMessagePage() throws Exception {
		
		int messageNo = getLastMessage();
		log.info("Last Message Archived " + messageNo);
		HtmlPage messagePage = getPage(messageNo);
   		// Get the Next Url Message from which we should start archiving
   		messagePage = getNextMessage(messagePage);
		return messagePage;
	}

	private void archiveToDB(MessageBean bean) throws SQLException {

		try {
			if (bean != null) {
		        insertMessage.setInt(1, bean.getMessageNo());
		        insertMessage.setString(2, bean.getFromMember());
		        insertMessage.setString(3, bean.getFromMail());
		        insertMessage.setString(4, bean.getSubject());
		        insertMessage.setString(5, bean.getThreadSubject());
		        insertMessage.setString(6, bean.getPlainTextMessage());
		        insertMessage.setString(7, bean.getHtmlMessage());
		        insertMessage.setString(8, bean.getOriginalSource());
		        insertMessage.setTimestamp(9, 
		        		new Timestamp(
		        				bean.getSentOn().getTime()));
		        insertMessage.setString(10, bean.getFromIp());
		        // Insert to MySQL database
		        insertMessage.execute();
			}
		} catch(Exception ex) {
			log.info("Couldn't write to db", ex);
		}
	}

	private int getMessageNoFromUri(String uri) {
		
		int loc = uri.lastIndexOf("?");
		
		int messageNo 
			= Integer.parseInt(uri.substring(
					uri.lastIndexOf("/", loc) + 1, loc));
		return messageNo;
	}

	private int getMessageNoFromUri(Page page) {
		
		return getMessageNoFromUri(page.getWebResponse().getUrl().toString());
	}
    
    public void updateThreadInfo(String folder) throws Exception {
    	File folderObj = new File(folder);
    	String[] messageFiles = folderObj.list();
		
        prepareDatabase();
        prepareWebClient();

        try {
	        for (int k = 0 ; k < messageFiles.length ; k++) {
	        	System.out.println("Processing " + k + " Of " + messageFiles.length + " ==>" + messageFiles[k]);
				String messageUri = "http://localhost/amessages/" + messageFiles[k];
    			///String messageUri = "http://localhost/amessages/Message_16038.html";// + messageFiles[k];
	        	HtmlPage page = (HtmlPage)webClient.getPage(messageUri);
	        	log.info("Processing Message " + messageUri);
        		updateThreadInfo(page);
	        }
        } finally {
        	con.close();
        }
    }

    /**
	 * @param totalMessages
	 * @param batchCount
	 * @param waitTime
	 * @throws Exception
	 * @throws SQLException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private void archive(int[][] messages, int noOfMessagesToArchive, 
			int batchCount, int waitTime) 
		throws Exception, SQLException, InterruptedException, IOException {
		
		for (int i = 0 ; i < messages.length ; i++) {
			int from;
			int to;
    		if (messages[i].length == 1) {
    			from = messages[i][0];
    			to = 9999999;
    			if (from > 0) {
    				log.info("Archiving Message " + from);
    			} else {
    				log.info("Archiving all new Message");
    			}
    		} else {
    			from = messages[i][0];
    			to = (messages[i][1] == 0 ? 9999999 : messages[i][1]);
    			log.info("Archiving Message from " + from + " to " + to);
    		}
    		
			int currentMessageNo = 0;
			HtmlPage messagePage = null;
    		
			if (from == 0) {
				messagePage = getLastMessagePage();
			} else {
				messagePage = getPage(from);
			}
			
    		for (int j = from; j <= to ; j++) {
    			
				if (messagePage == null) {
					break;
				}
    			int messageNo = 0;
    			
    			// Download given number of messages
				messageNo = getMessageNoFromUri(messagePage);
				if ((noOfMessagesToArchive != -1 
						&& currentMessageNo >= noOfMessagesToArchive) 
						|| messageNo > to) {
					break;
				}
				
				log.info("Archiving Message " + messageNo + " - "
						+ (++currentMessageNo) + " Of " 
						+ noOfMessagesToArchive);

				// Store the message content locally.
				// To investigate if there is any issue or rerun from
				// offline content instead of going to yahoo groups online
				//storeMessagePage(messagePage, messageNo);

				// Get the Raw Message Source 
				String messageSource = getMessageSource(messagePage);

				if (messageSource != null) {
					// Convert the message to Bean using JavaMail Component
					boolean successful = true;
					MessageBean bean = getMessageBean(messageSource);
					try {
						con.setAutoCommit(false);
						if (bean != null) {
				   			bean.setMessageNo(messageNo);
				   			archiveToDB(bean);
				   		    log.info("Archived Message " + messageNo);
						} else {
							log.info("Couldn't get Message Bean");
						}
						updateThreadInfo(messagePage);
					} catch(Exception ex) {
						successful = false;
					} finally {
						if (successful) {
							con.commit();
						} else {
							con.rollback();
						}
					}
				} else {
					log.info("Couldn't get Message Source");
				}
				// Sleep after archiving x number of messages
				if ((currentMessageNo % batchCount) == 0) {
					Thread.sleep(waitTime);
				}
				messagePage = getNextMessage(messagePage);
    		}
   			log.info("Total Archived Messages " + currentMessageNo);
		}
	}
    
    public void archive(String userID, String password, int[][] ranges, 
    		int totalMessages, int batchCount, int waitTime) throws Exception {

        try {
	    	prepareWebClient();
	        prepareDatabase();

	        login(userID, password);
	        
        	if (totalMessages == -1) {
        		totalMessages = 9999999;
        	}
        	
        	archive(ranges, totalMessages, batchCount, waitTime);
        } catch(Exception ex) {
    		log.error("Couldn't Archive", ex);
        } finally {
        	// Logout 
       		getPage(webClient, YG_LOGOUT_URI);
       		cleanUp();
       		log.info("Logged Out...");
        }
	}

    public int[][] parseCommandLine(String arg) {
    	int ranges[][] = new int[0][0];
    	
    	String arcArr[] = arg.split(",");
    	ranges = new int[arcArr.length][];
    	for (int i = 0 ; i < arcArr.length ; i++) {
    		String rangeArr[] = arcArr[i].split("-");
    		if (rangeArr.length > 1) {
    			ranges[i] = new int[] {Integer.parseInt(rangeArr[0]), Integer.parseInt(rangeArr[1])};
    		} else {
    			ranges[i] = new int[] {Integer.parseInt(rangeArr[0])};
    		}
    	}
    	
//    	for (int i = 0 ; i < ranges.length ; i++) {
//    		if (ranges[i].length > 1) {
//    			System.out.println("Range " + ranges[i][0] + " to " + ranges[i][1]);
//    		} else {
//    			System.out.println("Message " + ranges[i][0]);
//    		}
//    	}
    	return ranges;
    }
    
	public static void main(String[] args) throws Exception {

    	ArchiveYahooGroup archive = new ArchiveYahooGroup();
    	
//    	archive.updateThreadInfo("C:\\Workspaces\\Spider\\YGArchive\\messages");
    	archive.archive(args[0],  // Yahoo Login Id
    			args[1], 		  // Yahoo Login Password 
				archive.parseCommandLine(args[2]), // Download x no of messages
				Integer.parseInt(args[3]), // Download x no of messages
				Integer.parseInt(args[4]), // Batch Count
				Integer.parseInt(args[5])); // Sleep time for each Batch Count
		
	}
}
