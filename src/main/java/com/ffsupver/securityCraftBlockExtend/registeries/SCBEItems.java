package com.ffsupver.securityCraftBlockExtend.registeries;

import com.ffsupver.securityCraftBlockExtend.item.ChainedUniversalBlockReinforcerItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static com.ffsupver.securityCraftBlockExtend.SecurityCraftBlockExtend.MODID;

public class SCBEItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<ChainedUniversalBlockReinforcerItem> CHAINED_UNIVERSAL_BLOCK_REINFORCER_LV1 = registerItem(
            "chained_universal_block_reinforcer_lv1",
            () -> new ChainedUniversalBlockReinforcerItem(new Item.Properties().defaultDurability(600))
    );
    public static final RegistryObject<ChainedUniversalBlockReinforcerItem> CHAINED_UNIVERSAL_BLOCK_REINFORCER_LV2 = registerItem(
            "chained_universal_block_reinforcer_lv2",
            () -> new ChainedUniversalBlockReinforcerItem(new Item.Properties().defaultDurability(5400))
    );
    public static final RegistryObject<ChainedUniversalBlockReinforcerItem> CHAINED_UNIVERSAL_BLOCK_REINFORCER_LV3 = registerItem(
            "chained_universal_block_reinforcer_lv3",
            () -> new ChainedUniversalBlockReinforcerItem(new Item.Properties().stacksTo(1))
    );

    public static <T extends Item> RegistryObject<T> registerItem(String id, Supplier<T> item){
        return ITEMS.register(id, item);
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
