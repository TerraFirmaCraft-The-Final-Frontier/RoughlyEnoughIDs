package org.dimdev.jeid.mixin.modsupport.biometweaker;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.llamalad7.mixinextras.sugar.Local;
import me.superckl.biometweaker.server.command.CommandSetBiome;
import org.tff.reid.api.BiomeApi;
import org.dimdev.jeid.network.MessageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandSetBiome.class)
public class MixinCommandSetBiome {
    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;getBiomeArray()[B"))
    private void reid$setBiomeArrayElement(MinecraftServer server, ICommandSender sender, String[] args, CallbackInfo ci,
                                           @Local(name = "id") int id, @Local(name = "x") int x, @Local(name = "z") int z, @Local(name = "chunk") Chunk chunk) {
        BiomeApi.INSTANCE.updateBiome(chunk, new BlockPos(x, 0, z), id);
    }

    @Inject(method = "execute",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;getX()I", ordinal = 0),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;getX()I", ordinal = 2)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/command/ICommandSender;sendMessage(Lnet/minecraft/util/text/ITextComponent;)V"))
    private void reid$sendBiomeAreaChange(MinecraftServer server, ICommandSender sender, String[] args, CallbackInfo ci,
                                          @Local(name = "coord") BlockPos coord, @Local(name = "world") World world,
                                          @Local(name = "i") Integer radius, @Local(name = "id") int id) {
        MessageManager.sendClientsBiomeAreaChange(world, coord, radius, id);
    }

    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;setBiomeArray([B)V"))
    private void reid$setBiomeArray(Chunk instance, byte[] biomeArray,
                                    @Local(name = "chunk") Chunk chunk, @Local(name = "id") int id) {
        BiomeApi.INSTANCE.fillWithBiome(chunk, id);
    }
}
