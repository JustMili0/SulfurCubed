package net.justmili.libs.v1.config;

import net.justmili.libs.CoreLibs;
import net.justmili.libs.v1.config.entry.ConfigEntry;
import net.justmili.libs.v1.config.entry.ListConfigEntry;
import net.justmili.libs.v1.config.items.CategoryItem;
import net.justmili.libs.v1.config.type.properties.PropertiesWriter;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigLoader {
    public final String modId;
    public final String name;
    private final Path path;
    private final PropertiesWriter writer;
    public final Map<String, ConfigEntry<?>> entries = new HashMap<>();
    public final Map<String, ListConfigEntry> listEntries = new HashMap<>();
    public CategoryItem root;

    public ConfigLoader(String modId, String name, boolean createSubDirectory) {
        Path configDirectory = Path.of("config");
        this.modId = modId;
        this.name = name;
        this.writer = new PropertiesWriter();

        String extension = ".properties";
        String fileName = (name == null || name.isBlank())
            ? modId : (createSubDirectory ? name : modId+"-"+name);

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
}