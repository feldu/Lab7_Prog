package lab7.Exceptions;

/**
 * Класс исключения неверного количества аргументов команды
 *
 * @author Остряков Егор, P3112
 */
public class InvalidCountOfArgumentsException extends Exception {

    /**
     * Конструктор
     *
     * @param msg показываемое сообщение
     */
    public InvalidCountOfArgumentsException(String msg) {
        super(msg);
    }

}
