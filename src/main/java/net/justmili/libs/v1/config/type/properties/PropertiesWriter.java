package net.justmili.libs.v1.config.type.properties;

import net.justmili.libs.CoreLibs;
import net.justmili.libs.v1.config.entry.ConfigEntry;
import net.justmili.libs.v1.config.entry.ListConfigEntry;
import net.justmili.libs.v1.config.items.CategoryItem;
import net.justmili.libs.v1.config.items.CommentItem;
import net.justmili.libs.v1.config.items.ConfigItem;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertiesWriter {

    public void write(Path path, CategoryItem root) {
        File file = path.toFile();
        file.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("# "+path.getFileName()+"\n\n");
            writeItems(writer, root.children(), false);
        } catch (IOException e) {
            CoreLibs.LOGGER.error("Failed to write config: {}", e.getMessage());
        }
    }

    private void writeItems(BufferedWriter writer, List<ConfigItem> items, boolean warnedAboutCategories) throws IOException {
        for (ConfigItem item : items) {
            if (item instanceof CategoryItem categoryItem) {
                if (!warnedAboutCategories) {
                    CoreLibs.LOGGER.warn("Categories are not supported in .properties format, flattening.");
                    warnedAboutCategories = true;
                }
                writeItems(writer, categoryItem.children(), warnedAboutCategories);

            } else if (item instanceof CommentItem commentItem) {
                for (String line : commentItem.comment().split("\n")) writer.write("# "+line+"\n");

            } else if (item instanceof ListConfigEntry listEntry) {
                writer.write(listHint(listEntry)+"\n");
                writer.write(listEntry.key()+"="+listEntry.serialize()+"\n\n");

            } else if (item instanceof ConfigEntry<?> entry) {
                writer.write(hint(entry)+"\n");
                writer.write(entry.key()+"="+entry.serialize()+"\n\n");
            }
        }
    }

    public void load(Path path, Map<String, ConfigEntry<?>> entries, Map<String, ListConfigEntry> listEntries) {
        Properties properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream(path.toFile())) {
            properties.load(inputStream);
        } catch (IOException e) {
            CoreLibs.LOGGER.error("Failed to load config: {}", e.getMessage());
            return;
        }
        for (ConfigEntry<?> entry : entries.values()) entry.load(properties.getProperty(entry.key()));
        for (ListConfigEntry listEntry : listEntries.values()) listEntry.load(properties.getProperty(listEntry.key()));
    }

    private String hint(ConfigEntry<?> entry) {
        String prefix = "# ";

        Object defaultValue = entry.defaultValue();
        if (entry.hasRange()) return prefix+"Allowed range: "+entry.min()+"-"+entry.max()+" - Default: "+defaultValue;
        if (defaultValue instanceof Boolean) return prefix+"Allowed values: true, false - Default: "+defaultValue;

        return prefix+"Default: "+defaultValue;
    }

    private String listHint(ListConfigEntry entry) {
        String prefix = "# ";

        return prefix+"Allowed types: "+entry.type().getSimpleName()+" - Default: "+entry.defaultValue();
    }
}