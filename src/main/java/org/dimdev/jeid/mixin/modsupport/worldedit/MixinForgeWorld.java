package org.dimdev.jeid.mixin.modsupport.worldedit;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

import com.llamalad7.mixinextras.sugar.Local;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.forge.ForgeWorld;
import com.sk89q.worldedit.world.biome.BaseBiome;
import org.tff.reid.api.BiomeApi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ForgeWorld.class, remap = false)
public class MixinForgeWorld {
    @Inject(method = "setBiome", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;getBiomeArray()[B", remap = true))
    private void reid$toIntBiomeArray(Vector2D position, BaseBiome biome, CallbackInfoReturnable<Boolean> cir,
                                      @Local Chunk chunk) {
        BiomeApi.INSTANCE.updateBiome(chunk, new BlockPos(position.getX(), 0, position.getZ()), biome.getId());
    }
}
