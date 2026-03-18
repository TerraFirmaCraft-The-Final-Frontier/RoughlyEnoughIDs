package tff.reid.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tff.reid.Tags;
import org.jetbrains.annotations.ApiStatus;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Holds API constants for REID.
 * <br>
 * Use the {@code API_ID} {@value API_ID} (as a literal) to check if the API is loaded. Take care not to reference any
 * of these constants before checking the API is loaded, as it may not exist at runtime.
 * Recommended to cache this check. As an example:
 * <pre>
 * {@code
 * boolean reidApiLoaded = ModAPIManager.INSTANCE.hasAPI("RoughlyEnoughIds|API");
 * }
 * </pre>
 * This is the preferred way to check for the API over {@link net.minecraftforge.fml.common.Loader#isModLoaded(String)}
 * because REID shares the same mod id as JEID. Alternatively, you could explicitly check for the mod name and first
 * version that REID started providing its API (2.3.0).
 */
public final class REIDApi {
    public static final String MOD_ID = Tags.MOD_ID;
    public static final String MOD_NAME = Tags.MOD_NAME;
    public static final String MOD_VERSION = Tags.VERSION;
    public static final String API_ID = Tags.API_NAME;
    public static final String API_VERSION = Tags.API_VERSION;

    public static final Logger LOGGER = LogManager.getLogger(API_ID);

    @ApiStatus.Internal
    static <T> T loadService(Class<T> clazz) {
        Iterator<T> itr = ServiceLoader.load(clazz).iterator();
        if (itr.hasNext()) {
            return itr.next();
        } else {
            throw new NullPointerException("Failed to load service for " + clazz.getName());
        }
    }
}
