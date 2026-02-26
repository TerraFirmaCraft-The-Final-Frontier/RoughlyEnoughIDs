package org.tff.reid.api.biome;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

/**
 * A read-only view of the biomes in a chunk.
 */
public interface BiomeAccessor {
    /**
     * Returns the number of biomes in this chunk.
     *
     * @return the number of biomes in this chunk
     */
    int size();

    /**
     * Returns the chunk containing these biomes.
     *
     * @return the chunk containing these biomes
     */
    Chunk getChunk();

    /**
     * Returns a copy of the biomes in this chunk. Any changes to this array will NOT be reflected in the chunk.
     *
     * @return a copy of the biome ids in this chunk
     */
    int[] getBiomes();

    /**
     * Returns the biome id at the specified block position.
     *
     * @param pos the {@link BlockPos} within this chunk
     * @return the biome id at the specified block position
     * @throws IllegalArgumentException if {@code pos} is not within the chunk
     */
    int getBiomeId(BlockPos pos);

    /**
     * Returns the biome id at the specified position, relative to the chunk.
     *
     * @param chunkLocalX the x position, relative to the chunk (0-15)
     * @param chunkLocalZ the z position, relative to the chunk (0-15)
     * @return the biome id at the relative position
     * @throws IllegalArgumentException if {@code relativeX} or {@code relativeZ} is not a value within 0 to 15
     * (a position relative to the chunk)
     */
    int getBiomeId(int chunkLocalX, int chunkLocalZ);
}
