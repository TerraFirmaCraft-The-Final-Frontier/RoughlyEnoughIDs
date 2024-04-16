package org.dimdev.jeid.mixin.modsupport.phosphor;

import com.llamalad7.mixinextras.sugar.Local;
import me.jellysquid.mods.phosphor.api.IChunkLighting;
import me.jellysquid.mods.phosphor.api.IChunkLightingData;
import me.jellysquid.mods.phosphor.api.ILightingEngine;
import me.jellysquid.mods.phosphor.api.ILightingEngineProvider;
import me.jellysquid.mods.phosphor.mod.world.WorldChunkSlice;
import me.jellysquid.mods.phosphor.mod.world.lighting.LightingHooks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.dimdev.jeid.biome.BiomeError;
import org.dimdev.jeid.ducks.INewChunk;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(value = {Chunk.class},priority = 1100)
public abstract class MixinChunk implements IChunkLighting, IChunkLightingData, ILightingEngineProvider, INewChunk {
    @Unique
    private static final byte ERROR_BIOME_ID = (byte) Biome.REGISTRY.getIDForObject(BiomeError.getInstance());
    @Unique
    private static final byte[] EMPTY_BLOCK_BIOME_ARRAY = new byte[256];
    @Unique
    private final int[] intBiomeArray = generateIntBiomeArray();

    @Unique
    private static int[] generateIntBiomeArray() {
        int[] arr = new int[256];
        Arrays.fill(arr, -1);
        return arr;
    }

    @Override
    public int[] getIntBiomeArray() {
        return intBiomeArray;
    }

    @Override
    public void setIntBiomeArray(int[] intBiomeArray) {
        System.arraycopy(intBiomeArray, 0, this.intBiomeArray, 0, this.intBiomeArray.length);
    }

    @Inject(method = "getBiomeArray", at = @At(value = "RETURN"), cancellable = true)
    private void reid$returnErrorBiomeArray(CallbackInfoReturnable<byte[]> cir) {
        byte[] arr = new byte[256];
        Arrays.fill(arr, ERROR_BIOME_ID);
        cir.setReturnValue(arr);
    }

    @Dynamic("Read biome id from int biome array to int k")
    @ModifyVariable(method = "getBiome", at = @At(value = "STORE", ordinal = 0), name = "k")
    private int reid$fromIntBiomeArray(int original, @Local(name = "i") int i, @Local(name = "j") int j) {
        return this.intBiomeArray[j << 4 | i];
    }

    /**
     * Compatibility for mods that don't initialize the chunk's biome array on generation (e.g. Chunk-Pregenerator)
     *
     * @reason Use intBiomeArray's default value.
     */
    @ModifyConstant(method = "getBiome", constant = @Constant(intValue = 255, ordinal = 1))
    private int reid$modifyDefaultId(int original) {
        return -1;
    }

