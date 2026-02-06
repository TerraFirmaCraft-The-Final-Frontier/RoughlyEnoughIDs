package org.dimdev.jeid.mixin.modsupport.scapeandrunparasites;

import com.dhanantry.scapeandrunparasites.block.BlockBiomePurifier;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.ducks.INewChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockBiomePurifier.class, remap = false)
public class MixinPurifyBiome {
    /**
     * @author roguetictac, jchung01
     * @reason Support int biome id for resetting infected biome.
     */
    @Inject(method = "positionToBiome", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getChunk(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/chunk/Chunk;", remap = true), cancellable = true)
    private static void reid$plainsToIntBiomeArray(World worldIn, BlockPos pos, CallbackInfo ci,
                                                   @Local(ordinal = 0) int inChunkX, @Local(ordinal = 1) int inChunkZ) {
        Biome original = Biomes.PLAINS;
        Chunk chunk = worldIn.getChunk(pos);
        chunk.markDirty();
        ((INewChunk) chunk).getIntBiomeArray()[inChunkZ << 4 | inChunkX] = Biome.getIdForBiome(original);
        ci.cancel();
    }
}
