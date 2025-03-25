package com.execodex.poc01.ds;

import ch.qos.logback.classic.spi.EventArgUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyMinHeapTest {

    MyMinHeap myMinHeap = new MyMinHeap();

    @Test
    void insert() {
        myMinHeap.insert(30);
        myMinHeap.insert(20);
        myMinHeap.insert(15);
        myMinHeap.insert(40);
        myMinHeap.insert(50);
        Assertions.assertEquals(15, myMinHeap.peek());
        int poll = myMinHeap.poll();
        Assertions.assertEquals(15, poll);
        Assertions.assertEquals(20, myMinHeap.peek());
    }

    @Test
    void peek() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            myMinHeap.peek();
        });
    }

    @Test
    void poll() {
    }
}