package org.dimdev.jeid.mixin.modsupport.geographicraft;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import climateControl.DimensionManager;
import org.tff.reid.api.BiomeApi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DimensionManager.class, remap = false)
public class MixinDimensionManager {
    /**
     * @author Runemoro
     * @reason Support int biome ids and rewrite because of var types
     */
    @Inject(method = "hasOnlySea", at = @At(value = "HEAD"), cancellable = true)
    private void reid$rewriteHasOnlySea(Chunk tested, CallbackInfoReturnable<Boolean> cir) {
        final int[] biomes = BiomeApi.INSTANCE.getBiomeAccessor(tested).getBiomes();
        for (int biome : biomes) {
            if (biome != 0 && biome != Biome.getIdForBiome(Biomes.DEEP_OCEAN)) {
                cir.setReturnValue(false);
                return;
            }
        }
        cir.setReturnValue(true);
    }
}
