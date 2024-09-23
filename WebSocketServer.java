//> using lib "io.undertow:undertow-core:2.3.5.Final"
//> using lib "io.undertow:undertow-websockets-jsr:2.3.5.Final"

package java_cask;

import io.undertow.Undertow;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.spi.WebSocketHttpExchange;

import static io.undertow.Handlers.path;
import static io.undertow.Handlers.resource;
import static io.undertow.Handlers.websocket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Stuart Douglas
 */
public class WebSocketServer {

	private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	public static void main(final String[] args) {
		Undertow server = Undertow.builder().addHttpListener(9191, "0.0.0.0")
				.setHandler(path().addPrefixPath("/myapp", websocket(new WebSocketConnectionCallback() {

					@Override
					public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
						scheduler.scheduleAtFixedRate(() -> {
							try {
								// session.getBasicRemote().sendText("hello");
								WebSockets.sendText("hello", channel, null);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}, 0, 1, TimeUnit.SECONDS);
						channel.getReceiveSetter().set(new AbstractReceiveListener() {

							@Override
							protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
								WebSockets.sendText(message.getData(), channel, null);
							}
						});
						channel.resumeReceives();
					}
				})).addPrefixPath("/", resource(new ClassPathResourceManager(WebSocketServer.class.getClassLoader(),
						WebSocketServer.class.getPackage())).addWelcomeFiles("index.html")))
				.build();
		server.start();
	}

}
