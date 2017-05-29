package info.fingo.urlopia.mail;

/**
 * Stores information about the mail
 *
 * @author Tomasz Urbas
 */
public class Mail {
    private String senderAddress;
    private String senderName;
    private String recipientAddress;
    private String recipientName;
    private String subject;
    private String content;

    public Mail () {
        // No-args constructor to build emails
    }

    public Mail(EmailTemplate template) {
        this.subject = template.getSubject();
        this.content = template.getContent();
    }

    public Mail(String senderAddress, String senderName, String recipientAddress, String recipientName, String subject, String content) {
        this.senderAddress = senderAddress;
        this.senderName = senderName;
        this.recipientAddress = recipientAddress;
        this.recipientName = recipientName;
        this.subject = subject;
        this.content = content;
    }

    // PUBLIC METHODS
    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
