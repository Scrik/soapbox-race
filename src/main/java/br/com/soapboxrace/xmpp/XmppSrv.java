package br.com.soapboxrace.xmpp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class XmppSrv {

	public static ConcurrentHashMap<Long, XmppTalk> xmppClients = new ConcurrentHashMap<Long, XmppTalk>();

	public static void addXmppClient(long personaId, XmppTalk xmppClient) {
		xmppClients.put(personaId, xmppClient);
	}

	public static void sendMsg(long personaId, String msg) {
		if (xmppClients.containsKey(personaId)) {
			XmppTalk xTalk = xmppClients.get(personaId);
			if (xTalk != null)
				xTalk.write(msg);
			else
				System.err.println("xmppClient with the personaId " + personaId + " is attached to a null XmppTalk instance!");
		} else {
			System.err.println("xmppClients doesn't contain personaId " + personaId);
		}
	}

	public static void removeXmppClient(int personaId) {
		xmppClients.remove(personaId);
	}

	public static void main(String[] args) throws Exception {
		new XmppSrv();
	}

	public XmppSrv() {
		new XmppSrvRun().start();
	}

	private static class XmppSrvRun extends Thread {
		public void run() {
			try {
				System.out.println("Xmpp server is running.");
				ServerSocket listener = new ServerSocket(5222);
				try {
					while (true) {
						new Capitalizer(listener.accept()).start();
					}
				} finally {
					listener.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static class Capitalizer extends Thread {
		private Socket socket;
		private XmppTalk xmppTalk;

		public Capitalizer(Socket socket) {
			this.socket = socket;
			xmppTalk = new XmppTalk(this.socket);
			System.out.println("New connection at " + socket);
		}

		public void run() {
			try {
				new XmppHandShake(xmppTalk);
				while (true) {
					String input = xmppTalk.read();
					if (input == null || input.contains("</stream:stream>")) {
						break;
					}
				}
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					System.out.println("Couldn't close a socket, what's going on?");
				}
				System.out.println("Connection with client closed");
			}
		}

	}
}
