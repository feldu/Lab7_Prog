package lab7.Commands;

import lab7.Collections.SpaceMarine;
import lab7.DataBaseManager;

import java.util.PriorityQueue;

/**
 * Класс команды exit
 *
 * @author Остряков Егор, P3112
 */
public class Exit extends AbstractCommand {
    public Exit() {
        name = "exit";
        help = "завершает программу (без сохранения в файл)";
    }

    /**
     * Завершает работу с коллекций, выходит без сохранения
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
            commandsManager.getLock().lock();
            try {
                logger.info("Клиент завершил работу с коллекцией");
            } finally {
                commandsManager.getLock().unlock();
            }
        }
    }
}
