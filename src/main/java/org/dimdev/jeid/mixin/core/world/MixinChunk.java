package org.dimdev.jeid.mixin.core.world;

import net.minecraft.world.chunk.Chunk;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.dimdev.jeid.INewChunk;
import org.tff.reid.api.BiomeApi;
import org.tff.reid.api.biome.BiomeAccessor;
import org.dimdev.jeid.impl.type.BiomeContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chunk.class)
public abstract class MixinChunk implements INewChunk {
    @Unique
    private BiomeContainer reid$biomeContainer = new BiomeContainer((Chunk) (Object) this, 16 * 16);

    @Unique
    @Override
    public BiomeContainer reid$getBiomes() {
        return reid$biomeContainer;
    }

    /**
     * @deprecated Use the {@link BiomeApi} to get a {@link BiomeAccessor} instead.
     */
    @Deprecated
    @Override
    public int[] getIntBiomeArray() {
        return reid$biomeContainer.getInternalBiomes();
    }

    /**
     * @deprecated Use {@link BiomeApi}'s update methods instead.
     */
    @Deprecated
    @Override
    public void setIntBiomeArray(int[] intBiomeArray) {
        reid$biomeContainer.setBiomes(intBiomeArray);
    }

    /**
     * @return A biome array filled with REID's error biome to better identify mods that aren't supported yet.
     */
    @ModifyReturnValue(method = "getBiomeArray", at = @At(value = "RETURN"))
    private byte[] reid$returnErrorBiomeArray(byte[] original) {
        return reid$biomeContainer.dummy();
    }

    /**
     * Get biome from REID biome array.
     */
    @Definition(id = "blockBiomeArray", field = "Lnet/minecraft/world/chunk/Chunk;blockBiomeArray:[B")
    @Expression("this.blockBiomeArray[?] & 255")
    @ModifyExpressionValue(method = "getBiome", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int reid$fromIntBiomeArray(int original, @Local(name = "i") int i, @Local(name = "j") int j ) {
        return reid$biomeContainer.getBiomeId(i, j);
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
    @Definition(id = "blockBiomeArray", field = "Lnet/minecraft/world/chunk/Chunk;blockBiomeArray:[B")
    @Expression("this.blockBiomeArray[?] = (byte) (? & 255)")
    @WrapOperation(method = "getBiome", at = @At("MIXINEXTRAS:EXPRESSION"))
    private void reid$updateBiomeArray(byte[] array, int index, byte value, Operation<Void> original, @Local(name = "k") int biomeId) {
        reid$biomeContainer.setBiome(index, biomeId);
    }

    /**
     * Skip setting original biome array.
     */
    @Inject(method = "setBiomeArray", at = @At("HEAD"), cancellable = true)
    private void reid$cancelSetBiomeArray(byte[] biomeArray, CallbackInfo ci) {
        ci.cancel();
    }
}
