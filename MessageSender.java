import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * This challenge was sent to me by OpenMarket. They tasked me with completing their class to send messages to phone companies,
 * filtering out spam and delaying messages by the required amount.
 * 
 * This sample is a good example of the following:
 * - Multi-Threaded processing
 * - Functional Interfaces and Java Lambdas
 * - Java 11 type inference
 */

/**
 * This class is where your code should go, to complete the challenge.
 */
public class MessageSender {

    /** The company to send messages to */
    private PhoneCompany phoneCompany;

    /** The number of threads in the pool */
    private static final int POOL_SIZE = 16;
    /** The thread pool to schedule messages on */
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(POOL_SIZE);

    /** Returns true if a string is spam */
    private final Predicate<String> spamCheck = Pattern.compile("(?i)spam").asPredicate();

    /** Maps message IDs to their message instance */
    private final Map<Integer,Message> idMessageMap = new ConcurrentHashMap<>();
    /** Maps message IDs to the number of tries at sending them */
    private final Map<Integer,Integer> idTriesMap = new ConcurrentHashMap<>();

    public MessageSender(){ }

    /*
     * This method will be called once at the beginning, before any messages
     * are sent, giving you a phone company to send them to.
     */
    public void connectToPhoneCompany(PhoneCompany phoneCompany) {
        // Store phoneCompany instance for later
        this.phoneCompany = phoneCompany;
    }

    /*
     * This method will be called by a customer when they want you to send a
     * message for them. Send them to the PhoneCompany.
     */
    public Boolean sendMessage(Message message) {

        // If found to be spam, reject the message
        if(spamCheck.test(message.messageBody))
            return false;

        // Add message to map, so it can be queried later
        idMessageMap.put(message.messageId, message);
        // Schedule the message to be delivered
        scheduleMessage(message);

        // Accept the message
        return true;
    }

    /*
     * This method will be called by the phone company once they have attempted
     * to deliver a message. Outcome will be true if the message was delivered
     * or false if not (meaning you may need to retry it).
     */
    public void messageOutcome(Integer messageId, Boolean outcome) {

        // If successful, do nothing
        if(outcome) return;

        // Get message instance
        var message = idMessageMap.get(messageId);
        // Schedule message to be sent again
        scheduleMessage(message);
    }

    /**
     * Schedules a message to be delivered using the required delay
     *
     * @param message the message to be sent
     */
    private void scheduleMessage(Message message){

        // Get number of tries
        var tries = idTriesMap.getOrDefault(message.messageId, 0);

        // If too many tries, don't send
        if(tries >= 3) return;

        // Array of possible delays, where index = tries
        var delays = new long[]{ message.deliveryDelayMs, 0L, 100L };

        // Schedule delivery with executor service
        executor.schedule(
                () -> phoneCompany.deliverSmsMessage(message),
                delays[tries],
                TimeUnit.MILLISECONDS
        );

        // Increment the number of times we have tried to send the message
        idTriesMap.put(message.messageId, ++tries);
    }
}
