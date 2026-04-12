package org.dimdev.jeid.mixin.modsupport.kathairis;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import mod.krevik.world.dimension.ChunkGeneratorMystic;
import tff.reid.api.BiomeApi;
import tff.reid.api.compat.CompatibleChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChunkGeneratorMystic.class, remap = false)
public class MixinChunkGeneratorMystic implements CompatibleChunkGenerator {
    /* The modified biomes after querying the biome provider */
    @Shadow
    private Biome[] biomesForGeneration;

    /*
     * Explicit compatibility with REID's biome format.
     * The chunk generator modifies the biomes returned by the world's biome provider.
     */
    @Inject(method = "generateChunk", at = @At(value = "RETURN"), remap = true)
    private void reid$initBiomes(CallbackInfoReturnable<Chunk> cir) {
        BiomeApi.INSTANCE.replaceBiomes(cir.getReturnValue(), biomesForGeneration);
    }
}
