package org.dimdev.jeid.mixin.modsupport.scapeandrunparasites;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteSpreading;
import com.dhanantry.scapeandrunparasites.init.SRPBiomes;
import com.dhanantry.scapeandrunparasites.util.SRPReference;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.ducks.INewChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockParasiteSpreading.class, remap = false)
public class MixinBlockSpreadBiome {
    /**
     * @author roguetictac, jchung01
     * @reason Support int biome id for spreading infected biome.
     */
    @Inject(method = "positionToParasiteBiome", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getChunk(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/chunk/Chunk;", remap = true), cancellable = true)
    private static void reid$parasiteToIntBiomeArray(World worldIn, BlockPos pos, int type, CallbackInfo ci) {

        GameRules rules = worldIn.getGameRules();
        // Biome id local was cast to byte, so it may not be safe to use
        Biome targetbiome = rules.getBoolean("srpForceHarlequin") ?
                SRPBiomes.biomeHarlequin : SRPReference.getBiomeFromInt(type);
        Chunk chunk = worldIn.getChunk(pos);
        int inChunkX = pos.getX() & 15;
        int inChunkZ = pos.getZ() & 15;
        ((INewChunk) chunk).getIntBiomeArray()[inChunkZ << 4 | inChunkX] = Biome.getIdForBiome(targetbiome);
        chunk.markDirty();
        worldIn.markBlocksDirtyVertical(pos.getX(), pos.getZ(), 0, pos.getY());
        ci.cancel();
    }
}
