package org.dimdev.jeid.mixin.modsupport.thaumcraft;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.tff.reid.api.BiomeApi;
import org.dimdev.jeid.network.MessageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import thaumcraft.common.lib.utils.Utils;

@Mixin(value = Utils.class, remap = false)
public class MixinUtils {
    @Redirect(method = "setBiomeAt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/biome/Biome;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;setBiomeArray([B)V", remap = true))
    private static void reid$toIntBiomeArray(Chunk instance, byte[] biomeArray, World world, BlockPos pos, Biome biome) {
        BiomeApi.INSTANCE.updateBiome(instance, pos, Biome.getIdForBiome(biome));
    }

    @Redirect(method = "setBiomeAt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/biome/Biome;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/network/simpleimpl/SimpleNetworkWrapper;sendToAllAround(Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;Lnet/minecraftforge/fml/common/network/NetworkRegistry$TargetPoint;)V"))
    private static void reid$sendBiomeMessage(SimpleNetworkWrapper instance, IMessage message, NetworkRegistry.TargetPoint point, World world, BlockPos pos, Biome biome) {
        MessageManager.sendClientsBiomePosChange(world, pos, Biome.getIdForBiome(biome));
    }
}
