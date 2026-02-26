package org.dimdev.jeid.mixin.modsupport.journeymap;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import journeymap.client.model.ChunkMD;
import org.tff.reid.api.BiomeApi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = ChunkMD.class, remap = false)
public abstract class MixinChunkMD {
    @Shadow
    public abstract Chunk getChunk();

    /**
     * Get biome from REID biome array.
     */
    @Definition(id = "blockBiomeArray", local = @Local(type = byte[].class))
    @Expression("blockBiomeArray[?] & 255")
    @ModifyExpressionValue(method = "getBiome", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int reid$fromIntBiomeArray(int original, BlockPos pos) {
        return BiomeApi.INSTANCE.getBiomeAccessor(getChunk()).getBiomeId(pos);
    }

    /**
     * Compatibility for mods that don't initialize the chunk's biome array on generation (e.g. Chunk-Pregenerator)
     *
     * @reason Use intBiomeArray's default value.
     */
    @ModifyConstant(method = "getBiome", constant = @Constant(intValue = 255, ordinal = 1))
    private int reid$modifyDefaultId(int original) {
        return -1;
    }

    /**
     * Update REID biome array instead of original.
     */
    @Definition(id = "blockBiomeArray", local = @Local(type = byte[].class))
    @Expression("blockBiomeArray[?] = (byte) (? & 255)")
    @WrapOperation(method = "getBiome", at = @At("MIXINEXTRAS:EXPRESSION"))
    private void reid$updateBiomeArray(byte[] array, int index, byte value, Operation<Void> original,
                                       BlockPos pos, @Local(name = "k") int biomeId) {
        BiomeApi.INSTANCE.updateBiome(getChunk(), pos, biomeId);
    }
}
