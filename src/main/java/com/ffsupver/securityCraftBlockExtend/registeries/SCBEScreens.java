package com.ffsupver.securityCraftBlockExtend.registeries;

import com.ffsupver.securityCraftBlockExtend.SecurityCraftBlockExtend;
import com.ffsupver.securityCraftBlockExtend.screen.ChainedUniversalBlockReinforcerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SCBEScreens {
   public static DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, SecurityCraftBlockExtend.MODID);

   public static RegistryObject<MenuType<ChainedUniversalBlockReinforcerMenu>> CHAINED_UNIVERSAL_BLOCK_REINFORCER = MENU_TYPES.register(
     "chained_universal_block_reinforcer",
           ()-> IForgeMenuType.create((windowId, inv, data) -> new ChainedUniversalBlockReinforcerMenu(windowId, inv))
   );

   public static void register(IEventBus eventBus){
      MENU_TYPES.register(eventBus);
   }
}
