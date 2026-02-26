package org.dimdev.jeid.mixin.modsupport.cubicchunks;

import net.minecraft.network.PacketBuffer;
import net.minecraft.world.chunk.Chunk;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.opencubicchunks.cubicchunks.core.world.cube.Cube;
import io.netty.buffer.ByteBuf;
import org.tff.reid.api.BiomeApi;
import org.tff.reid.api.biome.BiomeAccessor;
import org.dimdev.jeid.impl.type.BiomeStorage;
import org.dimdev.jeid.impl.type.BiomeContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(targets = "io.github.opencubicchunks.cubicchunks.core.network.WorldEncoder", remap = false)
public class MixinWorldEncoder {
    @Inject(method = "lambda$encodeCubes$5", at = @At(value = "HEAD"), cancellable = true)
    private static void reid$encodeCubeBiomes(PacketBuffer out, Cube cube, CallbackInfo ci) {
        BiomeContainer cubeBiomes = ((BiomeStorage) cube).reid$getBiomes();
        if (cubeBiomes.isInitialized()) {
            reid$encodeBiomes(cubeBiomes, out);
        }
        ci.cancel();
    }

    @Redirect(method = "encodeColumn", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketBuffer;writeBytes([B)Lio/netty/buffer/ByteBuf;", remap = true))
    private static ByteBuf reid$writeColumnBiomes(PacketBuffer instance, byte[] oldBiomeArray, PacketBuffer out, Chunk column) {
        reid$encodeBiomes((BiomeContainer) BiomeApi.INSTANCE.getBiomeAccessor(column), out);
        return out;
    }

    @Redirect(method = "decodeColumn", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketBuffer;readBytes([B)Lio/netty/buffer/ByteBuf;", remap = true))
    private static ByteBuf reid$readColumnBiomes(PacketBuffer instance, byte[] oldBiomeArray, PacketBuffer in, Chunk column) {
        reid$decodeBiomes((BiomeContainer) BiomeApi.INSTANCE.getBiomeAccessor(column), in);
        return in;
    }

    @Definition(id = "hasCustomBiomeMap", local = @Local(type = boolean[].class, name = "hasCustomBiomeMap"))
    @Expression("hasCustomBiomeMap[?]")
    @ModifyExpressionValue(method = "decodeCube", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean reid$decodeCubeBiomes(boolean original, PacketBuffer in, List<Cube> cubes, @Local(name = "i") int i) {
        if (original) {
            Cube cube = cubes.get(i);
            BiomeContainer cubeBiomes = ((BiomeStorage) cube).reid$getBiomes();
            reid$decodeBiomes(cubeBiomes, in);
        }
        // Skip original decoding
        return false;
    }

    /**
     * @author Exsolutus, jchung01
     * @reason Encode int array size (in bytes)
     */
    @Overwrite
    static int getEncodedSize(Chunk column) {
        int biomeDataSize = reid$getBiomeBufferSize(BiomeApi.INSTANCE.getBiomeAccessor(column));
        int heightmapDataSize = (Cube.SIZE * Cube.SIZE * Integer.BYTES);
        return biomeDataSize + heightmapDataSize;
    }

    /**
     * Encode int array sizes for each cube
     */
    @Definition(id = "biomeArray", local = @Local(type = byte[].class, name = "biomeArray"))
    @Expression("biomeArray.length")
    @ModifyExpressionValue(method = "getEncodedSize(Ljava/util/Collection;)I", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static int reid$encodeCubeBiomesSize(int original, @Local(name = "cube") Cube cube) {
        BiomeContainer cubeBiomes = ((BiomeStorage) cube).reid$getBiomes();
        return reid$getBiomeBufferSize(cubeBiomes);
    }

    /**
     * Encodes biomes from a general {@link BiomeContainer} to a packet.
     */
    @Unique
    private static void reid$encodeBiomes(BiomeContainer container, PacketBuffer out) {
        int[] biomes = container.getInternalBiomes();
        for (int biome : biomes) {
            out.writeInt(biome);
        }
    }

    /**
     * Decodes biomes from a packet to a general {@link BiomeContainer}.
     */
    @Unique
    private static void reid$decodeBiomes(BiomeContainer container, PacketBuffer in) {
        int[] biomes = new int[container.size()];
        for (int i = 0; i < container.size(); i++) {
            biomes[i] = in.readInt();
        }
        container.setBiomes(biomes);
    }

    /**
     * Gets number of bytes to encode biomes from a general {@link BiomeAccessor}.
     */
    @Unique
    private static int reid$getBiomeBufferSize(BiomeAccessor accessor) {
        return accessor.size() * Integer.BYTES;
    }
}
