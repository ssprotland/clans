package clans.configuration;

import java.lang.reflect.Field;

import org.bukkit.configuration.file.FileConfiguration;

import clans.clans;

public class Loader {
    // loading from file using reflection(if var isnt declareted in file, then leave
    // it default)
    public static void loadConfiguration(FileConfiguration config) {
        clans.log("loading config...");
        Field[] fields = Config.class.getFields();

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            Object dat = config.get(field.getName());
            //clans.log("field name: "+ field.getName());
            //clans.log("stored value: "+ dat.toString());
            if (dat != null) {
                try {
                    field.set(null, dat);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

           
        }
    }

    // saving config using reflection
    public static void saveConfiguration(FileConfiguration config) {
        clans.log("saving config...");
        Field[] fields = Config.class.getFields();

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];

            try {
                config.set(field.getName(), field.get(null));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        
    }
}
