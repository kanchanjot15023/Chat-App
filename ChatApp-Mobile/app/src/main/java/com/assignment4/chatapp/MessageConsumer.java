package com.assignment4.chatapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
/**
 * Created by Naveen Patidar on 31-Mar-16.
 */
public class MessageConsumer extends RabbitMQConnector {
    public MessageConsumer(String server, String exchange, String mQueue,String exchangeType) {
        super(server, exchange, mQueue,exchangeType);
        this.mQueue = mQueue;
    }

    //The Queue name for this consumer
    private String mQueue;
    private QueueingConsumer MySubscription;
    public static String message;
    //last message to post back
    private byte[] mLastMessage;

    // An interface to be implemented by an object that is interested in messages(listener)
    public interface OnReceiveMessageHandler{

        void onReceiveMessage(byte[] message);
    }



    //A reference to the listener, we can only have one at a time(for now)
    private OnReceiveMessageHandler mOnReceiveMessageHandler;

    /**
     *
     * Set the callback for received messages
     * @param handler The callback
     */
    public void setOnReceiveMessageHandler(OnReceiveMessageHandler handler){
        mOnReceiveMessageHandler = handler;
    }

    private Handler mMessageHandler = new Handler();
    private Handler mConsumeHandler = new Handler();

    // Create runnable for posting back to main thread
    final Runnable mReturnMessage = new Runnable() {
        public void run() {
            mOnReceiveMessageHandler.onReceiveMessage(mLastMessage);
        }
    };

    final Runnable mConsumeRunner = new Runnable() {
        public void run() {
            Consume();
        }
    };

    /**
     * Create Exchange and then start consuming. A binding needs to be added before any messages will be delivered
     */
    @Override
    public boolean connectToRabbitMQ()
    {
        if(super.connectToRabbitMQ())
        {

            try {

                MySubscription = new QueueingConsumer(mModel);
                mModel.basicConsume(mQueue, false, MySubscription);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            if (MyExchangeType.equals("fanout") )
                AddBinding("");//fanout has default binding

            Running = true;
            mConsumeHandler.post(mConsumeRunner);

            return true;
        }
        return false;
    }

    /**
     * Add a binding between this consumers Queue and the Exchange with routingKey
     * @param routingKey the binding key eg GOOG
     */
    public void AddBinding(String routingKey)
    {
        try {
            mModel.queueBind(mQueue, mExchange, routingKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove binding between this consumers Queue and the Exchange with routingKey
     * @param routingKey the binding key eg GOOG
     */
    public void RemoveBinding(String routingKey)
    {
        try {
            mModel.queueUnbind(mQueue, mExchange, routingKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getmLastMessage() {
        return mLastMessage;
    }

    public void setmLastMessage(byte[] mLastMessage) {
        this.mLastMessage = mLastMessage;
    }

    private synchronized void Consume()
    {
        Thread thread = new Thread()
        {
            int flag = 1;

            @Override
            public void run() {
                while(Running){
                    QueueingConsumer.Delivery delivery;
                    if(ChatService.isConnectingToInternet())
                    {
                        try {
                            delivery = MySubscription.nextDelivery();

                            int size = delivery.getBody().length;
                            mLastMessage = new byte[size];
                            mLastMessage = delivery.getBody();

                            message = new String(mLastMessage);
                            Log.v("mLastMessage ",message);
                            mMessageHandler.post(mReturnMessage);
                            try {
                                mModel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                                Thread.sleep(2000);
                            } catch (IOException e) {

                            }
                        } catch (Exception ie) {

                        }
                    }



                }

            }
        };
        thread.start();

    }



    public void dispose(){
        Running = false;
    }
}
