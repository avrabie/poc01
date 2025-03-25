package com.execodex.poc01.ds;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyMinHeap2Test {

    MyMinHeap2 myMinHeap = new MyMinHeap2<Integer>();
    @Test
    void peek() {
        myMinHeap.insert(30);
        myMinHeap.insert(20);
        myMinHeap.insert(15);
        myMinHeap.insert(40);
        myMinHeap.insert(50);
        assertEquals(15, myMinHeap.peek());
        Integer poll = (Integer) myMinHeap.poll();
        assertEquals(15, poll);
        assertEquals(20, myMinHeap.peek());
    }

    @Test
    void insert() {
    }

    @Test
    void poll() {
    }
}