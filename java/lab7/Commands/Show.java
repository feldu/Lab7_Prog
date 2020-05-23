package lab7.Commands;

import lab7.Collections.SpaceMarine;
import lab7.DataBaseManager;

import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * Класс команды show
 *
 * @author Остряков Егор, P3112
 */
public class Show extends AbstractCommand {
    public Show() {
        name = "show";
        help = "показывает элементы коллекции";
    }

    /**
     * Показывает элементы коллекции
     *
     * @param priorityQueue   коллекция, которую нужно показать
     * @param commandsManager объект класса CommandsManager
     * @param dataBaseManager объект класса DataBaseManager
     */
    @Override
    public void execute(PriorityQueue<SpaceMarine> priorityQueue, CommandsManager commandsManager, DataBaseManager dataBaseManager) {
        if (args.length > 0) {
            commandsManager.printToClient("Команда не принимает аргументы");
            logger.warn("Команда не принимает аргументы");
        } else {
            commandsManager.getLock().lock();
            try {
                commandsManager.printToClient(Arrays.toString(priorityQueue.toArray()));
            } finally {
                commandsManager.getLock().unlock();
            }
        }
    }
}
