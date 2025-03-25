package com.execodex.poc01.ds;

public class MyMinHeap2<T extends Comparable<T>> {
    int capacity;
    T[] heap;
    int size;


    public MyMinHeap2() {
        this.capacity = 20;
        this.heap = (T[]) new Comparable[capacity];
        this.size = 0;
    }


    public T peek() {
        if (size == 0) {
            throw new IllegalStateException();
        }
        return heap[0];
    }

    public void insert(T value) {
        checkCapacity();
        heap[size] = value;
        size++;
        heapifyUp();
    }


    public T poll() {
        if (size == 0) {
            throw new IllegalStateException();
        }
        T value = heap[0];
        heap[0] = heap[size - 1];
        size--;
        heapifyDown();
        return value;
    }

    private void heapifyDown() {
        int index = 0;
        while (hasLeftChild(index)) {
            int smallerChildIndex = getLeftChildIndex(index);
            if (hasRightChild(index) && rightChild(index).compareTo(leftChild(index)) < 0) {
                smallerChildIndex = getRightChildIndex(index);
            }
            if(heap[index].compareTo(heap[smallerChildIndex]) < 0) {
                break;
            } else {
                swap(index, smallerChildIndex);
            }
            index = smallerChildIndex;
        }
    }

    private T leftChild(int index) {
        return heap[getLeftChildIndex(index)];
    }

    private T rightChild(int index) {
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



    private void heapifyUp() {
        int index = size - 1;
        while (hasParent(index) && parent(index).compareTo(heap[index]) > 0) {
            swap(getParentIndex(index), index);
            index = getParentIndex(index);
        }
    }

    private void swap(int parentIndex, int index) {
        T temp = heap[parentIndex];
        heap[parentIndex] = heap[index];
        heap[index] = temp;
    }

    private boolean hasParent(int index) {
        return getParentIndex(index) >= 0;
    }

    private T parent(int index) {
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
            T[] newHeap = (T[]) new Comparable[capacity * 2];
            for (int i = 0; i < size; i++) {
                newHeap[i] = heap[i];
            }
            heap = newHeap;
            capacity *= 2;
        }
    }
}
