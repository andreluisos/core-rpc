package org.jnvim.corerpc.client;

import org.jnvim.corerpc.message.NotificationMessage;
import org.jnvim.corerpc.message.RequestMessage;
import org.jnvim.corerpc.message.ResponseMessage;

import java.io.InputStream;

/**
 * Represents a RPC communication listener It should attach to communication streams and notify when
 * certain notifications and responses occur It should all be run on a separate thread to prevent
 * blocking
 */
public interface RpcListener {

    /**
     * Callback for {@link ResponseMessage} object It should usually be paired with a request and
     * this should be used to take the response
     */
    interface ResponseCallback {
        /**
         * Notifies callback that response has arrived
         *
         * @param responseMessage parsed {@link ResponseMessage}
         */
        void responseReceived(int forId, ResponseMessage responseMessage);
    }

    /**
     * Callback for {@link NotificationMessage} It may be called at any time and is not usually
     * paired with a request
     *
     * <p>It should be handled, but not with high priority, since it is not expected to be blocking
     */
    interface NotificationCallback {
        /**
         * Notifies callback that notification has arrived
         *
         * @param notificationMessage parsed {@link NotificationMessage}
         */
        void notificationReceived(NotificationMessage notificationMessage);
    }

    /**
     * Callback for {@link RequestMessage} It may be called at any time and is not usually paired
     * with a request
     *
     * <p>It should be handled immediately, if possible, since it is expected to be blocking and
     * response is expected
     */
    interface RequestCallback {
        /**
         * Notifies callback that request has arrived
         *
         * @param requestMessage parsed {@link RequestMessage}
         */
        void requestReceived(RequestMessage requestMessage);
    }

    /**
     * Provides a way to listen for certain response (that has passed id) The id should be the same
     * id as request and that is used to match up request and response
     *
     * <p>This should handle a single response and then stop listening for given id (unless it was
     * called again) Certain implementations may not behave this way, but it is recommended
     * implementation
     *
     * @param id ID of the response to listen to (it should match request id)
     * @param callback {@link ResponseCallback} that should be notified once response arrives
     */
    void listenForResponse(int id, ResponseCallback callback);

    /**
     * Starts listening on given {@link InputStream} All events will be passed to callbacks (if any
     * are registered)
     *
     * @param inputStream stream to listen on
     */
    void start(InputStream inputStream);

    /**
     * Stops listening It is not expected for implementation to be reusable after calling this
     * method!
     */
    void stop();

    /**
     * Provides a way to listen for notifications
     *
     * <p>This will handle all notifications and pass them to given callback These messages may
     * safely be ignored, since notifications are not expecting a response
     *
     * @param callback {@link NotificationCallback} that should be notified when notifications
     *     arrive
     */
    void listenForNotifications(NotificationCallback callback);

    /**
     * Provides a way to listen for requests
     *
     * <p>This will handle all requests and pass them to given callback These messages should be
     * handled and a response should be passed back, since caller may possibly be blocked
     *
     * @param callback {@link RequestCallback} that should be notified when requests arrive
     */
    void listenForRequests(RequestCallback callback);
}
