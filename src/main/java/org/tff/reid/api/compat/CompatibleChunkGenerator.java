package org.tff.reid.api.compat;

import net.minecraft.world.gen.IChunkGenerator;

/**
 * Implement this if your mod has a {@link IChunkGenerator} that already provides compatibility with REID's biome format
 * in {@link IChunkGenerator#generateChunk(int, int)}.
 * Not strictly necessary, but will reduce chunk generation overhead from REID.
 */
public interface CompatibleChunkGenerator {
}
