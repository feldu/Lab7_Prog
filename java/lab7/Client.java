package lab7;

import lab7.Commands.AbstractCommand;
import lab7.Commands.CommandsManager;
import lab7.Commands.Login;
import lab7.Commands.Register;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Класс клиента Client
 *
 * @author Остряков Егор, P3112
 */
public class Client implements Runnable {
    private DatagramChannel datagramChannel;
    private SocketAddress socketAddress;
    private Selector selector;
    private String login = "";
    private String password = "";
    private boolean registered = false;

    /**
     * Конструктор, сразу же открывает selector  datagramChannel
     *
     * @throws IOException IOException
     */
    public Client() throws IOException {
        selector = Selector.open();
        datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);
    }

    /**
     * Ну это main()...
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        try {
            Client client = new Client();
            client.connect("localhost", 1488);
            while (true)
                client.run();

        } catch (IOException e) {
            System.err.println("Произошла ошибка ввода/вывода");
        }
    }

    /**
     * Пытаетеся присоединиться к серверу
     *
     * @param hostname имя сервера
     * @param port     порт
     * @throws IOException IOException
     */
    private void connect(String hostname, int port) throws IOException {
        socketAddress = new InetSocketAddress(hostname, port);
        datagramChannel.connect(socketAddress);
        System.out.println("Устанавливаем соединение с " + hostname + " по порту " + port);

    }

    /**
     * Получает ответ от сервера
     *
     * @return полученное сообщение
     * @throws IOException IOException
     */
    private String receiveAnswer() throws IOException {
        byte[] bytes = new byte[1000000];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        socketAddress = datagramChannel.receive(buffer);
        return new String(buffer.array()).split("�")[0].trim();
    }

    /**
     * Отправляет команду серверу
     *
     * @param command передаваемая команда
     * @throws IOException IOException
     */
    private void sendCommand(AbstractCommand command) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(Serialization.SerializeObject(command, login, password));
        datagramChannel.send(buffer, socketAddress);
        if (command != null)
            if (command.getClass().getName().contains("Exit")) System.exit(0);
    }

    /**
     * Авторизация
     *
     * @param scanner сканер для ввода данных пользователем
     */
    private void authorization(Scanner scanner) {
        try {
            String input;
            do {
                System.out.println("Если вы уже зарегестрированны, введите login для входа, иначе введите register");
                input = scanner.nextLine().trim().split("\\s+")[0];
            } while (!input.equals("register") & !input.equals("login"));
            if (input.equals("register")) register(scanner);
            else login(scanner);
            String answer = "";
            while (answer.isEmpty()) {
                answer = receiveAnswer();
                if (answer.equals("Пользователь успешно вошёл в систему."))
                    registered = true;
            }
            System.out.println(answer);
        } catch (Exception e) {
            System.err.println("Ошибка авторизации");
        }
    }

    /**
     * Регаемся
     *
     * @param scanner сканер для ввода данных пользователем
     * @throws IOException
     */
    private void register(Scanner scanner) throws IOException {
        login = "";
        boolean lessThen4 = true;
        boolean withSpaces = true;
        boolean invalidChars = true;
        do {
            System.out.println("Придумайте логин, содержащий не менее 4 символов (допускается использование только английских прописных букв и цифр) ");
            login = scanner.nextLine();
            lessThen4 = login.trim().split("\\s+")[0].length() < 4;
            withSpaces = login.trim().split("\\s+").length != 1;
            invalidChars = !login.trim().split("\\s+")[0].matches("[a-z0-9]+");
        } while (lessThen4 || withSpaces || invalidChars);
        password = "";
        lessThen4 = true;
        withSpaces = true;
        invalidChars = true;
        do {
            System.out.println("Придумайте пароль, содержащий не менее 4 (допускается использование только английских прописных букв и цифр)");
            password = scanner.nextLine();
            lessThen4 = password.trim().split("\\s+")[0].length() < 4;
            withSpaces = password.trim().split("\\s+").length != 1;
            invalidChars = !password.trim().split("\\s+")[0].matches("[a-z0-9]+");
        } while (lessThen4 || withSpaces || invalidChars);
        System.out.println("Ваш логин: " + login.trim().split("\\s+")[0] + "\nВаш пароль: " + password.trim().split("\\s+")[0]);
        sendCommand(new Register());
    }

    /**
     * Логинимся
     *
     * @param scanner сканер для ввода данных пользователем
     * @throws IOException
     */
    private void login(Scanner scanner) throws IOException {
        System.out.print("Введите логин: ");
        login = scanner.nextLine();
        System.out.print("Введите пароль: ");
        password = scanner.nextLine();
        sendCommand(new Login());
    }

    /**
     * Ну это run()...
     */
    @Override
    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            datagramChannel.register(selector, SelectionKey.OP_WRITE);
            System.out.println("Для работы с коллекцией зарегистрируйтесь (register) или авторизуйтесь (login)");
            while (!registered)
                authorization(scanner);
            while (selector.select() > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                for (Iterator<SelectionKey> it = iterator; it.hasNext(); ) {
                    SelectionKey selectionKey = it.next();
                    iterator.remove();
                    if (selectionKey.isReadable()) {
//                        System.out.println("Readable");
                        String answer = receiveAnswer();
                        if (answer.contains("I am fucking seriously, it's fucking EOF!!!"))
                            datagramChannel.register(selector, SelectionKey.OP_WRITE);
                        else System.out.println(answer);
                    }
                    if (selectionKey.isWritable()) {
                        datagramChannel.register(selector, SelectionKey.OP_READ);
//                        System.out.println("Writable");
                        AbstractCommand command = CommandsManager.CommandDeterminator(scanner.nextLine().trim().split("\\s+"));
                        if (command != null && command.isNeedObjectToExecute()) {
                            command.setSpaceMarine(CommandsManager.GetSpaceMarine());
                        }
                        sendCommand(command);

                    }
                }
            }
        } catch (PortUnreachableException e) {
            System.err.println("Не удалось получить данные по указанному порту/сервер не доступен");
        } catch (IOException e) {
            System.err.println("Ошибка ввода/вывода");
        } catch (Exception e) {
            System.err.println("Произошла непредусмотренная ошибка");
        }
    }
}
