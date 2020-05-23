package lab7.Commands;

import lab7.Collections.SpaceMarine;
import lab7.DataBaseManager;
import lab7.InputHandler;
import lab7.Exceptions.InvalidCountOfArgumentsException;
import lab7.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Класс управляющий выборкой команд
 *
 * @author Остряков Егор, P3112
 */

public class CommandsManager implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(Server.class.getName());
    private static HashMap<String, AbstractCommand> commands = new HashMap<>();
    private static CommandsManager commandsManager = new CommandsManager();
    private DatagramChannel serverDatagramChannel;
    private SocketAddress socketAddress;
    private String scriptFileName;
    private boolean isScript = false;
    private BufferedReader scriptBufferedReader;
    ReentrantLock lock = new ReentrantLock();

    /**
     * Конструктор при вызове которого в HashSet commands будут добавлены все доступные команды
     */
    private CommandsManager() {
        commands.put("add",new Add());
        commands.put("add_if_min",new AddIfMin());
        commands.put("clear", new Clear());
        commands.put("execute_script", new ExecuteScript());
        commands.put("exit", new Exit());
        commands.put("help", new Help());
        commands.put("info", new Info());
        commands.put("max_by_health", new MaxByHealth());
        commands.put("print_unique_health", new PrintUniqueHealth());
        commands.put("remove_any_by_loyal", new RemoveAnyByLoyal());
        commands.put("remove_by_id", new RemoveById());
        commands.put("remove_first", new RemoveFirst());
        commands.put("remove_grater", new RemoveGreater());
        commands.put("show", new Show());
        commands.put("update", new Update());
    }

    /**
     * Запускает выполнение команды
     *
     * @param command         команда, выполненеие которой нужно запустить
     * @param priorityQueue   коллекция, с которой команда взаимодействует
     * @param datagramChannel канал для передачи сообщений клиенту
     * @param socketAddress   адрес порта
     */
    public static void executeCommand(AbstractCommand command, PriorityQueue<SpaceMarine> priorityQueue, DatagramChannel datagramChannel, SocketAddress socketAddress, DataBaseManager dataBaseManager) throws IOException {
        commandsManager.setServerDatagramChannel(datagramChannel);
        commandsManager.setSocketAddress(socketAddress);
        logger.info("Выполнение команды");
        command.execute(priorityQueue, commandsManager, dataBaseManager);
        if (!commandsManager.isScript) {
            logger.info("Отправляем клиенту сообщение о завершении чтения");
            datagramChannel.send(ByteBuffer.wrap("I am fucking seriously, it's fucking EOF!!!".getBytes()), socketAddress);
        }
    }

    /**
     * Определяет, какую команду ввёл пользователь
     *
     * @param args команда и её аргументы в виде массива строк
     * @return введённую команду или null, если такой каманды нет
     */
    public static AbstractCommand CommandDeterminator(String[] args) {
        try {
            String cmd = args[0].trim();
            args = Arrays.copyOfRange(args, 1, args.length);
            if (cmd.trim().equals("login") || cmd.trim().equals("register")) {
                System.out.println("Вы уже вошли, для повторного входа или регистрации закончите сеанс с помощью команды exit");
                return null;
            }
            AbstractCommand command = commands.get(cmd.trim());
            command.setArgs(args);
            return command;
        } catch (InvalidCountOfArgumentsException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    /**
     * Организует вывод текстового сообщения клиенту
     *
     * @param line строка отслыемая клиенту
     */
    public void printToClient(String line) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap((line.getBytes()));
            commandsManager.getServerDatagramChannel().send(buffer, commandsManager.getSocketAddress());
            logger.info("Отправляем ответ клиенту: {} ", new String(buffer.array()));
        } catch (IOException e) {
            logger.info("Не удалось отправить ответ клиенту {}", e.getMessage());
        }
    }

    /**
     * Возвращает объект класса SpaceMarine, сформированный из пользовательского ввода
     *
     * @return spaceMarine
     */
    public static SpaceMarine GetSpaceMarine() {
        return InputHandler.ArgumentsReader(commandsManager);
    }


    /**
     * Проматывает строки в случае ошибки при считывании команды из скрипта
     */
    public void commandRewider() {
        try {
            for (int i = 1; i < 10; i++) scriptBufferedReader.readLine();
        } catch (Exception ignored) {
        }
    }

    /**
     * @return HashSet с командами
     */
    public HashMap<String, AbstractCommand> getCommands() {
        return commands;
    }

    /**
     * @param script работает в данный момент пользователь со скриптом или нет
     */
    public void setScript(boolean script) {
        isScript = script;
    }

    /**
     * @return работает в данный момент пользователь со скриптом или нет
     */
    public boolean isScript() {
        return isScript;
    }

    /**
     * @return имя скрипта
     */
    public String getScriptFileName() {
        return scriptFileName;
    }

    /**
     * @param scriptFileName имя скрипта
     */
    public void setScriptFileName(String scriptFileName) {
        this.scriptFileName = scriptFileName;
    }

    /**
     * @return считыватель скрипта
     */
    public BufferedReader getScriptBufferedReader() {
        return scriptBufferedReader;
    }

    /**
     * @param scriptBufferedReader считыватель скрипта
     */
    public void setScriptBufferedReader(BufferedReader scriptBufferedReader) {
        this.scriptBufferedReader = scriptBufferedReader;
    }

    /**
     * Переопределение интерфейса Comparator для сравнения элементов коллекции по полю Health
     */
    private static Comparator<SpaceMarine> idComparator = (o1, o2) -> (int) (o1.getHealth() - o2.getHealth());

    /**
     * @return компаратор для сравнения элементов коллекции по полю Health
     */
    public static Comparator<SpaceMarine> GetIdComparator() {
        return idComparator;
    }

    /**
     * @param datagramChannel datagramChannel сервера
     */
    public void setServerDatagramChannel(DatagramChannel datagramChannel) {
        serverDatagramChannel = datagramChannel;
    }

    /**
     * @return datagramChannel сервера
     */
    public DatagramChannel getServerDatagramChannel() {
        return serverDatagramChannel;
    }

    /**
     * @param socketAddress socketAddress сервера
     */
    public void setSocketAddress(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    /**
     * @return socketAddress сервера
     */
    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    public ReentrantLock getLock() {
        return lock;
    }
}
