package com.sap.ids;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class ServletDemo extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3138593665070329088L;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		ActorSystem system = ActorSystem.create("ServletDemo");

		ActorRef printer = system.actorOf(new Props(Printer.class));
		ActorRef greeter = system.actorOf(new Props(Greeter.class));

		// Initialise the printer with the response object
		printer.tell(response);

		// Send a message to the greeter
		greeter.tell(printer);
		greeter.tell("Darren");
		greeter.tell(new CloseMessage());

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Actors
	 */
	public static class Greeter extends UntypedActor {

		ActorRef printer;

		public void onReceive(Object message) throws Exception {
			if (message instanceof ActorRef) {
				printer = (ActorRef) message;
			} else if (message instanceof String) {
				printer.tell("Hello, " + message, this.getSelf());
			} else if (message instanceof CloseMessage) {
				printer.tell(message);
			} else
				unhandled(message);
		}
	}

	public static class Printer extends UntypedActor {
		HttpServletResponse response;

		public void onReceive(Object message) throws Exception {

			if (message instanceof HttpServletResponse) {
				response = (HttpServletResponse) message;

			} else if (message instanceof String) {
				response.getWriter().println(message);
			} else if (message instanceof CloseMessage) {
				response.getWriter().close();
			} else
				unhandled(message);
		}

	}

	public static class CloseMessage {
	}
}
