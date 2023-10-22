package org.task1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static ArrayBlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    public static ArrayBlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    public static ArrayBlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) {
        int countTexts = 10_000;
        int countLength = 100_000;
        AtomicInteger maxLengthA = new AtomicInteger(-1);
        AtomicInteger maxLengthB = new AtomicInteger(-1);
        AtomicInteger maxLengthC = new AtomicInteger(-1);
        ConcurrentHashMap<Character, String> maxTexts = new ConcurrentHashMap<>();

        new Thread(() -> {
            for (int i = 0; i < countTexts; i++) {
                String newText = generateText("abc", countLength);
                try {
                    queueA.put(newText);
                    queueB.put(newText);
                    queueC.put(newText);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }).start();

        Thread threadA = new Thread(() -> {
            for (int i = 0; i < countTexts; i++) {
                try {
                    String text = queueA.take();
                    int result = countLetters(text, 'a');
                    if (result > maxLengthA.get()) {
                        maxLengthA.set(result);
                        maxTexts.put('a', text);
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        Thread threadB = new Thread(() -> {
            for (int i = 0; i < countTexts; i++) {
                try {
                    String text = queueB.take();
                    int result = countLetters(text, 'b');
                    if (result > maxLengthB.get()) {
                        maxLengthB.set(result);
                        maxTexts.put('b', text);
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        Thread threadC = new Thread(() -> {
            for (int i = 0; i < countTexts; i++) {
                try {
                    String text = queueC.take();
                    int result = countLetters(text, 'c');
                    if (result > maxLengthC.get()) {
                        maxLengthC.set(result);
                        maxTexts.put('c', text);
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        List<Thread> threads = new ArrayList<>(Arrays.asList(threadA, threadB, threadC));
        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        //Если потребуется вывести строку
        //System.out.println("Максимальное кол-во символов a = " + maxLengthA.get() + " , строка = " + maxTexts.get('a'));
        System.out.println("Максимальное кол-во символов a = " + maxLengthA.get());
        System.out.println("Максимальное кол-во символов b = " + maxLengthB.get());
        System.out.println("Максимальное кол-во символов c = " + maxLengthC.get());
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static int countLetters(String text, char x) {
        int counter = 0;
        for (char letter : text.toCharArray()) {
            if (letter == x) {
                counter++;
            }
        }
        return counter;
    }
}