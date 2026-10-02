package com.ffsupver.securityCraftBlockExtend.compat;


import com.ffsupver.securityCraftBlockExtend.SecurityCraftBlockExtend;
import com.ffsupver.securityCraftBlockExtend.compat.create.CreateCompat;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockLootData;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockModelData;
import com.ffsupver.securityCraftBlockExtend.dataGen.SCBEBlockTagData;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Mods {
    private static final Map<String, Supplier<SCBEModCompat>> MOD_SUPPLIERS = new HashMap<>();
    private static final Map<String, SCBEModCompat> MODS = new HashMap<>();
    private static boolean hasInit = false;

    private static void loadMods(){
        if (!hasInit){
            MOD_SUPPLIERS.clear();

            addMod(ModIds.CREATE, CreateCompat.class);

            MOD_SUPPLIERS.forEach(Mods::intiMod);
            SecurityCraftBlockExtend.LOGGER.info("[Mod Compat]Loaded {} mods!", MODS.size());

            hasInit = true;
        }

    }

    public static void init(IEventBus eventBus){
        loadMods();

        executeIfLoad(scbeModCompat -> scbeModCompat.init(eventBus));
    }

    private static void addMod(ModIds modId,Class<? extends SCBEModCompat> modCompatClass){
        SecurityCraftBlockExtend.LOGGER.info("[Mod Compat]{}:is {}!",modId.ModId,isModLoad(modId.ModId)?"loaded":"not loaded");
        if (isModLoad(modId.ModId)){

                Supplier<SCBEModCompat> modCompatSupplier = ()-> {
                    try {
                        return modCompatClass.getDeclaredConstructor().newInstance();
                    }catch (Exception e) {
                        SecurityCraftBlockExtend.LOGGER.error("[Mod Compat Fail]{}:{}", modId.ModId, e.getMessage());
                        throw new RuntimeException(e);
                    }
                };
                MOD_SUPPLIERS.put(modId.ModId, modCompatSupplier);

        }
    }

    /**
     * Not check if mod is loaded
     */
    private static void intiMod(String modId,Supplier<SCBEModCompat> modCompatSupplier){
        MODS.put(modId,modCompatSupplier.get());
    }

    public static boolean isModLoad(String modId){
        return ModList.get().isLoaded(modId);
    }

    public static List<Supplier<ItemLike>> collectCreativeTabItems(){
        List<Supplier<ItemLike>> result = new ArrayList<>();
        executeIfLoad(compat -> result.addAll(compat.getCreativeTabItems()));
        return result;
    }

    /**
     * 收集所有已加载兼容模块的方块模型数据。
     * 数据生成时调用，未加载的模块不会出现在结果中。
     */
    public static List<SCBEBlockModelData> collectBlockModelData() {
        List<SCBEBlockModelData> result = new ArrayList<>();
        executeIfLoad(compat -> result.addAll(compat.getBlockModelData()));
        return result;
    }
    public static List<SCBEBlockLootData> collectBlockLootData() {
        List<SCBEBlockLootData> result = new ArrayList<>();
        executeIfLoad(compat -> result.addAll(compat.getBlockLootData()));
        return result;
    }
    public static List<SCBEBlockTagData> collectBlockTagData() {
        List<SCBEBlockTagData> result = new ArrayList<>();
        executeIfLoad(compat -> result.addAll(compat.getBlockTagData()));
        return result;
    }

    private static void executeIfLoad(Consumer<SCBEModCompat> runnable){
        for (Map.Entry<String, SCBEModCompat> entry : MODS.entrySet()){
            if (ModList.get().isLoaded(entry.getKey())){
               runnable.accept(entry.getValue());
            }
        }
    }

    public static void executeIfModLoad(String modTd,Consumer<SCBEModCompat> runnable){
        if (MODS.containsKey(modTd)){
            runnable.accept(MODS.get(modTd));
        }
    }


    public enum ModIds{
        CREATE("create");
        public final String ModId;

        ModIds(String modId) {
            ModId = modId;
        }
    }
}
