package stanford.cs194.stanfood.helpers;

import com.sun.mail.imap.IMAPFolder;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.FolderClosedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import javax.mail.search.FlagTerm;

public class EmailExtractor {
    private Session session;
    private Store store;
    private Folder folder;
    // hardcoding protocol and the folder
    // it can be parameterized and enhanced as required
    private String protocol = "imaps";
    private String file = "INBOX";

    public EmailExtractor() { }

    public boolean isLoggedIn() {
        return store.isConnected();
    }

    public void readEmails() throws Exception {
        try {
            login("imap.gmail.com", "free.stanfood", "stanfoodalpaca");
            int msgCount;
            do {
                msgCount = getMessageCount();
                // Fetch unseen messages from inbox folder
                Message[] messages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
                for (Message msg : messages) {
                    processEmail(msg);
                }
                //if a new message came in while reading the messages start the loop over and get all unread messages
            } while (getMessageCount() != msgCount);
            //add listener
            folder.addMessageCountListener(new MessageCountAdapter() {
                @Override
                public void messagesAdded(MessageCountEvent ev) {
                    Message[] messages = ev.getMessages();
                    for (Message msg : messages) {
                        try {
                            processEmail(msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            // wait for new messages
            while (folder.isOpen()) {
                ((IMAPFolder) folder).idle();
                //every 25 minutes poke the server with a folder.getMessageCount() to keep the connection active/open
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                final Runnable pokeInbox = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            folder.getMessageCount();
                        } catch (MessagingException ex) {
                            // do nothing
                        }
                    }
                };
                scheduler.schedule(pokeInbox, 30, TimeUnit.MINUTES);
            }
        } catch (FolderClosedException e) {
            e.printStackTrace();
            System.out.println("error retrieving emails");
            if (store != null) {
                store.close();
            }
            readEmails();//restarts listening for email if the connection times out
        } finally {
            if (store != null) {
                store.close();
            }
        }
    }

    /**
     * Logs in to the mail host server
     */
    public void login(String host, String username, String password) throws Exception {
        URLName url = new URLName(protocol, host, 993, file, username, password);

        if (session == null) {
            Properties props = null;
            try {
                props = System.getProperties();
            } catch (SecurityException sex) {
                props = new Properties();
            }
            session = Session.getInstance(props, null);
        }
        store = session.getStore(url);
        store.connect();
        folder = store.getFolder(url);

        folder.open(Folder.READ_WRITE);
    }

    /**
     * Logs out from the mail host server
     */
    public void logout() throws MessagingException {
        folder.close(false);
        store.close();
        store = null;
        session = null;
    }

    public int getMessageCount() {
        int messageCount = 0;
        try {
            messageCount = folder.getMessageCount();
        } catch (MessagingException me) {
            me.printStackTrace();
        }
        return messageCount;
    }

    public Message[] getMessages() throws MessagingException {
        return folder.getMessages();
    }

    private void processEmail(Message message) throws Exception {
        Date date = message.getSentDate();
        String info = "";
        if (message.getSubject() != null) {
            String subject = message.getSubject();
            info += subject;
        }
        Object content = message.getContent();
        if (content instanceof String) {
            if (content != "") {
                info += "\n" + content;
            }
        } else if (content instanceof Multipart) {
            Multipart multiPart = (Multipart) content;
            int multiPartCount = multiPart.getCount();
            for (int part = 0; part < multiPartCount; part++) {
                BodyPart bodyPart = multiPart.getBodyPart(part);
                Object o;
                o = bodyPart.getContent();
                if (o instanceof String) {
                    if (o != "") {
                        info += "\n" + o;
                    }
                }
            }
        }
        if (info != "") {
            createEvent(info, date);
        }
    }

    private void createEvent(String info, Date date) {
        //TODO
    }

}
