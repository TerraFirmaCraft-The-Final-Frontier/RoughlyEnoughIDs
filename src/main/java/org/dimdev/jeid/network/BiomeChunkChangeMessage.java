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

public class BiomeChunkChangeMessage implements IMessage {
    private int chunkX;
    private int chunkZ;
    private int[] biomeArray;

    public BiomeChunkChangeMessage() {}

    public BiomeChunkChangeMessage(int chunkX, int chunkZ, int[] biomeArray) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.biomeArray = biomeArray;
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        PacketBuffer packetBuffer = new PacketBuffer(byteBuf);
        chunkX = packetBuffer.readVarInt();
        chunkZ = packetBuffer.readVarInt();
        biomeArray = packetBuffer.readVarIntArray();
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        PacketBuffer packetBuffer = new PacketBuffer(byteBuf);
        packetBuffer.writeVarInt(chunkX);
        packetBuffer.writeVarInt(chunkZ);
        packetBuffer.writeVarIntArray(biomeArray);
    }

    public static class Handler implements IMessageHandler<BiomeChunkChangeMessage, IMessage> {
        @Override
        public IMessage onMessage(BiomeChunkChangeMessage message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                WorldClient world = Minecraft.getMinecraft().world;
                Chunk chunk = world.getChunk(message.chunkX, message.chunkZ);
                BiomeApi.INSTANCE.replaceBiomes(chunk, message.biomeArray);
                world.markBlockRangeForRenderUpdate(new BlockPos(chunk.getPos().getXStart(), 0, chunk.getPos().getZStart()), new BlockPos(chunk.getPos().getXEnd(), world.getHeight(), chunk.getPos().getZEnd()));
            });
            return null;
        }
    }
}
