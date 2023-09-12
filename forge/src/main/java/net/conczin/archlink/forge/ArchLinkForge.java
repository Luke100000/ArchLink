package net.conczin.archlink.forge;

import net.conczin.archlink.ArchLink;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(ArchLink.MOD_ID)
public class ArchLinkForge {
    public ArchLinkForge() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        ArchLink.onServerStarting();
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        ArchLink.onServerStopping();
    }
}