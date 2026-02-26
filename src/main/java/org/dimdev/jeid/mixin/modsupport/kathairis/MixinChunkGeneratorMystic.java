package org.dimdev.jeid.mixin.modsupport.kathairis;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import mod.krevik.world.dimension.ChunkGeneratorMystic;
import org.tff.reid.api.BiomeApi;
import org.tff.reid.api.compat.CompatibleChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChunkGeneratorMystic.class, remap = false)
public class MixinChunkGeneratorMystic implements CompatibleChunkGenerator {
    @Shadow
    private Biome[] biomesForGeneration;

    @Inject(method = "generateChunk", at = @At(value = "RETURN"), remap = true)
    private void reid$initBiomes(CallbackInfoReturnable<Chunk> cir) {
        BiomeApi.INSTANCE.replaceBiomes(cir.getReturnValue(), biomesForGeneration);
    }
}
