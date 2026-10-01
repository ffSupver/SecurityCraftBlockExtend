package com.ffsupver.securityCraftBlockExtend.registeries;

import com.ffsupver.securityCraftBlockExtend.SecurityCraftBlockExtend;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.ffsupver.securityCraftBlockExtend.registeries.SCBEBlocks.REINFORCED_REINFORCED_DEEPSLATE;
import static com.ffsupver.securityCraftBlockExtend.registeries.SCBEBlocks.REINFORCED_WET_SPONGE;

public class SCBETabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SecurityCraftBlockExtend.MODID);


    public static final RegistryObject<CreativeModeTab> SECURITY_CRAFT_BLOCK_EXTEND = CREATIVE_MODE_TABS.register("security_craft_block_extend", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .title(Component.translatable("itemGroup.security_craft_block_extend.main"))
            .icon(() -> REINFORCED_REINFORCED_DEEPSLATE.get().asItem().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(REINFORCED_REINFORCED_DEEPSLATE.get());
                output.accept(REINFORCED_WET_SPONGE.get());
            }).build());


    public static void register(IEventBus modEventBus){
        CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(SCBETabs::addCreative);
    }
    private static void addCreative(BuildCreativeModeTabContentsEvent event)
    {
//        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
//        }
    }
}
