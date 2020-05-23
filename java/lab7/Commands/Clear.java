package lab7.Commands;

import lab7.Collections.SpaceMarine;
import lab7.DataBaseManager;

import java.util.PriorityQueue;

/**
 * Класс команды clean
 *
 * @author Остряков Егор, P3112
 */
public class Clear extends AbstractCommand {
    public Clear() {
        name = "clear";
        help = "!!! удаляет все элементы коллекции, принадлежащие Вам";
    }

    /**
     * Удаляет все элементы коллекции
     *  @param priorityQueue   коллекция, которую нужно очистить
     * @param commandsManager объект класса CommandsManager
     * @param dataBaseManager объект класса DataBaseManager
     */
    @Override
    public void execute(PriorityQueue<SpaceMarine> priorityQueue, CommandsManager commandsManager, DataBaseManager dataBaseManager) {
        if (args.length > 0) {
            commandsManager.printToClient("Команда не принимает аргументы");
            logger.warn("Команда не принимает аргументы");
        }else {
            commandsManager.getLock().lock();
            try {
                priorityQueue.parallelStream().forEachOrdered(dataBaseManager::removeFromDataBase);
                commandsManager.printToClient("Все элементы принадлежащие вам удалены (наверное)");
                logger.info("Обновляем колекцию в памяти, так как таблица в БД была изменена");
                dataBaseManager.updateCollectionFromDataBase(priorityQueue);
            }finally {
                commandsManager.getLock().unlock();
            }
        }
    }

}
