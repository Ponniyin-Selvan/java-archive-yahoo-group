package net.namonamaha.archive.ygroups;

import java.util.Date;


public class MessageBean {

	private int messageNo;
	
	private String fromMember;
	
	private String fromMail;
	
	private String subject;
	
	private String threadSubject;
	
	private String plainTextMessage;
	
	private String htmlMessage;
	
	private String originalSource;

	private String fromIp;
	
	private Date sentOn;
	
	
	/**
	 * @return Returns the fromMember.
	 */
	public String getFromMember() {
		return fromMember;
	}
	
	/**
	 * @param fromMember The fromMember to set.
	 */
	public void setFromMember(String fromMember) {
		this.fromMember = fromMember;
	}
	
	/**
	 * @return Returns the htmlMessage.
	 */
	public String getHtmlMessage() {
		return htmlMessage;
	}
	
	/**
	 * @param htmlMessage The htmlMessage to set.
	 */
	public void setHtmlMessage(String htmlMessage) {
		this.htmlMessage = htmlMessage;
	}
	
	/**
	 * @return Returns the messageNo.
	 */
	public int getMessageNo() {
		return messageNo;
	}
	
	/**
	 * @param messageNo The messageNo to set.
	 */
	public void setMessageNo(int messageNo) {
		this.messageNo = messageNo;
	}
	
	/**
	 * @return Returns the originalSource.
	 */
	public String getOriginalSource() {
		return originalSource;
	}
	
	/**
	 * @param originalSource The originalSource to set.
	 */
	public void setOriginalSource(String originalSource) {
		this.originalSource = originalSource;
	}
	
	/**
	 * @return Returns the plainTextMessage.
	 */
	public String getPlainTextMessage() {
		return plainTextMessage;
	}
	
	/**
	 * @param plainTextMessage The plainTextMessage to set.
	 */
	public void setPlainTextMessage(String plainTextMessage) {
		this.plainTextMessage = plainTextMessage;
	}
	
	/**
	 * @return Returns the subject.
	 */
	public String getSubject() {
		return subject;
	}
	
	/**
	 * @param subject The subject to set.
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	/**
	 * @return Returns the threadSubject.
	 */
	public String getThreadSubject() {
		return threadSubject;
	}
	
	/**
	 * @param threadSubject The threadSubject to set.
	 */
	public void setThreadSubject(String threadSubject) {
		this.threadSubject = threadSubject;
	}
	
	/**
	 * @return Returns the sentOn.
	 */
	public Date getSentOn() {
		return sentOn;
	}

	/**
	 * @param sentOn The sentOn to set.
	 */
	public void setSentOn(Date sentOn) {
		this.sentOn = sentOn;
	}

	/**
	 * @return Returns the fromIp.
	 */
	public String getFromIp() {
		return fromIp;
	}
	/**
	 * @param fromIp The fromIp to set.
	 */
	public void setFromIp(String fromIp) {
		this.fromIp = fromIp;
	}
	/**
	 * @return Returns the fromMail.
	 */
	public String getFromMail() {
		return fromMail;
	}
	/**
	 * @param fromMail The fromMail to set.
	 */
	public void setFromMail(String fromMail) {
		this.fromMail = fromMail;
	}
}
