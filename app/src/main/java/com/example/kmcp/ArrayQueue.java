package com.example.kmcp;

import android.util.Log;

//자료가 꽉차게되면 이전자료를 전부 지우고 새로채우는 원형큐
public class ArrayQueue {

    public static final int MAX_SIZE = 64;
    public static int front, rear;

    public static String[] queue;

    public static ArrayQueue AQueue = null;

    // front와 rear이 같은 위치에 있다면 큐가 비어있다는 뜻
    public boolean isEmpty() {
        return front == rear;
    }

    // rear이 front의 바로 전 위치에 있다면 큐가 가득 찼다는 뜻
    public boolean isFull() {
        return (rear + 1)%MAX_SIZE == front;
    }

    // 데이터가 들어갈 땐 rear만 움직인다.
    // rear의 위치는 최근에 들어온 데이터의 위치이다.
    // 즉, 새로운 데이터가 들어오기 위해 먼저 이동해야한다.
    public void enqueue(String data){
        // 큐가 가득차면 모든 큐를 비우고 인덱스를 rear = front = 0으로 돌려놓은뒤 (rear+1)%size 위치에 데이터를 삽입한다.
        if(isFull()) {
            for(int i=0; i<MAX_SIZE; i++){
                queue[i] = null;
            }
            rear = front = 0;
            rear = (rear + 1) % MAX_SIZE;
        }else {
            rear = (rear + 1) % MAX_SIZE;
        }
        queue[rear] = data;
    }

    // 데이터가 나갈 땐 front만 움직인다.
    public String dequeue() {
        String ExtractData = null;
        //큐가 비어있다면 데이터를 뺄 수 없다. rear = front = 0; 으로 돌려놓고 데이터가 비었다는 뜻으로 "0을" 반환한다.
        if(isEmpty()){
            rear = front = 0;
            ExtractData = "0";
        }else {
            front = (front + 1) % MAX_SIZE;
            ExtractData = queue[front];
            queue[front] = null;
        }
        return ExtractData;
    }

    public void displayu() {
        for(int index = front +1; index != (rear + 1) % MAX_SIZE; index = (index + 1)%MAX_SIZE){
            Log.d("AllElementsOfQueue",queue[index].toString());
        }
    }

    public void datanumb() {
        int count = 0;
        if(!isEmpty()){
            for(int index = front +1; index != (rear + 1) % MAX_SIZE; index = (index + 1)%MAX_SIZE){
                // 포문 조건만두면 rear = front = 0 일경우 즉 데이터가 없을경우에도 카운터가 64가 될 수 있다. 그렇기에 if문으로 감쌋음
                count++;
            }
        }
        Log.d("number of data : ",String.valueOf(count));
    }

    public ArrayQueue() {
        front = rear = 0;
        queue = new String[MAX_SIZE];
    }

    public static synchronized ArrayQueue getInstance() {
        if(AQueue==null){
            AQueue = new ArrayQueue();
        }
        return AQueue;
    }

}
