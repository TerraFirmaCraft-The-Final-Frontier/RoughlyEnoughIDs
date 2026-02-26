package org.dimdev.jeid.mixin.modsupport.chunkpregenerator;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import com.llamalad7.mixinextras.sugar.Local;
import org.tff.reid.api.BiomeApi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import pregenerator.impl.tracking.types.BaseWorldEntry;
import pregenerator.impl.tracking.types.BiomeEntry;

@Mixin(value = BiomeEntry.class, remap = false)
public class MixinBiomeEntry {
    @Unique
    private static final byte[] SKIP_BIOME_ARRAY = new byte[0];

    @Redirect(method = "getChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;getBiomeArray()[B", remap = true))
    private static byte[] reid$countIntBiomes(Chunk instance, @Local BaseWorldEntry.Counter<Biome> counter) {
        final int[] biomeArray = BiomeApi.INSTANCE.getBiomeAccessor(instance).getBiomes();
        for (int id : biomeArray) {
            Biome biome = Biome.getBiome(id);
            if (biome != null) counter.add(biome);
        }
        // empty byte[], skips original biome counting
        return SKIP_BIOME_ARRAY;
    }
}
