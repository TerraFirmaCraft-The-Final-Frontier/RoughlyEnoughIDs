package org.dimdev.jeid.mixin.modsupport.wyrmsofnyrus;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.vetpetmon.wyrmsofnyrus.common.world.biome.SpreadingBiome;
import org.tff.reid.api.BiomeApi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SpreadingBiome.class, remap = false)
public class MixinSpreadingBiome {
    @ModifyExpressionValue(method = "setBiome(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getIdForBiome(Lnet/minecraft/world/biome/Biome;)I"))
    private static int reid$updateCreepedBiome(int biomeId, World world, BlockPos pos) {
        BiomeApi.INSTANCE.updateBiome(world.getChunk(pos), pos, biomeId);
        return biomeId;
    }

    @ModifyExpressionValue(method = "setBiome(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/biome/Biome;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getIdForBiome(Lnet/minecraft/world/biome/Biome;)I"))
    private static int reid$updateBiome(int biomeId, World world, BlockPos pos) {
        BiomeApi.INSTANCE.updateBiome(world.getChunk(pos), pos, biomeId);
        return biomeId;
    }
}
