package info.fingo.urlopia.mail;

import info.fingo.urlopia.mail.send.MailTemplate;

/**
 * Stores information about the mail
 */
public class Mail {
    private String senderAddress;
    private String senderName;
    private String recipientAddress;
    private String recipientName;
    private String subject;
    private String content;

    public static Builder newBuilder() {
        return new Mail.Builder();
    }

    public Mail () {
        // No-args constructor to build emails
    }

    public Mail(MailTemplate template) {
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

    public String getSenderName() {
        return senderName;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public static class Builder {
        private String senderAddress;
        private String senderName;
        private String recipientAddress;
        private String recipientName;
        private String subject;
        private String content;

        private Builder() {
            // private non-args constructor to prevent create this class independently
        }

        public Builder setSenderAddress(String senderAddress) {
            this.senderAddress = senderAddress;
            return this;
        }

        public Builder setSenderName(String senderName) {
            this.senderName = senderName;
            return this;
        }

        public Builder setRecipientAddress(String recipientAddress) {
            this.recipientAddress = recipientAddress;
            return this;
        }

        public Builder setRecipientName(String recipientName) {
            this.recipientName = recipientName;
            return this;
        }

        public Builder setTemplate(MailTemplate template) {
            this.subject = template.getSubject();
            this.content = template.getContent();
            return this;
        }

        public Builder setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Mail build() {
            if(this.recipientName == null) {
                this.senderName = this.senderAddress;
            }
            if(this.recipientName == null) {
                this.recipientName = this.recipientAddress;
            }
            return new Mail(senderAddress, senderName, recipientAddress, recipientName, subject, content);
        }
    }
}
