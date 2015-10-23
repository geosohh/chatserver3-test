package com.chat3.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.SocketConnection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class ClientListenerTest {
	
	private Server server = new Server();
	private SocketConnection mockSocket = Mockito.mock(SocketConnection.class);
	private InputStream mockInput = Mockito.mock(InputStream.class);
	private OutputStream mockOutput = Mockito.mock(OutputStream.class);
	private ClientListener client;
	
	@Before
	public void init() throws IOException{
		Mockito.when(mockSocket.openInputStream()).thenReturn(mockInput);
		Mockito.when(mockSocket.openOutputStream()).thenReturn(mockOutput);
		
		client = new ClientListener(server, mockSocket, -1);
	}
	
	@Test
	public void testHtmlRequest() throws IOException{
		Mockito.doAnswer(new Answer<Object>() {
	        @Override
	        public Object answer(InvocationOnMock invocation) throws Throwable {
	            Object[] args = invocation.getArguments();
	            byte[] message = (byte[])args[0];
	            String msg = "HTTP/1.1 200 OK\n"
							+"Accept-Ranges: bytes\n"
							+"Content-Length: 97\n"
							+"Connection: close\n"
							+"Content-Type: text/html\n"
							+"\n"
							+"<html>"
							+"<head><link rel=\"shortcut icon\" href=\"#\"/></head>"
							+"<body><h1>It works!</h1></body>"
							+"</html>";
	            System.out.println("assertingHtml");
	            Assert.assertTrue((new String(message)).equals(msg));
	        	return null;
	        }
	    }).when(mockOutput).write(Mockito.anyVararg());
		
		String requestText = "GET / HTTP/1.1\r\n"
							+"Host: 10.10.26.242\r\n"
							+"Connection: keep-alive\r\n"
							+"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\r\n"
							+"Upgrade-Insecure-Requests: 1\r\n"
							+"User-Agent: Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) "
							+ 												 "Chrome/46.0.2490.71 Safari/537.36\r\n"
							+"Accept-Encoding: gzip, deflate, sdch\r\n"
							+"Accept-Language: pt-BR,pt;q=0.8,en-US;q=0.6,en;q=0.4\r\n"
							+"\r\n";
		client.handleHttpRequest(requestText);
		Assert.assertTrue(!client.hasHandshaked());
	}
	
	@Test
	public void testWebSocketHandshake() throws IOException{
		Mockito.doAnswer(new Answer<Object>() {
	        @Override
	        public Object answer(InvocationOnMock invocation) throws Throwable {
	            Object[] args = invocation.getArguments();
	            byte[] message = (byte[])args[0];
	            String response = "HTTP/1.1 101 Switching Protocols\r\n"+
								  "Upgrade: websocket\r\n"+
								  "Connection: Upgrade\r\n"+
								  "Sec-WebSocket-Accept: s3pPLMBiTxaQ9kYGzzhZRbK+xOo=\r\n"+
								  "\r\n";
	            System.out.println("assertingWebSocket");
	            Assert.assertTrue((new String(message)).equals(response));
	        	return null;
	        }
	    }).when(mockOutput).write(Mockito.anyVararg());
		
		String requestText = "GET /ws/chat HTTP/1.1\r\n"
							+"Host: 10.10.26.242\r\n"
							+"Connection: Upgrade\r\n"
							+"Pragma: no-cache\r\n"
							+"Cache-Control: no-cache\r\n"
							+"Upgrade: websocket\r\n"
							+"Origin: file://\r\n"
							+"Sec-WebSocket-Version: 13\r\n"
							+"User-Agent: Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) "
							+ 												 "Chrome/46.0.2490.71 Safari/537.36\r\n"
							+"Accept-Encoding: gzip, deflate, sdch\r\n"
							+"Accept-Language: pt-BR,pt;q=0.8,en-US;q=0.6,en;q=0.4\r\n"
							+"Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==\r\n"
							+"Sec-WebSocket-Extensions: permessage-deflate; client_max_window_bits\r\n"
							+"\r\n";
		client.handleHttpRequest(requestText);
		Assert.assertTrue(client.hasHandshaked());
	}

}
