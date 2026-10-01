package com.ffsupver.securityCraftBlockExtend.dataGen;

import com.ffsupver.securityCraftBlockExtend.SecurityCraftBlockExtend;
import com.ffsupver.securityCraftBlockExtend.registeries.SCBEBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

public class SCBEBlockStateProvider extends BlockStateProvider {

    public SCBEBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, SecurityCraftBlockExtend.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // 示例：使用 reinforced_cube_bottom_top 父模型
        reinforcedCubeBottomTop(
                "reinforced_reinforced_deepslate",
                SCBEBlocks.REINFORCED_REINFORCED_DEEPSLATE,
                mcLoc("block/reinforced_deepslate_bottom"),
                mcLoc("block/reinforced_deepslate_top"),
                mcLoc("block/reinforced_deepslate_side")
        );

         reinforcedCubeAll(
                 "reinforced_wet_sponge",
                 SCBEBlocks.REINFORCED_WET_SPONGE,
                 mcLoc("block/wet_sponge")
         );
    }

    // ========== 通用方法 ==========

    /**
     * 为方块生成 blockstate、block model 和 item model。
     *
     * @param name        方块注册名（也用作模型文件名）
     * @param block       方块的 RegistryObject
     * @param parentModel 父模型路径，例如 "block/reinforced_cube_all"（securitycraft 命名空间）
     * @param textures    纹理映射，key 为父模型中的占位符（如 "all"、"bottom"、"top"、"side"）
     */
    private void reinforcedBlock(String name,
                                 RegistryObject<? extends Block> block,
                                 String parentModel,
                                 Map<String, ResourceLocation> textures) {

        // 1. 方块模型：使用 UncheckedModelFile 跳过父模型存在性检查
        BlockModelBuilder builder = models().getBuilder(name)
                .parent(new UncheckedModelFile(ResourceLocation.tryBuild("securitycraft", parentModel)));

        textures.forEach(builder::texture);
        ModelFile blockModel = builder;

        // 2. 方块状态
        simpleBlock(block.get(), blockModel);

        // 3. 物品模型：继承方块模型
        itemModels().withExistingParent(name, modLoc("block/" + name));
    }

    /**
     * 便捷方法：父模型为 securitycraft:block/reinforced_cube_all，纹理占位符为 "all"
     */
    private void reinforcedCubeAll(String name,
                                   RegistryObject<? extends Block> block,
                                   ResourceLocation allTexture) {
        reinforcedBlock(name, block, "block/reinforced_cube_all", Map.of("all", allTexture));
    }

    /**
     * 便捷方法：父模型为 securitycraft:block/reinforced_cube_bottom_top，纹理占位符为 bottom/top/side
     */
    private void reinforcedCubeBottomTop(String name,
                                         RegistryObject<? extends Block> block,
                                         ResourceLocation bottom,
                                         ResourceLocation top,
                                         ResourceLocation side) {
        reinforcedBlock(name, block, "block/reinforced_cube_bottom_top", Map.of(
                "bottom", bottom,
                "top", top,
                "side", side
        ));
    }

    /**
     * 便捷方法：父模型为 securitycraft:block/reinforced_cube_column，纹理占位符为 end/side
     */
    private void reinforcedCubeColumn(String name,
                                      RegistryObject<? extends Block> block,
                                      ResourceLocation end,
                                      ResourceLocation side) {
        reinforcedBlock(name, block, "block/reinforced_cube_column", Map.of(
                "end", end,
                "side", side
        ));
    }

    // ========== 工具方法 ==========

    /** 快速创建 minecraft 命名空间的 ResourceLocation */
    public ResourceLocation mcLoc(String path) {
        return ResourceLocation.tryBuild("minecraft", path);
    }
}