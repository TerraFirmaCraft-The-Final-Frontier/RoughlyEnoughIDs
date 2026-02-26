package org.dimdev.jeid.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import org.dimdev.jeid.util.Mods;
import org.tff.reid.Tags;
import zone.rong.mixinbooter.ILateMixinLoader;

public class JEIDMixinLoader implements ILateMixinLoader {
    public List<String> getMixinConfigs() {
        List<String> configs = new ArrayList<>();

        // Common configs
        Arrays.stream(Mods.values())
                .filter(Mods::isLoaded)
                .map(mod -> getMixinConfigName(mod, false))
                .forEach(configs::add);

        // Client configs
        if (JEIDLoadingPlugin.isClient) {
            if (Mods.ADVANCED_ROCKETRY.isLoaded()) {
                configs.add(getMixinConfigName(Mods.ADVANCED_ROCKETRY, true));
            }
        }

        return configs;
    }

    private String getMixinConfigName(Mods mod, boolean clientOnly) {
        StringJoiner joiner = new StringJoiner(".");
        joiner.add("mixins").add(Tags.MOD_ID).add(mod.modId);
        if (clientOnly) {
            joiner.add("client");
        }
        joiner.add("json");
        return joiner.toString();
    }
}
