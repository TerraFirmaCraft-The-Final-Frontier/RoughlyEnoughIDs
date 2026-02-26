package org.dimdev.jeid.mixin.modsupport.cyclopscore;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import com.llamalad7.mixinextras.sugar.Local;
import org.cyclops.cyclopscore.helper.WorldHelpers;
import org.tff.reid.api.BiomeApi;
import org.dimdev.jeid.network.MessageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorldHelpers.class, remap = false)
public class MixinWorldHelpers {
    @Inject(method = "setBiome", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;markDirty()V", remap = true))
    private static void reid$toIntBiomeArray(World world, BlockPos pos, Biome biome, CallbackInfo ci, @Local Chunk chunk) {
        BiomeApi.INSTANCE.updateBiome(chunk, pos, Biome.getIdForBiome(biome));
    }

    /**
     * @reason Sync clients and don't call unnecessary methods - {@link IChunkProvider#provideChunk} and {@link World#markBlockRangeForRenderUpdate}
     */
    @Inject(method = "setBiome", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getChunkProvider()Lnet/minecraft/world/chunk/IChunkProvider;", remap = true), cancellable = true)
    private static void reid$sendBiomeMessage(World world, BlockPos pos, Biome biome, CallbackInfo ci) {
        if (!world.isRemote) {
            MessageManager.sendClientsBiomePosChange(world, pos, Biome.getIdForBiome(biome));
        }
        ci.cancel();
    }
}
