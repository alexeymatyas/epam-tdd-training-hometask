package com.epam.tdd;

/**
 * Created by Alexey on 20.02.2017.
 */
public class UnreliableServiceProxy {
    public void doSomeStuff() {
        if(Math.random() > 0.5) {
            throw new RuntimeException("Unexpected exeption happened while calling 3rd-party service");
        }

        return;
    }
}