    @Inject(method = "getBiome", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/biome/Biome;getIdForBiome(Lnet/minecraft/world/biome/Biome;)I"))
    private void reid$toIntBiomeArray(CallbackInfoReturnable<Biome> cir, @Local(name = "i") int i, @Local(name = "j") int j, @Local(name = "k") int k) {
        this.intBiomeArray[j << 4 | i] = k;
    }

    /**
     * @reason Disable default biome array write logic.
     */
    @Redirect(method = "getBiome", at = @At(value = "FIELD", target = "Lnet/minecraft/world/chunk/Chunk;blockBiomeArray:[B", opcode = Opcodes.GETFIELD, ordinal = 1))
    private byte[] reid$defaultWriteBiomeArray(Chunk instance) {
        return EMPTY_BLOCK_BIOME_ARRAY;
    }


    private static final EnumFacing[] HORIZONTAL;
    @Shadow
    @Final
    private ExtendedBlockStorage[] storageArrays;
    @Shadow
    private boolean dirty;
    @Shadow
    @Final
    private int[] heightMap;
    @Shadow
    private int heightMapMinimum;
    @Shadow
    @Final
    private int[] precipitationHeightMap;
    @Shadow
    @Final
    private World world;
    @Shadow
    private boolean isTerrainPopulated;
    @Final
    @Shadow
    private boolean[] updateSkylightColumns;
    @Final
    @Shadow
    public int x;
    @Final
    @Shadow
    public int z;
    @Shadow
    private boolean isGapLightingUpdated;
    private short[] neighborLightChecks;
    private boolean isLightInitialized;
    private ILightingEngine lightingEngine;

    @Shadow
    public abstract TileEntity getTileEntity(BlockPos var1, Chunk.EnumCreateEntityType var2);

    @Shadow
    public abstract IBlockState getBlockState(BlockPos var1);

    @Shadow
    protected abstract int getBlockLightOpacity(int var1, int var2, int var3);

    @Shadow
    public abstract boolean canSeeSky(BlockPos var1);

    @Inject(
            method = {"<init>"},
            at = {@At("RETURN")}
    )
    private void onConstructed(CallbackInfo ci) {
        this.lightingEngine = ((ILightingEngineProvider)this.world).getLightingEngine();
    }

    @Inject(
            method = {"getLightSubtracted"},
            at = {@At("HEAD")}
    )
    private void onGetLightSubtracted(BlockPos pos, int amount, CallbackInfoReturnable<Integer> cir) {
        this.lightingEngine.processLightUpdates();
    }

    @Inject(
            method = {"onLoad"},
            at = {@At("RETURN")}
    )
    private void onLoad(CallbackInfo ci) {
        LightingHooks.scheduleRelightChecksForChunkBoundaries(this.world, new Chunk(this.world, this.x, this.z));
    }

    @Redirect(
            method = {"setLightFor"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/chunk/Chunk;generateSkylightMap()V"
            ),
            expect = 0
    )
    private void setLightForRedirectGenerateSkylightMap(Chunk chunk, EnumSkyBlock type, BlockPos pos, int value) {
        LightingHooks.initSkylightForSection(this.world, new Chunk(this.world,this.x,this.z), this.storageArrays[pos.getY() >> 4]);
    }

    @Overwrite
    private void relightBlock(int x, int y, int z) {
        int i = this.heightMap[z << 4 | x] & 255;
        int j = i;
        if (y > i) {
            j = y;
        }

        while(j > 0 && this.getBlockLightOpacity(x, j - 1, z) == 0) {
            --j;
        }

        if (j != i) {
            this.heightMap[z << 4 | x] = j;
            if (this.world.provider.hasSkyLight()) {
                LightingHooks.relightSkylightColumn(this.world, new Chunk(this.world,this.x,this.z), x, z, i, j);
            }

            int l1 = this.heightMap[z << 4 | x];
            if (l1 < this.heightMapMinimum) {
                this.heightMapMinimum = l1;
            }
        }

    }

    @Overwrite
    public int getLightFor(EnumSkyBlock type, BlockPos pos) {
        this.lightingEngine.processLightUpdatesForType(type);
        return this.getCachedLightFor(type, pos);
    }

    @Overwrite
    public void checkLight() {
        this.isTerrainPopulated = true;
        LightingHooks.checkChunkLighting(new Chunk(this.world,this.x,this.z), this.world);
    }

    @Overwrite
    private void recheckGaps(boolean onlyOne) {
        this.world.profiler.startSection("recheckGaps");
        WorldChunkSlice slice = new WorldChunkSlice(this.world, this.x, this.z);
        if (this.world.isAreaLoaded(new BlockPos(this.x * 16 + 8, 0, this.z * 16 + 8), 16)) {
            for(int x = 0; x < 16; ++x) {
                for(int z = 0; z < 16; ++z) {
                    if (this.recheckGapsForColumn(slice, x, z) && onlyOne) {
                        this.world.profiler.endSection();
                        return;
                    }
                }
            }

            this.isGapLightingUpdated = false;
        }

        this.world.profiler.endSection();
    }

    private boolean recheckGapsForColumn(WorldChunkSlice slice, int x, int z) {
        int i = x + z * 16;
        if (this.updateSkylightColumns[i]) {
            this.updateSkylightColumns[i] = false;
            int height = this.getHeightValue(x, z);
            int x1 = this.x * 16 + x;
            int z1 = this.z * 16 + z;
            int max = this.recheckGapsGetLowestHeight(slice, x1, z1);
            this.recheckGapsSkylightNeighborHeight(slice, x1, z1, height, max);
            return true;
        } else {
            return false;
        }
    }

    private int recheckGapsGetLowestHeight(WorldChunkSlice slice, int x, int z) {
        int max = Integer.MAX_VALUE;
        EnumFacing[] var5 = HORIZONTAL;
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            EnumFacing facing = var5[var7];
            int j = x + facing.getXOffset();
            int k = z + facing.getZOffset();
            max = Math.min(max, slice.getChunkFromWorldCoords(j, k).getLowestHeight());
        }

        return max;
    }

