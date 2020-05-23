package lab7.Commands;

import lab7.Collections.*;
import lab7.DataBaseManager;
import lab7.Exceptions.InvalidCountOfArgumentsException;
import lab7.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.PriorityQueue;

/**
 * Абстрактный класс, от которого наследуются все команды
 *
 * @author Остряков Егор, P3112
 */
public abstract class AbstractCommand implements Serializable {
    protected static final Logger logger = LoggerFactory.getLogger(Server.class.getName());
    protected String name;
    protected String help;
    protected String[] args;
    protected boolean needObjectToExecute = false;
    protected SpaceMarine spaceMarine = null;

    /**
     * Метод выполнения команды
     *  @param priorityQueue   коллекция, с которой работает пользователь
     * @param commandsManager объект класса CommandsManager
     * @param dataBaseManager объект класса DataBaseManager
     */
    public abstract void execute(PriorityQueue<SpaceMarine> priorityQueue, CommandsManager commandsManager, DataBaseManager dataBaseManager);

    /**
     * @return имя команды
     */
    public String getName() {
        return name;
    }

    /**
     * @return описание работы команды
     */
    public String getHelp() {
        return help;
    }

    /**
     *
     * @return аргументы команды
     */
    public String[] getArgs() {
        return args;
    }

    /**
     *
     * @param args аргументы команды
     * @throws InvalidCountOfArgumentsException ошибка неверного количества аргументов
     */
    public void setArgs(String[] args) throws InvalidCountOfArgumentsException {
        this.args = args;
    }

    /**
     *
     * @return нужен ли команде объект spaceMarine для выполнения
     */
    public boolean isNeedObjectToExecute() {
        return needObjectToExecute;
    }

    /**
     *
     * @param spaceMarine объект spaceMarine для команды
     */
    public void setSpaceMarine(SpaceMarine spaceMarine) {
        this.spaceMarine = spaceMarine;
    }

}
