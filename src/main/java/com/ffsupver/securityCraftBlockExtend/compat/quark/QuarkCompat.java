package com.ffsupver.securityCraftBlockExtend.compat.quark;

import com.ffsupver.securityCraftBlockExtend.compat.Mods;
import com.ffsupver.securityCraftBlockExtend.compat.SCBEModCompat;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockLootData;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockModelData;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockTagData;
import com.ffsupver.securityCraftBlockExtend.registeries.SCBEBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import org.violetmoon.quark.content.building.block.QuarkVerticalSlabBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class QuarkCompat implements SCBEModCompat {
    protected static final DeferredRegister<Block> BLOCKS = SCBEBlocks.BLOCKS;
    public static final Map<Block,RegistryObject<? extends Block>> QUARK_REINFORCED_BLOCKS = new HashMap<>();
    private boolean populated = false;

    private final List<Supplier<ItemLike>> creativeTabItems = new ArrayList<>();
    private final List<SCBEBlockModelData> modelDataList = new ArrayList<>();
    private final List<SCBEBlockLootData> lootTableProviders = new ArrayList<>();
    private final List<SCBEBlockTagData> blockTagDataList = new ArrayList<>();

    @Override
    public String getModId() {
        return Mods.ModIds.QUARK.ModId;
    }

    @Override
    public void init(IEventBus eventBus) {
        eventBus.addListener(EventPriority.HIGH,this::onRegister);
//        MinecraftForge.EVENT_BUS.addListener(this::onTagsUpdated);
    }

    private void onRegister(RegisterEvent event) {
        if (populated) return;
        if (!event.getRegistryKey().equals(ForgeRegistries.Keys.BLOCKS)) return;
        populated = true;

        System.out.println("OnRegister QuarkCompat:\n"+ QuarkBlocksCapture.QUARK_BLOCKS.size());
        for (Map.Entry<ResourceLocation, Block> bE : QuarkBlocksCapture.QUARK_BLOCKS.entrySet()){
            Block block = bE.getValue();
            ResourceLocation id = bE.getKey();
            if (block instanceof QuarkVerticalSlabBlock quarkVerticalSlabBlock){
                Block parent = quarkVerticalSlabBlock.parent.get();
                ResourceLocation parentId = ForgeRegistries.BLOCKS.getKey(parent);
                if (parentId == null) continue;

                System.out.println("OnRegister QuarkCompat VerticalSlabBlock: "+id+" parent: "+parentId+ " parentBlock: "+parent+" c:"+parent.getClass().getName());
                String name = "reinforced_" + id.getPath();

                RegistryObject<ReinforcedVerticalSlab> reg = SCBEBlocks.registerBlock(
                        name,
                        p->new ReinforcedVerticalSlab(p, block),
                        ()->SCBEBlocks.reinforcedCopy(block, UnaryOperator.identity()),
                        BLOCKS
                );
                SCBEBlockTagData.Builder builder = new SCBEBlockTagData.Builder(reg);

                System.out.println(name+"contains"+QuarkBlockTags.BLOCK_TAGS.containsKey(id.getPath())+" pPath:"+id.getPath());
                if (QuarkBlockTags.BLOCK_TAGS.containsKey(id.getPath())) {
                    QuarkBlockTags.BLOCK_TAGS.get(id.getPath()).forEach(builder::add);
                }


                modelDataList.add(new SCBEBlockModelData(
                        name,
                        reg,
                        ResourceLocation.tryBuild("quark", "block/vertical_slab"),
                        Map.of("parentId", parentId),
                        null,
                        SCBEBlockModelData.BlockType.VERTICAL_SLAB,
                        null
                ));
                creativeTabItems.add(reg::get);
                lootTableProviders.add(new SCBEBlockLootData(reg,SCBEBlockLootData.BlockLootType.VERTICAL_SLAB));
                SCBEBlocks.registerReinforcedBlockMapping(reg,reg);
                blockTagDataList.add(builder.reinforced().build());
                QUARK_REINFORCED_BLOCKS.put(block, reg);
            }
        }

    }

    @Override
    public List<SCBEBlockModelData> getBlockModelData() {
        return modelDataList;
    }

    @Override
    public List<Supplier<ItemLike>> getCreativeTabItems() {
        return creativeTabItems;
    }

    @Override
    public List<SCBEBlockLootData> getBlockLootData() {
        return lootTableProviders;
    }

    @Override
    public List<SCBEBlockTagData> getBlockTagData() {
        return blockTagDataList;
    }
}
