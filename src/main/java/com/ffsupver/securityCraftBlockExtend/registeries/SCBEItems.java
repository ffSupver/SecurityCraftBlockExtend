package com.ffsupver.securityCraftBlockExtend.registeries;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.ffsupver.securityCraftBlockExtend.SecurityCraftBlockExtend.MODID;

public class SCBEItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static <T extends Item> RegistryObject<T> registerItem(String id,T item){
        return ITEMS.register(id, () -> item);
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
