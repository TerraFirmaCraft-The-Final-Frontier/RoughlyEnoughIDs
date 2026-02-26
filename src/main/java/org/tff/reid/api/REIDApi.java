package org.tff.reid.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tff.reid.Tags;
import org.jetbrains.annotations.ApiStatus;

import java.util.Iterator;
import java.util.ServiceLoader;

public final class REIDApi {
    public static final String MOD_ID = Tags.MOD_ID;
    public static final String MOD_NAME = Tags.MOD_NAME;
    public static final String MOD_VERSION = Tags.VERSION;
    /**
     * The id of the provided API. Use this name to check if the API is loaded. Recommended to cache the check. For example:
     * <pre>
     * {@code
     * public static boolean reidApiLoaded = ModAPIManager.INSTANCE.hasAPI(REIDApi.API_NAME);
     * }
     * </pre>
     * You should use this way to check for the API over {@link net.minecraftforge.fml.common.Loader#isModLoaded(String)}
     * because REID shares the same mod id as JEID.
     */
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
