package lab7.Collections;

import java.io.Serializable;

/**
 * Класс с главой SpaceMarine
 *
 * @author Остряков Егор, P3112
 */
public class Chapter implements Serializable {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private String world; //Поле не может быть null

    /**
     * @param name  имя главы
     * @param world название мира
     */
    public Chapter(String name, String world) {
        trySetName(name);
        trySetWorld(world);
    }

    /**
     * @return строковое представление объекта
     */
    @Override
    public String toString() {
        return "глава{" +
                "имя=: '" + name + '\'' +
                ", мир: '" + world + '\'' +
                '}';
    }

    /**
     * @param name параметр сеттера имени
     */

    public boolean trySetName(String name) {
        if (name != null) {
            this.name = name;
            return true;
        }
        return false;
    }

    /**
     * @return имя
     */
    public String getName() {
        return name;
    }

    /**
     * @param world параметр сеттера названия мира
     */
    public boolean trySetWorld(String world) {
        if (world != null) {
            this.world = world;
            return true;
        }
        return false;
    }

    /**
     * @return мир
     */
    public String getWorld() {
        return world;
    }
}