package org.dimdev.jeid.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.tff.reid.api.BiomeApi;

public class BiomePositionChangeMessage implements IMessage {
    private int x;
    private int z;
    private int biomeId;

    public BiomePositionChangeMessage() {}

    public BiomePositionChangeMessage(int x, int z, int biomeId) {
        this.x = x;
        this.z = z;
        this.biomeId = biomeId;
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        PacketBuffer packetBuffer = new PacketBuffer(byteBuf);
        x = packetBuffer.readVarInt();
        z = packetBuffer.readVarInt();
        biomeId = packetBuffer.readVarInt();
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        PacketBuffer packetBuffer = new PacketBuffer(byteBuf);
        packetBuffer.writeVarInt(x);
        packetBuffer.writeVarInt(z);
        packetBuffer.writeVarInt(biomeId);
    }

    public static class Handler implements IMessageHandler<BiomePositionChangeMessage, IMessage> {
        @Override
        public IMessage onMessage(BiomePositionChangeMessage message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                WorldClient world = Minecraft.getMinecraft().world;
                BlockPos pos = new BlockPos(message.x, 0, message.z);
                Chunk chunk = world.getChunk(pos);
                BiomeApi.INSTANCE.updateBiome(chunk, pos, message.biomeId);
                world.markBlockRangeForRenderUpdate(pos, new BlockPos(message.x, world.getHeight(), message.z));
            });
            return null;
        }
    }
}
