package stanford.cs194.stanfood.helpers;

import android.util.Log;

import com.sun.mail.imap.IMAPFolder;

import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.mail.Address;
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
import javax.mail.internet.InternetAddress;
import javax.mail.search.FlagTerm;

public class EmailExtractor {
    private Session session;
    private Store store;
    private Folder folder;
    // hardcoding protocol and the folder
    // it can be parameterized and enhanced as required
    private String protocol = "imaps";
    private String file = "INBOX";
    private final String[] FOOD_EMAIL_RECIPIENTS = {
            "food@gates.stanford.edu",
            "gates-food@lists.stanford.edu",
            "cindyj@stanford.edu"   // TODO: delete this after testing
    };

    public EmailExtractor() {
        Log.d("email_start", "EmailExtractor: IT HAS BEGUN");
    }

    private boolean senderEmailIsCorrect(Message msg) throws MessagingException {
        Address address = msg.getFrom()[0];
        String recipientEmail = ((InternetAddress)address).getAddress();
        for (String email: FOOD_EMAIL_RECIPIENTS) {
            if (recipientEmail.equals(email)) {
                return true;
            }
        }
        return false;
    }

    public void readEmails() throws Exception {
        try {
            login("imap.gmail.com", "free.stanfood@gmail.com", "stanfoodalpaca");
            Log.d("email_login", "EmailExtractor: I HAVE LOGGED IN");
            int msgCount;
            do {
                msgCount = getMessageCount();
                Log.d("email_#", "EmailExtractor: message count is " + msgCount);
                // Fetch unseen messages from inbox folder
                Message[] messages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
                for (Message msg : messages) {
                    Log.d("email_from", "EmailExtractor: message is from " + ((InternetAddress)(msg.getFrom()[0])).getAddress());
                    if (senderEmailIsCorrect(msg)) {
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
                            if (senderEmailIsCorrect(msg)) {
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

    private boolean isLoggedIn() {
        return store.isConnected();
    }

    /**
     * Logs in to the mail host server
     */
    private void login(String host, String username, String password) throws Exception {
        URLName url = new URLName(protocol, host, 993, file, username, password);

        if (session == null) {
            Properties props;
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
    private void logout() throws MessagingException {
        folder.close(false);
        store.close();
        store = null;
        session = null;
    }

    /**
     * Gets the number of emails in the inbox.
     */
    private int getMessageCount() {
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
        Log.d("email_subject", "processEmail: subject is " + message.getSubject());
        if (message.getSubject() != null) {
            String subject = message.getSubject();
            emailContents.add(subject);
        }
        Object content = message.getContent();
        Log.d("email_content", "processEmail: content is " + content);
        if (content instanceof String) {
            Log.d("email_content1", "processEmail: content1 is " + content);
            if (!content.equals("") && !((String) content).startsWith("<div")) {
                emailContents.add((String)content);
            }
        } else if (content instanceof Multipart) {
            Log.d("email_content2", "processEmail: multipart content");
            Multipart multiPart = (Multipart) content;
            int multiPartCount = multiPart.getCount();
            for (int part = 0; part < multiPartCount; part++) {
                BodyPart bodyPart = multiPart.getBodyPart(part);
                Object o;
                o = bodyPart.getContent();
                if (o instanceof String) {
                    Log.d("email_content_parts", "processEmail: multipart content: " + o);
                    if (!o.equals("") && !((String) o).startsWith("<div")) {
                        emailContents.add((String)o);
                    }
                }
            }
        }
        Log.d("email_contents", "processEmail: email contents list is " + emailContents);
        createEvent(emailContents, date);
    }

    private void createEvent(ArrayList<String> emailContents, Date date) {
        //TODO: preprocess contents to remove empty subject or messages
        // emailContents should contain the subject and body of the email as the first two entries
        String description = "Extracted from gates-food mailing list:\n";
        String subject = emailContents.get(0);
        if (!subject.equals("")) {
            description += subject;
            description += "\n";
        }
        String body = emailContents.get(1);
        if (!body.equals("")) {
            description += body;
        }
        for (int i = description.length() - 1; i >= 0; i--) {
            if (description.endsWith("\n")) {
                description = description.substring(0, i);
            } else {
                break;
            }
        }
        // CREATE NEW EVENT IN DB HERE

        // testing:
        Log.d("email_extractor", "createEvent: contents: " + emailContents + ", date: " + date);
        for (String text : emailContents) {
            if (!text.equals("")) {
                description += text;
                String[] lines = text.split("\n");
                for (String line : lines) {
                    Log.d("email_line", "createEvent: line is " + line);
                }
            }
        }
    }
}
