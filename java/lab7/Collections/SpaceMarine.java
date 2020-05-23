package lab7.Collections;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

/**
 * Класс SpaceMarine, объектами которого заполняется коллекция
 *
 * @author Остряков Егор, P3112
 */
public class SpaceMarine implements Serializable {
    public static long idSetter = 1;
    private long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Float health; //Поле не может быть null, Значение поля должно быть больше 0
    private long heartCount; //Значение поля должно быть больше 0, Максимальное значение поля: 3
    private Boolean loyal = null; //Поле может быть null
    private MeleeWeapon meleeWeapon; //Поле не может быть null
    private Chapter chapter; //Поле не может быть null
    private String createdByUser;

    /**
     * Конструктор класса
     *
     * @param name        имя
     * @param coordinates координаты
     * @param health      здоровье
     * @param heartCount  количество сердечек
     * @param loyal       показатель лояльности
     * @param meleeWeapon оружие ближнего боя
     * @param chapter     глава
     */
    public SpaceMarine(String name, Coordinates coordinates, Float health, Long heartCount, Boolean loyal, MeleeWeapon meleeWeapon, Chapter chapter) {
        this.creationDate = LocalDate.now();
        setId(idSetter++);
        trySetName(name);
        trySetCoordinates(coordinates);
        trySetHealth(health);
        trySetHeartCount(heartCount);
        setLoyal(loyal);
        trySetMeleeWeapon(meleeWeapon);
        trySetChapter(chapter);
    }

    public SpaceMarine(SpaceMarine spaceMarine) {
        this.creationDate = LocalDate.now();
        setId(idSetter++);
        trySetName(spaceMarine.name);
        trySetCoordinates(spaceMarine.coordinates);
        trySetHealth(spaceMarine.health);
        trySetHeartCount(spaceMarine.heartCount);
        setLoyal(spaceMarine.loyal);
        trySetMeleeWeapon(spaceMarine.meleeWeapon);
        trySetChapter(spaceMarine.chapter);

    }

    /**
     * @return id SpaceMarine
     */
    public long getId() {
        return id;
    }

    /**
     * @param id SpaceMarine
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @param name имя SpaceMarine
     */
    public boolean trySetName(String name) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
            return true;
        }
        return false;
    }

    /**
     * @param coordinates координаты SpaceMarine
     */
    public boolean trySetCoordinates(Coordinates coordinates) {
        if (coordinates != null) {
            this.coordinates = coordinates;
            return true;
        }
        return false;
    }

    /**
     * @return здоровье SpaceMarine
     */
    public Float getHealth() {
        return health;
    }

    /**
     * @param health здоровье SpaceMarine
     */
    public boolean trySetHealth(Float health)  {
        if (health > 0) {
            this.health = health;
            return true;
        }
        return false;
    }

    /**
     * @param heartCount количество сердечек SpaceMarine
     */
    public  boolean trySetHeartCount(long heartCount){
        if (heartCount > 0 && heartCount <= 3) {
            this.heartCount = heartCount;
            return true;
        }
        return false;

    }

    /**
     * @return значение лояльности SpaceMarine
     */
    public Boolean getLoyal() {
        return loyal;
    }

    /**
     * @param loyal значение лояльности SpaceMarine
     */
    public void setLoyal(Boolean loyal) {
        this.loyal = loyal;
    }

    /**
     * @param meleeWeapon оружие ближнего боя SpaceMarine
     */
    public  boolean trySetMeleeWeapon(MeleeWeapon meleeWeapon) {
        if (meleeWeapon != null) {
            this.meleeWeapon = meleeWeapon;
            return true;
        }
        return false;

    }

    /**
     * @param chapter глава SpaceMarine
     */
    public  boolean trySetChapter(Chapter chapter){
        if (chapter != null) {
            this.chapter = chapter;
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
     * @return координаты
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * @return количество сердечек
     */
    public long getHeartCount() {
        return heartCount;
    }

    /**
     * @return тип оружия ближнего боя
     */
    public MeleeWeapon getMeleeWeapon() {
        return meleeWeapon;
    }

    /**
     * @return главу
     */
    public Chapter getChapter() {
        return chapter;
    }

    /**
     *
     * @return время создания
     */
    public Date getCreationDate() {
        return Date.valueOf(creationDate);
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public void setCreatedByUser(String createdByUser) {
        this.createdByUser = createdByUser;
    }

    /**
     * @return строковое представление объекта
     */
    @Override
    public String toString() {
        return "Космический десант пользователя " + createdByUser + " с " +
                "id = " + id +
                "{имя: '" + name + '\'' +
                ", " + coordinates +
                ", дата создания: " + creationDate +
                ", здоровье: " + health +
                ", количество сердечек: " + heartCount +
                ", значение лояльности: " + loyal +
                ", оружие ближнего боя: " + meleeWeapon +
                ", " + chapter +
                "}\n";
    }

}