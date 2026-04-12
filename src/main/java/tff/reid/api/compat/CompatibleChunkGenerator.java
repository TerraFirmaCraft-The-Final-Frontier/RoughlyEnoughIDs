package tff.reid.api.compat;

import net.minecraft.world.gen.IChunkGenerator;

/**
 * Implement this if your mod has an {@link IChunkGenerator} that provides custom compatibility with REID's biome format
 * in {@link IChunkGenerator#generateChunk(int, int)}.
 * This is required, otherwise REID will overwrite your changes with its default implementation.
 */
public interface CompatibleChunkGenerator {
}
