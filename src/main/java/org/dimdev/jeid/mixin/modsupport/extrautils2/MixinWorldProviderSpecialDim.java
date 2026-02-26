package org.dimdev.jeid.mixin.modsupport.extrautils2;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import com.llamalad7.mixinextras.sugar.Local;
import com.rwtema.extrautils2.dimensions.workhousedim.WorldProviderSpecialDim;
import org.tff.reid.api.BiomeApi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorldProviderSpecialDim.class, remap = false)
public class MixinWorldProviderSpecialDim {
    @Shadow
    public static Biome biome;

    @Inject(method = "generate", at = @At(target = "Lnet/minecraft/world/chunk/Chunk;setTerrainPopulated(Z)V", value = "INVOKE", ordinal = 0, remap = true))
    private static void reid$setBiomeArray(CallbackInfo ci, @Local Chunk chunk) {
        BiomeApi.INSTANCE.fillWithBiome(chunk, Biome.getIdForBiome(biome));
    }
}
