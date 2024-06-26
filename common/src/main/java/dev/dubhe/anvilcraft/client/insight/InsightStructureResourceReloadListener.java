package dev.dubhe.anvilcraft.client.insight;

import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import lombok.Getter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class InsightStructureResourceReloadListener
        extends SimplePreparableReloadListener<Map<ResourceLocation, CompoundTag>> {
    public static final InsightStructureResourceReloadListener INSTANCE = new InsightStructureResourceReloadListener();
    public static final Pattern STRUCTURES_PATTERN =
            Pattern.compile("structures/([a-z0-9_.-]+)");
    private static final Logger logger = LogUtils.getLogger();
    private final Map<ResourceLocation, StructureTemplate> data = new HashMap<>();

    InsightStructureResourceReloadListener() {
    }

    private static void scanDirectory(
            ResourceManager resourceManager,
            String name,
            Map<ResourceLocation, CompoundTag> output
    ) {
        FileToIdConverter fileToIdConverter = new FileToIdConverter(name, ".nbt");
        for (Map.Entry<ResourceLocation, Resource> entry :
                fileToIdConverter.listMatchingResources(resourceManager).entrySet()
        ) {
            ResourceLocation resourceLocation = entry.getKey();
            ResourceLocation resourceLocation2 = fileToIdConverter.fileToId(resourceLocation);

            try (InputStream is = entry.getValue().open()) {
                output.put(resourceLocation2, NbtIo.readCompressed(is));
            } catch (IllegalArgumentException | IOException | JsonParseException ex) {
                logger.error("Couldn't parse data file {} from {}", resourceLocation2, resourceLocation, ex);
            }
        }
    }

    @Override
    protected @NotNull Map<ResourceLocation, CompoundTag> prepare(
            @NotNull ResourceManager resourceManager,
            @NotNull ProfilerFiller profiler
    ) {
        Map<ResourceLocation, CompoundTag> map = new HashMap<>();
        scanDirectory(resourceManager, "insight", map);
        return map;
    }

    @Override
    protected void apply(
            @NotNull Map<ResourceLocation, CompoundTag> object,
            @NotNull ResourceManager resourceManager,
            @NotNull ProfilerFiller profiler
    ) {
        data.clear();
        object.forEach((resourceLocation, elem) -> {
            try {
                Matcher matcher = STRUCTURES_PATTERN.matcher(resourceLocation.getPath());
                if (!matcher.matches()) {
                    logger.warn("Ignoring file {}", resourceLocation);
                    return;
                }
                StructureTemplate template = new StructureTemplate();
                template.load(BuiltInRegistries.BLOCK.asLookup(), elem);
                this.data.put(resourceLocation, template);
            } catch (Exception e) {
                logger.error("Error reloading InsightStructureResourceReloadListener", e);
            }
        });
        logger.info("InsightStructureResourceReloadListener loaded {} entries.", data.size());
    }
}
