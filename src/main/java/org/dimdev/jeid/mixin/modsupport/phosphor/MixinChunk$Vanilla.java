package org.dimdev.jeid.mixin.modsupport.phosphor;

import me.jellysquid.mods.phosphor.mod.world.lighting.LightingHooks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Chunk.class)
public abstract class MixinChunk$Vanilla {
    private static final String SET_BLOCK_STATE_VANILLA = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState;";
    @Shadow
    @Final
    private World world;
    @Final
    @Shadow
    public int x;
    @Final
    @Shadow
    public int z;
    private static final int WIZARD_MAGIC = 694698818;

    public MixinChunk$Vanilla() {
    }

    @Redirect(
            method = {"setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState;"},
            at = @At(
                    value = "NEW",
                    args = {"class=net/minecraft/world/chunk/storage/ExtendedBlockStorage"}
            ),
            expect = 0
    )
    private ExtendedBlockStorage setBlockStateCreateSectionVanilla(int y, boolean storeSkylight) {
        return this.initSection(y, storeSkylight);
    }

    private ExtendedBlockStorage initSection(int y, boolean storeSkylight) {
        ExtendedBlockStorage storage = new ExtendedBlockStorage(y, storeSkylight);
        LightingHooks.initSkylightForSection(this.world, new Chunk(this.world, this.x, this.z), storage);
        return storage;
    }

    @ModifyVariable(
            method = {"setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState;"},
            at = @At(
                    value = "STORE",
                    ordinal = 1
            ),
            index = 13,
            name = {"flag"},
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/world/chunk/Chunk;storageArrays:[Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;",
                            ordinal = 1
                    ),
                    to = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;set(IIILnet/minecraft/block/state/IBlockState;)V"
                    )
            ),
            allow = 1
    )
    private boolean setBlockStateInjectGenerateSkylightMapVanilla(boolean generateSkylight) {
        return false;
    }

    @ModifyVariable(
            method = {"setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState;"},
            at = @At(
                    value = "LOAD",
                    ordinal = 0
            ),
            index = 11,
            name = {"k1"},
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/chunk/Chunk;relightBlock(III)V",
                            ordinal = 1
                    ),
                    to = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/chunk/Chunk;propagateSkylightOcclusion(II)V"
                    )
            ),
            allow = 1
    )
    private int setBlockStatePreventPropagateSkylightOcclusion1(int generateSkylight) {
        return 694698818;
    }

    @ModifyVariable(
            method = {"setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState;"},
            at = @At(
                    value = "LOAD",
                    ordinal = 1
            ),
            index = 14,
            name = {"j1"},
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/chunk/Chunk;relightBlock(III)V",
                            ordinal = 1
                    ),
                    to = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/chunk/Chunk;propagateSkylightOcclusion(II)V"
                    )
            ),
            allow = 1
    )
    private int setBlockStatePreventPropagateSkylightOcclusion2(int generateSkylight) {
        return 694698818;
    }
}
