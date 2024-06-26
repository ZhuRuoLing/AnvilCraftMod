package dev.dubhe.anvilcraft.client.insight;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InsightContentResourceReloadListener extends SimpleJsonResourceReloadListener {
    public static final InsightContentResourceReloadListener INSTANCE = new InsightContentResourceReloadListener();

    public static final Pattern ENTRIES_PATTERN =
            Pattern.compile("entries/([a-z0-9_.-]+)");

    private final Logger logger = LogUtils.getLogger();
    @Getter
    private final Map<ResourceLocation, InsightData> data = new HashMap<>();

    InsightContentResourceReloadListener() {
        super(new Gson(), "insight");
    }

    @Override
    protected void apply(
            @NotNull Map<ResourceLocation, JsonElement> object,
            @NotNull ResourceManager resourceManager,
            @NotNull ProfilerFiller profiler
    ) {
        data.clear();
        object.forEach((resourceLocation, jsonElement) -> {
            try {
                Matcher matcher = ENTRIES_PATTERN.matcher(resourceLocation.getPath());
                if (!matcher.matches()) {
                    logger.warn("Ignoring file {}", resourceLocation);
                    return;
                }
                DataResult<Pair<InsightData, JsonElement>> result =
                        InsightData.CODEC.decode(JsonOps.INSTANCE, jsonElement);
                InsightData data = result.getOrThrow(
                        false,
                        e -> logger.error("Error in {}: {}", resourceLocation, e)
                ).getFirst();
                data.updateStructure();
                this.data.put(resourceLocation, data);
            } catch (Exception e) {
                logger.error("Error reloading InsightContentResourceReloadListener", e);
            }
        });
        logger.info("InsightContentResourceReloadListener loaded {} entries.", data.size());
    }
}
