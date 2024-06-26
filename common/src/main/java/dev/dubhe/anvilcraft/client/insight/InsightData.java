package dev.dubhe.anvilcraft.client.insight;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class InsightData {
    public static final Codec<InsightData> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.STRING.fieldOf("title").forGetter(o -> o.title),
            Codec.either(Codec.STRING.listOf(), Codec.STRING)
                    .fieldOf("feature")
                    .forGetter(o -> Either.left(o.feature)),
            Codec.either(ResourceLocation.CODEC.listOf(), ResourceLocation.CODEC)
                    .fieldOf("icon").forGetter(o -> Either.left(o.icon)),
            ResourceLocation.CODEC.optionalFieldOf("structure").forGetter(o -> Optional.ofNullable(o.structure)),
            Codec.STRING.listOf().fieldOf("text").forGetter(o -> o.text)
    ).apply(ins, InsightData::new));

    private final String title;
    private List<String> feature;
    private List<ResourceLocation> icon;
    private ResourceLocation structure;
    private final List<String> text;

    InsightData(
            String title,
            Either<List<String>, String> feature,
            Either<List<ResourceLocation>, ResourceLocation> icon,
            Optional<ResourceLocation> structure,
            List<String> text
    ) {
        this.title = title;
        feature.ifLeft(l -> this.feature = l)
                .ifRight(r -> this.feature = List.of(r));
        icon.ifLeft(l -> this.icon = l)
                .ifRight(r -> this.icon = List.of(r));
        this.structure = structure.orElse(null);
        this.text = text;
    }

    void updateStructure() {
        String prefix = "structures/";
        if (!structure.getPath().startsWith(prefix)) {
            structure = new ResourceLocation(structure.getNamespace(), prefix + structure.getPath());
        }
    }

    /**
     * 获得格式化后的标题
     */
    public String getFormattedTitle() {
        Component component = Component.translatable(title);
        return component.getString();
    }

    /**
     * 获得格式化后的内容
     */
    public List<String> getFormattedContents() {
        return text.stream()
                .map(Component::translatable)
                .map(Component::getString)
                .collect(Collectors.toCollection(ArrayList::new));
    }


}
