package com.fortybeans;

import java.io.IOException;

import javax.servlet.AsyncContext;
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
	private ActorRef printer;
	private ActorRef greeter;
	private ActorSystem actorSystem;

	@Override
	public void init() throws ServletException {
		super.init();
		
		actorSystem = ActorSystem.create("ServletDemo");

		printer = actorSystem.actorOf(new Props(Printer.class));
		greeter = actorSystem.actorOf(new Props(Greeter.class));

	}
	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		AsyncContext asyncCtx = request.startAsync();

		// Initialise the printer with the AsyncContext object
		printer.tell(asyncCtx);

		// Send a message to the greeter
		greeter.tell(printer);
		greeter.tell("Darren");
		greeter.tell(new CloseMessage());

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
				printer.tell("Hello, " + message);
			} else if (message instanceof CloseMessage) {
				printer.tell(message);
			} else
				unhandled(message);
		}
	}

	public static class Printer extends UntypedActor {
		AsyncContext asyncCtx;

		public void onReceive(Object message) throws Exception {

			if (message instanceof AsyncContext) {
				asyncCtx = (AsyncContext) message;

			} else if (message instanceof String) {
				asyncCtx.getResponse().getWriter().println(message);
				
			} else if (message instanceof CloseMessage) {
				asyncCtx.getResponse().getWriter().close();
				asyncCtx.complete();
				
			} else
				unhandled(message);
		}

	}

	public static class CloseMessage {
	}

}
