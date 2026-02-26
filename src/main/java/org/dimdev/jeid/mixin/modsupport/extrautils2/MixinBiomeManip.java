package org.dimdev.jeid.mixin.modsupport.extrautils2;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import com.rwtema.extrautils2.biome.BiomeManip;
import org.tff.reid.api.BiomeApi;
import org.dimdev.jeid.network.MessageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BiomeManip.class, remap = false)
public class MixinBiomeManip {
    /**
     * @author ZombieHDGaming
     * @reason Support int biome ids and rewrite because it's unnecessarily complicated
     */
    @Inject(method = "setBiome", at = @At(value = "HEAD"), cancellable = true)
    private static void reid$rewriteSetBiome(World world, Biome biome, BlockPos pos, CallbackInfo ci) {
        Chunk chunk = world.getChunk(pos);
        BiomeApi.INSTANCE.updateBiome(chunk, pos, Biome.getIdForBiome(biome));
        if (!world.isRemote) {
            MessageManager.sendClientsBiomePosChange(world, pos, Biome.getIdForBiome(biome));
        }
        ci.cancel();
    }
}
