package org.dimdev.jeid.mixin.modsupport.advancedrocketry;

import net.minecraft.world.chunk.Chunk;

import io.netty.buffer.ByteBuf;
import org.tff.reid.api.BiomeApi;
import org.dimdev.jeid.impl.type.BiomeContainer;
import org.dimdev.jeid.impl.type.BiomeStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zmaster587.advancedRocketry.network.PacketBiomeIDChange;

@Mixin(value = PacketBiomeIDChange.class, remap = false)
public class MixinPacketBiomeIDChange implements BiomeStorage {
    @Shadow
    Chunk chunk;
    @Unique
    BiomeContainer reid$biomes;

    @Inject(method = "<init>()V", at = @At("RETURN"))
    public void onConstructed(CallbackInfo ci) {
        // Chunk is uninitialized, but passing null should be fine as it's not used.
        reid$biomes = new BiomeContainer(null, 256);
    }

    @Inject(method = "write", at = @At(value = "INVOKE", target = "Lio/netty/buffer/ByteBuf;writeBytes([B)Lio/netty/buffer/ByteBuf;"), cancellable = true)
    private void reid$writeIntBiomeIds(ByteBuf out, CallbackInfo ci) {
        for (int biomeId : BiomeApi.INSTANCE.getBiomeAccessor(chunk).getBiomes()) {
            out.writeInt(biomeId);
        }
        ci.cancel();
    }

    @Inject(method = "readClient", at = @At(value = "INVOKE", target = "Lio/netty/buffer/ByteBuf;readBytes([B)Lio/netty/buffer/ByteBuf;"), cancellable = true)
    private void reid$readIntBiomeIds(ByteBuf in, CallbackInfo ci) {
        for (int i = 0; i < 256; i++) {
            int biomeId = in.readInt();
            reid$biomes.setBiome(i, biomeId);
        }
        ci.cancel();
    }

    @Override
    public BiomeContainer reid$getBiomes() {
        return reid$biomes;
    }
}
