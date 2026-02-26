package org.dimdev.jeid.mixin.modsupport.worldedit;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

import com.llamalad7.mixinextras.sugar.Local;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.command.BiomeCommands;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.forge.ForgeWorld;
import com.sk89q.worldedit.function.visitor.FlatRegionVisitor;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.biome.BaseBiome;
import org.tff.reid.api.BiomeApi;
import org.dimdev.jeid.network.MessageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(value = BiomeCommands.class, remap = false)
public class MixinBiomeCommands {
    /**
     * @reason Send per-chunk packets (instead of per-position in {@link ForgeWorld}) to client to reduce network usage.
     */
    @Inject(method = "setBiome", at = @At(value = "INVOKE", target = "Lcom/sk89q/worldedit/entity/Player;print(Ljava/lang/String;)V"), cancellable = true)
    private void reid$sendBiomeChunksChange(Player player, LocalSession session, EditSession editSession, BaseBiome target, boolean atPosition, CallbackInfo ci,
                                            @Local World world, @Local Region region, @Local FlatRegionVisitor visitor) {
        if (!(world instanceof ForgeWorld)) return;
        net.minecraft.world.World trueWorld = ((ForgeWorld) world).getWorld();
        Set<Vector2D> chunks = region.getChunks();
        for (Vector2D chunkPos : chunks) {
            BlockPos pos = new BlockPos(chunkPos.getBlockX() << 4, 0.0D, chunkPos.getBlockZ() << 4);
            Chunk chunk = trueWorld.getChunk(chunkPos.getBlockX(), chunkPos.getBlockZ());
            // Using chunks instead of area because WorldEdit allows non-cuboid regions.
            MessageManager.sendClientsBiomeChunkChange(trueWorld, pos, BiomeApi.INSTANCE.getBiomeAccessor(chunk).getBiomes());
        }
        // Changes are immediately reflected on client.
        player.print("Biomes were changed in " + visitor.getAffected() + " columns.");
        ci.cancel();
    }
}