    private void recheckGapsSkylightNeighborHeight(WorldChunkSlice slice, int x, int z, int height, int max) {
        this.checkSkylightNeighborHeight(slice, x, z, max);
        EnumFacing[] var6 = HORIZONTAL;
        int var7 = var6.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            EnumFacing facing = var6[var8];
            int j = x + facing.getXOffset();
            int k = z + facing.getZOffset();
            this.checkSkylightNeighborHeight(slice, j, k, height);
        }

    }

    private void checkSkylightNeighborHeight(WorldChunkSlice slice, int x, int z, int maxValue) {
        int i = slice.getChunkFromWorldCoords(x, z).getHeightValue(x & 15, z & 15);
        if (i > maxValue) {
            this.updateSkylightNeighborHeight(slice, x, z, maxValue, i + 1);
        } else if (i < maxValue) {
            this.updateSkylightNeighborHeight(slice, x, z, i, maxValue + 1);
        }

    }

    private void updateSkylightNeighborHeight(WorldChunkSlice slice, int x, int z, int startY, int endY) {
        if (endY > startY) {
            if (!slice.isLoaded(x, z, 16)) {
                return;
            }

            for(int i = startY; i < endY; ++i) {
                this.world.checkLightFor(EnumSkyBlock.SKY, new BlockPos(x, i, z));
            }

            this.dirty = true;
        }

    }

    @Shadow
    public abstract int getHeightValue(int var1, int var2);

    public short[] getNeighborLightChecks() {
        return this.neighborLightChecks;
    }

    public void setNeighborLightChecks(short[] data) {
        this.neighborLightChecks = data;
    }

    public ILightingEngine getLightingEngine() {
        return this.lightingEngine;
    }

    public boolean isLightInitialized() {
        return this.isLightInitialized;
    }

    public void setLightInitialized(boolean lightInitialized) {
        this.isLightInitialized = lightInitialized;
    }

    @Shadow
    protected abstract void setSkylightUpdated();

    public void setSkylightUpdatedPublic() {
        this.setSkylightUpdated();
    }

    public int getCachedLightFor(EnumSkyBlock type, BlockPos pos) {
        int i = pos.getX() & 15;
        int j = pos.getY();
        int k = pos.getZ() & 15;
        ExtendedBlockStorage extendedblockstorage = this.storageArrays[j >> 4];
        if (extendedblockstorage == Chunk.NULL_BLOCK_STORAGE) {
            return this.canSeeSky(pos) ? type.defaultLightValue : 0;
        } else if (type == EnumSkyBlock.SKY) {
            return !this.world.provider.hasSkyLight() ? 0 : extendedblockstorage.getSkyLight(i, j & 15, k);
        } else {
            return type == EnumSkyBlock.BLOCK ? extendedblockstorage.getBlockLight(i, j & 15, k) : type.defaultLightValue;
        }
    }


    static {
        HORIZONTAL = EnumFacing.Plane.HORIZONTAL.facings();
    }
}
