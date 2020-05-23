package lab7;

import lab7.Collections.Chapter;
import lab7.Collections.Coordinates;
import lab7.Collections.MeleeWeapon;
import lab7.Collections.SpaceMarine;
import lab7.Commands.CommandsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.*;
import java.time.LocalDate;
import java.util.PriorityQueue;

public class DataBaseManager implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(Server.class.getName());
    private final String URL = "jdbc:postgresql://localhost:5432/Lab7";
    private String USER = "postgres";
    private String PASSWORD = "14881488";

    public DataBaseManager() {
        try {
            Class.forName("org.postgresql.Driver");
            logger.info("Драйвер подключён");
        } catch (ClassNotFoundException e) {
            logger.error("PostgreSQL JDBC Driver не найден.");
            e.printStackTrace();
        }
    }

    /**
     * Добавляет юзера в БД и выдаёт ему необходимые права, для работы с бд, помещая в специальную группу
     *
     * @param login    логин
     * @param password пароль
     * @return false, если не удалось
     */
    public boolean addUser(String login, String password) {
        logger.info("Пытаемся добавить пользователя в базу данных");
        try (Connection connection = DriverManager.getConnection(URL, "postgres", "14881488")) {
            String query = "CREATE ROLE " + login + " WITH\n" +
                    "  LOGIN\n" +
                    "  NOSUPERUSER\n" +
                    "  INHERIT\n" +
                    "  NOCREATEDB\n" +
                    "  NOCREATEROLE\n" +
                    "  NOREPLICATION\n" +
                    "  ENCRYPTED PASSWORD '" + password + "';\n" +
                    "GRANT lab7_user TO " + login + ";\n" +
                    "ALTER ROLE " + login + " SET password_encryption TO 'scram-sha-256';";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.execute();
            logger.info("Пользователь {} добавлен", login);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Не удалось создать аккаунт, возможно, ваш логин {} занят или содержит недопустимые символы", login);
            return false;
        }
    }

    /**
     * Проверяет есть ли юзер, с указанными логином и паролем в БД
     *
     * @param login    логин
     * @param password пароль
     * @return false, если нет
     */
    public boolean login(String login, String password) {
        USER = login;
        PASSWORD = password;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            Statement statement = connection.createStatement();
            statement.executeQuery("SELECT * FROM spacemarines");
            logger.info("Вход завершён");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Не удалось войти");
            return false;
        }
    }

    /**
     * Добавляет элемент в БД
     *
     * @param spaceMarine элемент
     * @return false, если не удалось
     */
    public boolean addToDataBase(SpaceMarine spaceMarine) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            logger.info("Пытаемся добавить объект в базу данных");
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO spacemarines (\"name\", \"Coordinates (X)\", \"Coordinates (Y)\", health , \"heartCount\", loyal, \"meleeWeapon\", \"Chapter (Name)\", \"Chapter (World)\", \"creationDate\", \"user\") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            preparedStatement.setString(1, spaceMarine.getName());
            preparedStatement.setInt(2, spaceMarine.getCoordinates().getX());
            preparedStatement.setLong(3, spaceMarine.getCoordinates().getY());
            preparedStatement.setFloat(4, spaceMarine.getHealth());
            preparedStatement.setLong(5, spaceMarine.getHeartCount());
            if (spaceMarine.getLoyal() != null)
                preparedStatement.setBoolean(6, spaceMarine.getLoyal());
            else preparedStatement.setNull(6, Types.NULL);
            preparedStatement.setString(7, spaceMarine.getMeleeWeapon().name());
            preparedStatement.setString(8, spaceMarine.getChapter().getName());
            preparedStatement.setString(9, spaceMarine.getChapter().getWorld());
            preparedStatement.setDate(10, spaceMarine.getCreationDate());
            preparedStatement.setString(11, USER);
            preparedStatement.executeUpdate();
            System.out.println(spaceMarine + " после");
            logger.info("Объект добавлен в БД");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Не удалось добавить объект в БД");
            return false;
        }
    }

    /**
     * Обновляет элемент коллекции
     *
     * @param spaceMarine элемент
     * @return false, если не удалось
     */
    public boolean updateElementInDataBase(SpaceMarine spaceMarine) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            logger.info("Пытаемся обновить объект в базе данных");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT MAX(id) FROM spacemarines");
            long maxId = 0;
            while (resultSet.next()) {
                maxId = resultSet.getLong("max");
            }
            if (addToDataBase(spaceMarine)) {
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE spacemarines SET id = " + spaceMarine.getId() + " WHERE id = (SELECT MAX(id) FROM spacemarines);");
                preparedStatement.executeUpdate();
                Connection newConnection = DriverManager.getConnection(URL, "postgres", "14881488");
                preparedStatement = newConnection.prepareStatement("ALTER SEQUENCE \"spacemarines_id_seq\" RESTART WITH " + (maxId + 1) + ";");
                preparedStatement.execute();
                newConnection.close();
                logger.info("Объект успешно обновлён");
                return true;
            } else logger.error("Не удалось обновить элемент, обновлённый элемент не может быть добавлен в БД");
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Не удалось обновить элемент");
        }
        return false;
    }

    /**
     * Удаляет элемент из БД
     *
     * @param spaceMarine элемент
     * @return false, если не удалось
     */
    public boolean removeFromDataBase(SpaceMarine spaceMarine) {
        logger.info("Пытаемся удалить объект {} из БД", spaceMarine);
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM spacemarines WHERE id = " + spaceMarine.getId());
            while (resultSet.next()) {
                if (!resultSet.getString("user").equals(USER)) {
                    logger.warn("Элемент не принадлежит пользователю {}, удаление не возможно", USER);
                    return false;
                }
            }
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM spacemarines WHERE id = " + spaceMarine.getId() + " AND \"user\" = '" + USER + "'");
            preparedStatement.executeUpdate();
            logger.info("Элемент был удалён");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Не удалось удалить объект");
            return false;
        }
    }

    /**
     * Обновляет коллекцию в памяти из БД
     *
     * @param priorityQueue коллекция в памяти
     */
    public void updateCollectionFromDataBase(PriorityQueue<SpaceMarine> priorityQueue) {
        logger.info("Пытаемся обновить коллекцию в памяти");
        PriorityQueue<SpaceMarine> newPriorityQueque = new PriorityQueue<>(CommandsManager.GetIdComparator());
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM spacemarines");
            Boolean loyality;
            while (resultSet.next()) {
                if (resultSet.getString("loyal") == null) loyality = null;
                else loyality = !resultSet.getString("loyal").equals("f");
                SpaceMarine spaceMarine = new SpaceMarine(resultSet.getString("name"), new Coordinates(resultSet.getInt("Coordinates (X)"), resultSet.getLong("Coordinates (Y)")), resultSet.getFloat("health"), resultSet.getLong("heartCount"), loyality, MeleeWeapon.valueOf(resultSet.getString("meleeWeapon")), new Chapter(resultSet.getString("Chapter (Name)"), resultSet.getString("Chapter (World)")));
                spaceMarine.setId(resultSet.getLong("id"));
                spaceMarine.setCreatedByUser(resultSet.getString("user"));
                spaceMarine.setCreationDate(LocalDate.parse(resultSet.getString("creationDate")));
                newPriorityQueque.add(spaceMarine);
            }
            priorityQueue.clear();
            priorityQueue.addAll(newPriorityQueque);
            logger.info("Коллекция обновлена");
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Не удалось обновить коллекцию в памяти");
        }
    }

    /**
     * @param USER логин
     */
    public void setUSER(String USER) {
        this.USER = USER;
    }

    /**
     * @param PASSWORD пароль
     */
    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }
}