package net.conczin.archlink.fabric;

import net.conczin.archlink.ArchLink;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class ArchLinkFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> ArchLink.onServerStarting());

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> ArchLink.onServerStopping());

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                ArchLink.onPlayerLoggedIn(handler.player)
        );

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                ArchLink.onPlayerLoggedOut(handler.player)
        );

        // todo Advancements and death events missing
    }
}