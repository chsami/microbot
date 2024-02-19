package net.runelite.client.plugins.microbot.dashboard;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;

@Slf4j
@Singleton
public class WSClient {

    public WSClient() {
        HubConnection hubConnection = HubConnectionBuilder
                .create("")
                .build();

        hubConnection.on("Send", (message) -> {
            System.out.println("New Message: " + message);
        }, String.class);

        hubConnection.start();

        hubConnection.send("Send", "test");

    }

}
