package org.dimdev.jeid.mixin.modsupport.phosphor;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.jellysquid.mods.phosphor.api.ILightingEngineProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.jeid.ducks.INewChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SPacketChunkData.class)
public abstract class MixinSPacketChunkData {
    @Shadow
    public abstract boolean isFullChunk();

    /**
     * @reason Write the biome int array.
     **/
    @Inject(method = "extractChunkData", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/play/server/SPacketChunkData;isFullChunk()Z", ordinal = 1))
    public void reid$writeBiomeArray(PacketBuffer buf, Chunk chunk, boolean writeSkylight, int changedSectionFilter, CallbackInfoReturnable<Integer> cir) {
        if (isFullChunk()) {
            buf.writeVarIntArray(((INewChunk) chunk).getIntBiomeArray());
        }
    }

    /**
     * @reason Disable writing biome byte array.
     **/
    @Redirect(method = "extractChunkData", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/play/server/SPacketChunkData;isFullChunk()Z", ordinal = 1))
    public boolean reid$getIsFullChunk(SPacketChunkData packet) {
        return false;
    }

    /**
     * @reason Disable adding biome byte array size.
     **/
    @Redirect(method = "calculateChunkSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/play/server/SPacketChunkData;isFullChunk()Z", ordinal = 1))
    public boolean reid$getIsFullChunk1(SPacketChunkData packet) {
        return false;
    }

    @ModifyReturnValue(method = "calculateChunkSize", at = @At(value = "RETURN"))
    public int reid$addIntBiomeArraySize(int originalSize, Chunk chunkIn) {
        if (isFullChunk()) {
            return originalSize + this.getVarIntArraySize(((INewChunk) chunkIn).getIntBiomeArray());
        }
        return originalSize;
    }

    @Unique
    private int getVarIntArraySize(int[] array) {
        int size = PacketBuffer.getVarIntSize(array.length);
        for (int i : array) {
            size += PacketBuffer.getVarIntSize(i);
        }
        return size;
    }

    @Inject(
            method = {"calculateChunkSize"},
            at = {@At("HEAD")}
    )
    private void onCalculateChunkSize(Chunk chunkIn, boolean hasSkyLight, int changedSectionFilter, CallbackInfoReturnable<Integer> cir) {
        ((ILightingEngineProvider)chunkIn).getLightingEngine().processLightUpdates();
    }
}
