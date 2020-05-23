package lab7;

import java.io.*;
import java.util.Arrays;

/**
 * Класс, проводящий сериализацию и десериализацию объектов
 *
 * @author Остряков Егор, P3112
 */
public class Serialization {
    /**
     * Сериализует объект
     * @param object объект
     * @param <T> тип объекта
     * @return массив байтов
     */
    public static <T> byte[] SerializeObject(T object, String login, String password) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeUTF(login);
            objectOutputStream.writeUTF(password);
            objectOutputStream.writeObject(object);

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            System.out.println("Ошибка сериализации");
            e.printStackTrace();
        }
        return null;
    }

}
