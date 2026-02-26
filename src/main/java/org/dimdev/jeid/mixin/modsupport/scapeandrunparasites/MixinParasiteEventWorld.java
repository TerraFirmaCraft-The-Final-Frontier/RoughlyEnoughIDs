package org.dimdev.jeid.mixin.modsupport.scapeandrunparasites;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.tff.reid.api.BiomeApi;
import org.dimdev.jeid.network.MessageManager;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Legacy mixin for biome change in SRP 1.9.x.
 * Provided here until 1.10.x is stable and addons update.
 */
@Mixin(value = ParasiteEventWorld.class, remap = false)
public class MixinParasiteEventWorld {
    @Dynamic("1.9.x SRP - Update parasite biome")
    @ModifyExpressionValue(method = "positionToParasiteBiome", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getIdForBiome(Lnet/minecraft/world/biome/Biome;)I", remap = true), require = 0)
    private static int reid$updateToParasiteBiome(int original, World world, BlockPos pos) {
        BiomeApi.INSTANCE.updateBiome(world.getChunk(pos), pos, original);
        MessageManager.sendClientsBiomePosChange(world, pos, original);
        return original;
    }

    @Dynamic("1.9.x SRP - Update purified biome")
    @ModifyExpressionValue(method = "positionToBiome", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getIdForBiome(Lnet/minecraft/world/biome/Biome;)I", remap = true), require = 0)
    private static int reid$updateToPurifiedBiome(int original, World world, BlockPos pos) {
        BiomeApi.INSTANCE.updateBiome(world.getChunk(pos), pos, original);
        MessageManager.sendClientsBiomePosChange(world, pos, original);
        return original;
    }
}
