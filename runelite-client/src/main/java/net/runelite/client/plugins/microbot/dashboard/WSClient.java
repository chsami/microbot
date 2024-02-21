package net.runelite.client.plugins.microbot.dashboard;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.Microbot;

import javax.inject.Singleton;

@Slf4j
@Singleton
public class WSClient {

    @Getter
    public static HubConnection hubConnection;

    public WSClient() {
        hubConnection = HubConnectionBuilder
                .create("http://localhost:5029/chatHub")
                .build();

        hubConnection.on("ReceiveMessage", (user, message) -> {
            Microbot.showMessage("Received a message: " + message + " from: " + user);
        }, String.class, String.class);

        hubConnection.start().blockingAwait();

    }

}
