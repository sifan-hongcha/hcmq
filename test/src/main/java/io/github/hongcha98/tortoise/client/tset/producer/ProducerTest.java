package io.github.hongcha98.tortoise.client.tset.producer;

import io.github.hongcha98.tortoise.client.config.TortoiseConfig;
import io.github.hongcha98.tortoise.client.producer.DefaultProducer;
import io.github.hongcha98.tortoise.client.producer.Producer;
import io.github.hongcha98.tortoise.client.tset.dto.User;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ProducerTest {
    private static final String TOPIC = "test-topic";

    public static void main(String[] args) throws Exception {
        TortoiseConfig tortoiseConfig = new TortoiseConfig();
        tortoiseConfig.setGroup("producer-test");
        Producer producer = new DefaultProducer(tortoiseConfig);
        producer.start();
//        producer.createTopic(TOPIC, 8);
        asyncSend(producer, 10);
        producer.close();
    }

    public static void send(Producer producer, int number) {
        long l = System.currentTimeMillis();
        for (int i = 0; i < number; i++) {
            producer.send(TOPIC, "hello world" + i);
        }
        System.out.println("time cost :" + (System.currentTimeMillis() - l));
    }

    public static void asyncSend(Producer producer, int number) throws ExecutionException, InterruptedException {
        CompletableFuture<String>[] taskArray = new CompletableFuture[number];
        long l = System.currentTimeMillis();
        for (int i = 0; i < number; i++) {
            User user = new User();
            user.setName("hello world" + i);
            user.setAge(i);
            taskArray[i] = producer.asyncSend(TOPIC, new HashMap<>(), user, 5);
        }
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(taskArray);
        voidCompletableFuture.get();
        System.out.println("number : " + number + " time cost :" + (System.currentTimeMillis() - l));
    }
}
