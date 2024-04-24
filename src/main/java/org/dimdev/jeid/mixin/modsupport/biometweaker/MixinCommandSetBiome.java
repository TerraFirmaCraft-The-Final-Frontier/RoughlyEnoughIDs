package org.dimdev.jeid.mixin.modsupport.biometweaker;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.superckl.biometweaker.server.command.CommandSetBiome;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.JEID;
import org.dimdev.jeid.ducks.INewChunk;
import org.dimdev.jeid.network.MessageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(CommandSetBiome.class)
public class MixinCommandSetBiome {
    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;getBiomeArray()[B"))
    private void reid$setBiomeArrayElement(MinecraftServer server, ICommandSender sender, String[] args, CallbackInfo ci,
                                           @Local BlockPos coord, @Local World world, @Local(ordinal = 0) int id,
                                           @Local(ordinal = 2) int x, @Local(ordinal = 3) int z, @Local Chunk chunk) {
        JEID.LOGGER.info("setting biome at {}, {}", x, z);
        // Method calls markDirty()
        ((INewChunk) chunk).getIntBiomeArray()[(z & 0xF) << 4 | x & 0xF] = id;
        // TODO: send single packet for all changes
        MessageManager.sendClientsBiomeChange(world, new BlockPos(x, coord.getY(), z), id);
    }

    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Ljava/util/Arrays;fill([BB)V", remap = false))
    private void reid$initBiomeArray(CallbackInfo ci, @Local(ordinal = 0) int id, @Share("intBiomeArray") LocalRef<int[]> intBiomeArray) {
        final int[] arr = new int[256];
        Arrays.fill(arr, id);
        intBiomeArray.set(arr);
    }

    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;setBiomeArray([B)V"))
    private void reid$setBiomeArray(Chunk instance, byte[] biomeArray,
                                    @Local BlockPos coord, @Local World world, @Local(ordinal = 4) int chunkX,
                                    @Local(ordinal = 5) int chunkZ, @Share("intBiomeArray") LocalRef<int[]> intBiomeArray) {
        // Method calls markDirty()
        int posX = chunkX << 4;
        int posZ = chunkZ << 4;
        ((INewChunk) world.getChunk(chunkX, chunkZ)).setIntBiomeArray(Arrays.copyOf(intBiomeArray.get(), intBiomeArray.get().length));
        MessageManager.sendClientsBiomeArray(world, new BlockPos(posX, coord.getY(), posZ), Arrays.copyOf(intBiomeArray.get(), intBiomeArray.get().length));
    }
}
