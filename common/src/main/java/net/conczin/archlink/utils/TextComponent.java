package net.conczin.archlink.utils;

import com.mojang.brigadier.Message;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;

public class TextComponent implements Message {

    private final String message;

    public TextComponent(String message) {
        this.message = message;
    }

    @Override
    public String getString() {
        return message;
    }

    public Component getAsComponent() {
        return ComponentUtils.fromMessage(this);
    }
}
