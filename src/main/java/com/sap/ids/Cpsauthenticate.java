package com.sap.ids;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.dispatch.Future;
import akka.dispatch.OnSuccess;
import static akka.pattern.Patterns.ask;

public class Cpsauthenticate {

    public static class Counter extends UntypedActor {
        int count = 0;

        public void onReceive(Object message) throws Exception {
            if ("tick".equals(message))
                count++;
            else if ("get".equals(message))
                getSender().tell(count);
            else
                unhandled(message);
        }
    }

    public static void main(String... args) {
        ActorSystem system = ActorSystem.create("Cpsauthenticate");

        ActorRef counter = system.actorOf(new Props(Counter.class));

        counter.tell("tick");
        counter.tell("tick");
        counter.tell("tick");

        Future future = ask(counter, "get", 5000);

        future.onSuccess(new OnSuccess<Integer>() {
            public void onSuccess(Integer count) {
                System.out.println("Count is " + count);
            }
        });

        system.shutdown();
    }
}
