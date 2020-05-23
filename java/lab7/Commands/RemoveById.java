package lab7.Commands;

import lab7.Collections.SpaceMarine;
import lab7.DataBaseManager;

import java.util.PriorityQueue;

/**
 * Класс команды remove_by_id
 *
 * @author Остряков Егор, P3112
 */
public class RemoveById extends AbstractCommand {
    public RemoveById() {
        name = "remove_by_id";
        help = "удаляет элемент из коллекции по его id";
    }

    /**
     * Удаляет элемент по id
     *
     * @param priorityQueue   коллекция, из которой удаляется элемент
     * @param commandsManager объект класса CommandsManager
     * @param dataBaseManager объект класса DataBaseManager
     */
    @Override
    public void execute(PriorityQueue<SpaceMarine> priorityQueue, CommandsManager commandsManager, DataBaseManager dataBaseManager) {
        if (args.length != 1) {
            commandsManager.printToClient("Команда принимает лишь один аргумент");
            logger.warn("Команда принимает лишь один аргумент");
        } else {
            commandsManager.getLock().lock();
            try {
                long id = Long.parseLong(args[0]);
                if (priorityQueue.removeIf(spaceMarine -> {
                    if (spaceMarine.getId() == id) {
                        return (dataBaseManager.removeFromDataBase(spaceMarine));
                    }
                    return false;
                })) {
                    commandsManager.printToClient("Элемент коллекции с id = " + args[0] + " удалён");
                    logger.info("Обновляем колекцию в памяти, так как таблица в БД была изменена");
                    dataBaseManager.updateCollectionFromDataBase(priorityQueue);
                }
                else
                    commandsManager.printToClient("Элемент коллекции с id = " + args[0] + " не найден или у вас нет прав для его удаления");
            } catch (Exception e) {
                commandsManager.printToClient("Неверный тип аргумента");
            } finally {
                commandsManager.getLock().unlock();
            }
        }
    }
}
