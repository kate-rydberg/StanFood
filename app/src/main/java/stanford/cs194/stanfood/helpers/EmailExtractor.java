package stanford.cs194.stanfood.helpers;

import com.sun.mail.imap.IMAPFolder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private final String FOOD_SENDER_EMAIL = "cindyj@stanford.edu";

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
                    if (msg.getFrom().equals(FOOD_SENDER_EMAIL)) {
                        processEmail(msg);
                    }
                }
                // If a new message came in while reading the messages start the loop over and get all unread messages
            } while (getMessageCount() != msgCount);

            // Add listener for new emails
            folder.addMessageCountListener(new MessageCountAdapter() {
                @Override
                public void messagesAdded(MessageCountEvent ev) {
                    try {
                        Message[] messages = ev.getMessages();
                        for (Message msg : messages) {
                            if (msg.getFrom().equals(FOOD_SENDER_EMAIL)) {
                                processEmail(msg);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            // Wait for new messages
            while (folder.isOpen()) {
                ((IMAPFolder) folder).idle();
                // Poke the server every 30 min with a folder.getMessageCount() to keep the connection active/open
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                final Runnable pokeInbox = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            folder.getMessageCount();
                        } catch (MessagingException ex) {
                            // Do nothing
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
            // Restarts listening for email if the connection times out
            readEmails();
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

    /**
     * Gets the number of emails in the inbox.
     */
    public int getMessageCount() {
        int messageCount = 0;
        try {
            messageCount = folder.getMessageCount();
        } catch (MessagingException me) {
            me.printStackTrace();
        }
        return messageCount;
    }

    /**
     * Gets the contents of the emails and creates a new event
     */
    private void processEmail(Message message) throws Exception {
        Date date = message.getSentDate();
        ArrayList<String> emailContents = new ArrayList<>();
        if (message.getSubject() != null) {
            String subject = message.getSubject();
            emailContents.add(subject);
        }
        Object content = message.getContent();
        if (content instanceof String) {
            if (content != "") {
                emailContents.add((String)content);
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
                        emailContents.add((String)o);
                    }
                }
            }
        }
        createEvent(emailContents, date);
    }

    private void createEvent(ArrayList<String> info, Date date) {
        //TODO
    }

}
