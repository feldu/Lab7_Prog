package lab7;

import lab7.Collections.SpaceMarine;
import lab7.Commands.AbstractCommand;
import lab7.Commands.CommandsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.PriorityQueue;

public class MessagesHandler extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(Server.class.getName());
    private DatagramChannel datagramChannel;
    private SocketAddress socketAddress;
    private DataBaseManager dataBaseManager;
    private PriorityQueue<SpaceMarine> priorityQueue;
    private ByteBuffer byteBuffer;

    /**
     * @param datagramChannel datagramChannel сервера
     * @param socketAddress   socketAddress сервера
     * @param dataBaseManager dataBaseManager
     * @param priorityQueue   коллекция в памяти
     * @param byteBuffer      пришедший запрос
     */
    public MessagesHandler(DatagramChannel datagramChannel, SocketAddress socketAddress, DataBaseManager dataBaseManager, PriorityQueue<SpaceMarine> priorityQueue, ByteBuffer byteBuffer) {
        this.datagramChannel = datagramChannel;
        this.socketAddress = socketAddress;
        this.dataBaseManager = dataBaseManager;
        this.priorityQueue = priorityQueue;
        this.byteBuffer = byteBuffer;
    }

    /**
     * Метод run чтения-отправки запросов и ответов
     */
    @Override
    public void run() {
        String login;
        String password;
        Object o;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteBuffer.array());
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            login = objectInputStream.readUTF();
            password = objectInputStream.readUTF();
            o = objectInputStream.readObject();
            logger.info("Получен запрос от {}", login);
            logger.info("Его пароль получен");
            if (o == null) {
                logger.info("Получена несуществующая команда");
                datagramChannel.send(ByteBuffer.wrap("Команда не найдена или имеент неверное количество аргументов. Для просмотра доступных команд введите help".getBytes()), socketAddress);
                datagramChannel.send(ByteBuffer.wrap("I am fucking seriously, it's fucking EOF!!!".getBytes()), socketAddress);
            } else {
                if (o.getClass().getName().contains(".Login")) authorization("login", login, password);
                else if (o.getClass().getName().contains(".Register")) authorization("register", login, password);
                else if (!o.getClass().getName().contains(".SpaceMarine")) {
                    AbstractCommand command = (AbstractCommand) o;
                    logger.info("Сервер получил команду: " + command.getName());
                    logger.info("Передаём логин и пароль пользователя DataBaseManager'у для идентификации");
                    dataBaseManager.setUSER(login);
                    dataBaseManager.setPASSWORD(password);
                    CommandsManager.executeCommand(command, priorityQueue, datagramChannel, socketAddress, dataBaseManager);
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            logger.error("Не удалось десериализовать объект");
            e.printStackTrace();
        }
    }

    /**
     * Авторизация пользователя
     *
     * @param message  вид авторизации
     * @param login    логин
     * @param password пароль
     * @throws IOException исключение
     */
    private void authorization(String message, String login, String password) throws IOException {
        if (message.equals("login")) {
            logger.info("Команда входа в систему определена");
            if (dataBaseManager.login(login, password)) {
                datagramChannel.send(ByteBuffer.wrap("Пользователь успешно вошёл в систему.".getBytes()), socketAddress);

            } else datagramChannel.send(ByteBuffer.wrap("Не удалось войти в систему.".getBytes()), socketAddress);
        }
        if (message.equals("register")) {
            logger.info("Команда регистрации в системе определена");

            if (dataBaseManager.addUser(login, password))
                datagramChannel.send(ByteBuffer.wrap("Пользователь добавлен. Войдите в систему.".getBytes()), socketAddress);
            else
                datagramChannel.send(ByteBuffer.wrap("Не удалось добавить пользователя. Логин занят, содержит недопустимые символы или их последовательность. Мне наплевать, если честно, сами гуглите, какой логин и пароль захавает чёртов postgresql".getBytes()), socketAddress);
        }
    }
}
