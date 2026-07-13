package net.justmili.libs.v1.config;

import net.justmili.libs.CoreLibs;
import net.justmili.libs.v1.config.entry.ConfigEntry;
import net.justmili.libs.v1.config.entry.ListConfigEntry;
import net.justmili.libs.v1.config.items.CategoryItem;
import net.justmili.libs.v1.config.sync.SyncConfigCSP;
import net.justmili.libs.v1.config.writers.FormatWriter;
import net.justmili.libs.v1.config.writers.properties.PropertiesWriter;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigLoader {
    public final String modId;
    public final String suffix;
    private final Path path;
    private final FormatWriter writer;
    public final Map<String, ConfigEntry<?>> entries = new HashMap<>();
    public final Map<String, ListConfigEntry> listEntries = new HashMap<>();
    public CategoryItem root;

    public ConfigLoader(String modId, ConfigType configType, FileType fileType, boolean createSubDirectory) {
        Path configDirectory = Path.of("config");
        this.modId = modId;
        this.suffix = switch (configType) {
            case CLIENT -> "client";
            case SERVER -> "server";
            case COMMON -> "common";
            case COMMON_SERVER_PRIORITY -> "common-sp";
            case MIXINS -> "mixins";
        };
        if (configType == ConfigType.COMMON_SERVER_PRIORITY) {
            SyncConfigCSP.register(this);
        }
        this.writer = resolveWriter(fileType);

        String extension = extension(fileType);
        String fileName = createSubDirectory ? suffix : modId+"-"+ suffix;

        path = createSubDirectory
            ? configDirectory.resolve(modId).resolve(fileName+extension)
            : configDirectory.resolve(fileName+extension);
    }

    public void register(ConfigEntry<?> entry) {
        entries.put(entry.key(), entry);
    }

    public void registerList(ListConfigEntry entry) {
        listEntries.put(entry.key(), entry);
    }

    public void loadOrCreate(CategoryItem root) {
        this.root = root;
        File file = path.toFile();
        if (!file.exists()) {
            CoreLibs.LOGGER.info("No config found, creating defaults.");
            writer.write(path, root);
            return;
        }
        writer.load(path, entries, listEntries);
        writer.write(path, root);
    }

    public void save() {
        writer.write(path, root);
    }

    private static FormatWriter resolveWriter(FileType fileType) {
        return switch (fileType) {
            case JSON -> throw new UnsupportedOperationException("JSON support is not yet implemented.");
            case JSON5 -> throw new UnsupportedOperationException("JSON5 support is not yet implemented.");
            case YAML, YML -> throw new UnsupportedOperationException("YAML/YML support is not yet implemented.");
            case TOML -> throw new UnsupportedOperationException("TOML support is not yet implemented.");
            default -> new PropertiesWriter();
        };
    }

    private static String extension(FileType fileType) {
        return switch (fileType) {
            case JSON -> ".json";
            case JSON5 -> ".json5";
            case YAML -> ".yaml";
            case YML -> ".yml";
            case TOML -> ".toml";
            default -> ".properties";
        };
    }
}