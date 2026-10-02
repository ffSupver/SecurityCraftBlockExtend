package com.ffsupver.securityCraftBlockExtend;

import com.ffsupver.securityCraftBlockExtend.compat.Mods;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockStateProvider;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBELootTableProvider;
import com.ffsupver.securityCraftBlockExtend.registeries.SCBEBlocks;
import com.ffsupver.securityCraftBlockExtend.registeries.SCBEItems;
import com.ffsupver.securityCraftBlockExtend.registeries.SCBETabs;
import com.mojang.logging.LogUtils;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SecurityCraftBlockExtend.MODID)
public class SecurityCraftBlockExtend
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "securitycraft_block_extend";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();


    public SecurityCraftBlockExtend(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();


        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(SecurityCraftBlockExtend::gatherData);

        SCBEItems.register(modEventBus);
        SCBEBlocks.register(modEventBus);

        Mods.init(modEventBus);

        SCBETabs.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        //common setup
//        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        System.out.println("gatherData");

        DataGenerator gen = event.getGenerator();
        ExistingFileHelper efh = event.getExistingFileHelper();

        gen.addProvider(event.includeClient(),
                new SCBEBlockStateProvider(gen.getPackOutput(), efh));
        gen.addProvider(event.includeServer(),
                new SCBELootTableProvider(gen.getPackOutput()));
    }
}
