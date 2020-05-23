package lab7.Commands;

import lab7.Collections.SpaceMarine;
import lab7.DataBaseManager;
import lab7.Server;

import java.util.PriorityQueue;

/**
 * Класс команды info
 *
 * @author Остряков Егор, P3112
 */
public class Info extends AbstractCommand {
    public Info() {
        name = "info";
        help = "выводит в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)";
    }

    /**
     * Выводит инормацию о коллекции
     *
     * @param priorityQueue   коллекция, с которой работает пользователь
     * @param commandsManager объект класса CommandsManager
     * @param dataBaseManager объект класса DataBaseManager
     */
    @Override
    public void execute(PriorityQueue<SpaceMarine> priorityQueue, CommandsManager commandsManager, DataBaseManager dataBaseManager) {
        if (args.length > 0) {
            commandsManager.printToClient("Команда не принимает аргументы");
            logger.warn("Команда не принимает аргументы");
        } else {
            commandsManager.printToClient("Тип коллекции: " + priorityQueue.getClass().getName());
            commandsManager.printToClient("Время создания коллекции: " + Server.GetCreationDate());
            commandsManager.printToClient("Количество элементов в коллеции: " + priorityQueue.size());
        }
    }
}
