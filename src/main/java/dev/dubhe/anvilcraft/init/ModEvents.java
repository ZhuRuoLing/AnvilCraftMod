package dev.dubhe.anvilcraft.init;

import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.event.AnvilEventListener;
import dev.dubhe.anvilcraft.event.LightningEventListener;

public class ModEvents {
    public static void register(){
        AnvilCraft.EVENT_BUS.register(new AnvilEventListener());
        AnvilCraft.EVENT_BUS.register(new LightningEventListener());
    }
}
