package org.dimdev.jeid.mixin.modsupport.thebetweenlands;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import com.llamalad7.mixinextras.sugar.Local;
import org.tff.reid.api.BiomeApi;
import org.dimdev.jeid.network.MessageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import thebetweenlands.common.block.terrain.BlockSpreadingDeath;

@Mixin(value = BlockSpreadingDeath.class, remap = false)
public class MixinBlockSpreadingDeath {
    @Redirect(method = "convertBiome", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;setBiomeArray([B)V", remap = true))
    private void reid$toIntBiomeArray(Chunk instance, byte[] biomeArray, World world, BlockPos pos, Biome biome, @Local Chunk chunk) {
        BiomeApi.INSTANCE.updateBiome(chunk, pos, Biome.getIdForBiome(biome));
        if (!world.isRemote) {
            MessageManager.sendClientsBiomePosChange(world, pos, Biome.getIdForBiome(biome));
        }
    }
}
