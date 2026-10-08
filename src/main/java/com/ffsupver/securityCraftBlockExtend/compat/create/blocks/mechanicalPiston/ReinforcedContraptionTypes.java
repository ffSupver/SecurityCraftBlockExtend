package com.ffsupver.securityCraftBlockExtend.compat.create.blocks.mechanicalPiston;

import com.ffsupver.securityCraftBlockExtend.SecurityCraftBlockExtend;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;

public class ReinforcedContraptionTypes {
    public static final Holder.Reference<ContraptionType> REINFORCED_PISTON =
            Registry.registerForHolder(
                    CreateBuiltInRegistries.CONTRAPTION_TYPE,
                    SecurityCraftBlockExtend.asResource("reinforced_piston"),
                    new ContraptionType(RPistonContraption::new));

    /** 仅用于触发静态初始化，实际注册在 <clinit> 中完成。 */
    public static void init() {
        SecurityCraftBlockExtend.LOGGER.info(
                "[SCBE] Registered contraption type: {}", REINFORCED_PISTON.key().location());
    }
}