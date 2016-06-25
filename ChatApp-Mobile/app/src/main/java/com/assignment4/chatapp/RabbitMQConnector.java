package com.assignment4.chatapp;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;

public class RabbitMQConnector {
    public String mServer;
    public String mExchange;
    public String mQueue;
    protected Channel mModel = null;
    protected Connection  mConnection;

    protected boolean Running ;

    protected  String MyExchangeType ;

    /**
     *
     * @param server The server address
     * @param exchange The named exchange
     * @param exchangeType The exchange type name
     */
    public RabbitMQConnector(String server, String exchange,String mQueue, String exchangeType)
    {
        mServer = server;
        mExchange = exchange;
        MyExchangeType = exchangeType;
        this.mQueue = mQueue;
    }

    public void dispose()
    {
        Running = false;

        try {
            if (mConnection!=null)
                mConnection.close();
            if (mModel != null)
                mModel.abort();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Connect to the broker and create the exchange
     * @return success
     */
    public boolean connectToRabbitMQ()
    {
        if(mModel!= null && mModel.isOpen() )//already declared
            return true;
        try
        {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(mServer);
            connectionFactory.setUsername("kapish");
            connectionFactory.setPassword("kapish");
            connectionFactory.setPort(5672);
            mConnection = connectionFactory.newConnection();
            mModel = mConnection.createChannel();
            mModel.exchangeDeclare(mExchange, MyExchangeType, true);
            mModel.queueDeclare(mExchange,false,false,false,null);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
