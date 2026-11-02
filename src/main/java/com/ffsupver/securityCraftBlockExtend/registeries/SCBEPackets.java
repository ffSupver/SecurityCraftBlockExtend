package com.ffsupver.securityCraftBlockExtend.registeries;

import com.ffsupver.securityCraftBlockExtend.SecurityCraftBlockExtend;
import com.ffsupver.securityCraftBlockExtend.item.ChainedUniversalBlockReinforcer.ChainedUniversalReinforcePacket;
import com.ffsupver.securityCraftBlockExtend.item.ChainedUniversalBlockReinforcer.ChainedUniversalReinforcePacketHandler;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class SCBEPackets {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            SecurityCraftBlockExtend.asResource("chained_universal_reinforce"),
            ()->PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register(){
        int id = 0;

        INSTANCE.messageBuilder(ChainedUniversalReinforcePacket.class,id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ChainedUniversalReinforcePacket::encode)
                .decoder(ChainedUniversalReinforcePacket::new)
                .consumerMainThread(ChainedUniversalReinforcePacketHandler::handle)
                .add();
    }
}
