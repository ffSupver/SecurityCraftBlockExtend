package com.ffsupver.securityCraftBlockExtend.compat.jei;

import com.ffsupver.securityCraftBlockExtend.SecurityCraftBlockExtend;
import com.ffsupver.securityCraftBlockExtend.screen.ChainedUniversalBlockReinforcerScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class JeiCompat implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return SecurityCraftBlockExtend.asResource("jei_plugin");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGhostIngredientHandler(ChainedUniversalBlockReinforcerScreen.class, new ChainedUniversalBlockReinforcerScreenGhostItemHandler());
    }
}
