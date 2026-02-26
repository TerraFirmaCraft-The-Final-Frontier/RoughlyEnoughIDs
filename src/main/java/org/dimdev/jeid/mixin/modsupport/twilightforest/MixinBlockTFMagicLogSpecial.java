package org.dimdev.jeid.mixin.modsupport.twilightforest;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import com.llamalad7.mixinextras.sugar.Local;
import org.tff.reid.api.BiomeApi;
import org.dimdev.jeid.network.MessageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twilightforest.biomes.TFBiomes;
import twilightforest.block.BlockTFMagicLogSpecial;

@Mixin(value = BlockTFMagicLogSpecial.class, remap = false)
public class MixinBlockTFMagicLogSpecial {
    /**
     * @reason For versions 3.9 and later
     */
    @Inject(method = "sendChangedBiome", at = @At(value = "HEAD"), cancellable = true)
    private void reid$rewriteSendChangedBiome(World world, BlockPos pos, Biome biome, CallbackInfo ci) {
        MessageManager.sendClientsBiomePosChange(world, pos, Biome.getIdForBiome(biome));
        ci.cancel();
    }

    @Inject(method = "doTreeOfTransformationEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;getBiomeArray()[B", remap = true))
    private void reid$toIntBiomeArray(CallbackInfo ci, @Local(ordinal = 1) BlockPos dPos, @Local Chunk chunk) {
        BiomeApi.INSTANCE.updateBiome(chunk, dPos, Biome.getIdForBiome(TFBiomes.enchantedForest));
    }
}