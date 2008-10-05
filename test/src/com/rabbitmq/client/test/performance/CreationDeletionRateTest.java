package com.rabbitmq.client.test.performance;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Channel;

import java.util.Random;


public class CreationDeletionRateTest {

    static Random rand = new Random();

    public static void main(String[] args) throws Exception {

        final int b = 100, q = 100;

        final Connection con = new ConnectionFactory().newConnection("0.0.0.0", 5672);
        Channel channel = con.createChannel();

        String x = newRandomName();

        channel.exchangeDeclare(1, x, "direct");

        String[] queues = new String[b * q];

        int k = 0;

        final long start = System.currentTimeMillis();

        for (int i = 0; i < q; i++) {
            for (int j = 0; j < b; j++) {
                String s = newRandomName();
                queues[k++] = s;
                channel.queueDeclare(1, s);
                channel.queueBind(1, s, x, newRandomName());
            }
        }

        final long split = System.currentTimeMillis();

        for (String qN : queues) {
            channel.queueDelete(1, qN);
        }

        final long stop = System.currentTimeMillis();

        System.err.println("Creation rate: " + (float) (b * q) / (split - start) * 1000 );
        System.err.println("Deletion rate: " + (float) (b * q) / (stop - split) * 1000 );

        channel.close(200, "foo");
        con.close();
    }

    private static String newRandomName() {
        return rand.nextInt() + "-" + System.currentTimeMillis() + "";
    }

}