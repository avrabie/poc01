package com.execodex.poc01.ds;

public class MyMinHeap {
    int capacity;
    int[] heap;
    int size;


    public MyMinHeap() {
        this.capacity = 20;
        this.heap = new int[capacity];
        this.size = 0;
    }


    public int peek() {
        if (size == 0) {
            throw new IllegalStateException();
        }
        return heap[0];
    }

    public void insert(int value) {
        checkCapacity();
        heap[size] = value;
        size++;
        heapifyUp();
    }


    public int poll() {
        if (size == 0) {
            throw new IllegalStateException();
        }
        int value = heap[0];
        heap[0] = heap[size - 1];
        size--;
        heapifyDown();
        return value;
    }

    private void heapifyDown() {
        int index = 0;
        while (hasLeftChild(index)) {
            int smallerChildIndex = getLeftChildIndex(index);
            if (hasRightChild(index) && rightChild(index) < leftChild(index)) {
                smallerChildIndex = getRightChildIndex(index);
            }
            if (heap[index] < heap[smallerChildIndex]) {
                break;
            } else {
                swap(index, smallerChildIndex);
            }
            index = smallerChildIndex;
        }
    }

    private int rightChild(int index) {
        return heap[getRightChildIndex(index)];
    }

    private boolean hasRightChild(int index) {
        return getRightChildIndex(index) < size;
    }

    private int getRightChildIndex(int index) {
        return 2 * index + 2;
    }

    private boolean hasLeftChild(int index) {
        return getLeftChildIndex(index) < size;
    }

    private int getLeftChildIndex(int index) {
        return 2 * index + 1;
    }

    private int leftChild(int index) {
        return heap[getLeftChildIndex(index)];
    }

    private void heapifyUp() {
        int index = size - 1;
        while (hasParent(index) && parent(index) > heap[index]) {
            swap(getParentIndex(index), index);
            index = getParentIndex(index);
        }
    }

    private void swap(int parentIndex, int index) {
        int temp = heap[parentIndex];
        heap[parentIndex] = heap[index];
        heap[index] = temp;
    }

    private boolean hasParent(int index) {
        return getParentIndex(index) >= 0;
    }

    private int parent(int index) {
        return heap[getParentIndex(index)];
    }

    private int getParentIndex(int index) {
        if (index == 0) {
            return 0;
        }
        return (index - 1) / 2;
    }

    private void checkCapacity() {

        if (size == capacity) {
            int[] newHeap = new int[capacity * 2];
            for (int i = 0; i < size; i++) {
                newHeap[i] = heap[i];
            }
            heap = newHeap;
            capacity *= 2;
        }
    }
}
