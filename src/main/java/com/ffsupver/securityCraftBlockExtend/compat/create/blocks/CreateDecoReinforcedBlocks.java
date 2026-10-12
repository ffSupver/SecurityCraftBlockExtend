package com.ffsupver.securityCraftBlockExtend.compat.create.blocks;

import com.ffsupver.securityCraftBlockExtend.SecurityCraftBlockExtend;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockLootData;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockTagData;
import com.ffsupver.securityCraftBlockExtend.registeries.SCBEBlocks;
import com.github.talrey.createdeco.BlockRegistry;
import com.github.talrey.createdeco.ItemRegistry;
import com.github.talrey.createdeco.blocks.CatwalkBlock;
import com.github.talrey.createdeco.connected.CatwalkCTBehaviour;
import com.github.talrey.createdeco.items.CatwalkBlockItem;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.client.model.generators.ConfiguredModel;

import java.util.*;
import java.util.function.Supplier;

public class CreateDecoReinforcedBlocks {
    private static final CreateRegistrate REGISTRATE = CreateReinforcedBlocks.REGISTRATE;

    public static final Map<String, CTSpriteShiftEntry> REINFORCED_CATWALK_TOPS = new HashMap<>();
    public static final Map<String, BlockEntry<ReinforcedCatwalkBlock>> REINFORCED_CATWALKS = new HashMap<>();
    static {
        List<String> metals = new ArrayList<>(ItemRegistry.METAL_TYPES.keySet());
        for (String metal : metals) {
            String regName = metal.toLowerCase(Locale.ROOT).replace(" ", "_");
            ResourceLocation src = SecurityCraftBlockExtend.asResource(
                    "block/palettes/catwalks/reinforced_" + regName + "_catwalk");
            ResourceLocation target = SecurityCraftBlockExtend.asResource(
                    "block/palettes/catwalks/reinforced_" + regName + "_catwalk_connected");
            REINFORCED_CATWALK_TOPS.put(
                    metal,
                    CTSpriteShifter.getCT(AllCTTypes.OMNIDIRECTIONAL, src, target));
        }

        metals.forEach(CreateDecoReinforcedBlocks::registerReinforcedCatwalk);
    }
    public static void register(){}
    private static void registerReinforcedCatwalk(String metal) {
        String regName = "reinforced_" + metal.toLowerCase(Locale.ROOT).replace(" ", "_") + "_catwalk";
        ResourceLocation catwalkTexture = SecurityCraftBlockExtend.asResource(
                "block/palettes/catwalks/reinforced_" + metal.toLowerCase(Locale.ROOT).replace(" ", "_") + "_catwalk");
        ResourceLocation catwalkModel = SecurityCraftBlockExtend.asResource("block/" + regName);

        BlockEntry<CatwalkBlock> vanillaCatwalk = BlockRegistry.CATWALKS.get(metal);

        BlockEntry<ReinforcedCatwalkBlock> entry = REGISTRATE
                .block(regName, p -> new ReinforcedCatwalkBlock(
                        p, vanillaCatwalk.get()))
                .properties(props -> props
                        .strength(5.0F, 6.0F)
                        .requiresCorrectToolForDrops()
                        .noOcclusion()
                        .sound(SoundType.NETHERITE_BLOCK))
//                .addLayer(() -> RenderType::cutoutMipped)  // 需要在模型文件指定
                .setData(ProviderType.LOOT, NonNullBiConsumer.noop())
                .setData(ProviderType.LANG,NonNullBiConsumer.noop())
                .setData(ProviderType.BLOCK_TAGS, NonNullBiConsumer.noop())
                .blockstate((ctx, prov) -> {
                    ResourceLocation parent = ResourceLocation.tryBuild("createdeco", "block/catwalk_top");
                    prov.models()
                            .withExistingParent(ctx.getName(), parent)
                            .texture("2", catwalkTexture)
                            .texture("particle", catwalkTexture)
                            .renderType("cutout_mipped");
                    prov.getVariantBuilder(ctx.get()).forAllStates(state ->
                            ConfiguredModel.builder()
                                    .modelFile(prov.models().getExistingFile(catwalkModel))
                                    .build());
                })
                .item(CatwalkBlockItem::new).properties((p) -> metal.equals("Netherite") ? p.fireResistant() : p)
                .model((ctx, prov) -> prov.withExistingParent(
                        ctx.getName(), ResourceLocation.tryBuild(ResourceLocation.DEFAULT_NAMESPACE, "block/template_trapdoor_bottom"))
                        .texture("texture",catwalkTexture)
                )
                .build()
                .onRegister(CreateRegistrate.connectedTextures(
                        () -> new CatwalkCTBehaviour(REINFORCED_CATWALK_TOPS.get(metal))
                ))
                .register();

        SCBEBlocks.registerReinforcedBlockMapping(entry,entry);

        REINFORCED_CATWALKS.put(metal, entry);
    }

    public static List<SCBEBlockLootData> getBlockLootData() {
        List<SCBEBlockLootData> blockLootData = new ArrayList<>();
        CreateDecoReinforcedBlocks.REINFORCED_CATWALKS.values().forEach(
                entry -> blockLootData.add(SCBEBlockLootData.dropSelf(entry))
        );
        return blockLootData;
    }

    public static List<SCBEBlockTagData> getBlockTagData() {
        List<SCBEBlockTagData> blockTagData = new ArrayList<>();
        CreateDecoReinforcedBlocks.REINFORCED_CATWALKS.values().forEach(
                entry -> blockTagData.add(
                        new SCBEBlockTagData.Builder(entry)
                                .pickaxe()
                                .add(AllTags.AllBlockTags.FAN_TRANSPARENT.tag)
                                .reinforced()
                                .build()
                )
        );
        return blockTagData;
    }

    public static List<Supplier<ItemLike>> getCreativeTabItems(){
        return REINFORCED_CATWALKS.values().stream()
                .map(e -> (Supplier<ItemLike>) e::get)
                .toList();
    }
}
