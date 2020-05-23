package lab7.Commands;

import lab7.Collections.SpaceMarine;
import lab7.DataBaseManager;

import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Класс команды remove_any_by_loyal
 *
 * @author Остряков Егор, P3112
 */
public class RemoveAnyByLoyal extends AbstractCommand {
    public RemoveAnyByLoyal() {
        name = "remove_any_by_loyal";
        help = "!!! удаляет из коллекции один элемент, ПРИНАДЛЕЖАЩИЙ ВАМ и значение поля loyal которого эквивалентно заданному";
    }

    /**
     * Удаляет из коллекции один элемент с указанным значением лояльности
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
                if (priorityQueue.size() > 0) {
                    Boolean loyal = null;
                    if (args[0].equalsIgnoreCase("true")) loyal = true;
                    else if (args[0].equalsIgnoreCase("false")) loyal = false;
                    else if (args[0].equals("null")) loyal = null;
                    else {
                        commandsManager.printToClient("Неверный аргумент");
                        return;
                    }
                    AtomicBoolean breakme = new AtomicBoolean(false);
                    Boolean finalLoyal = loyal;
                    if (!priorityQueue.removeIf(spaceMarine -> {
                        if (spaceMarine.getLoyal() == finalLoyal && !breakme.get()) {
                            if (dataBaseManager.removeFromDataBase(spaceMarine)) {
                                breakme.set(true);
                                commandsManager.printToClient("Элемент с id = " + spaceMarine.getId() + " удалён");
                                logger.info("Обновляем колекцию в памяти, так как таблица в БД была изменена");
                                dataBaseManager.updateCollectionFromDataBase(priorityQueue);
                                return true;
                            }
                        }
                        return false;
                    })) {
                        commandsManager.printToClient("Элемент с loyal = " + args[0] + " не найден");
                    }
                } else commandsManager.printToClient("Список пуст");
            } catch (Exception e) {
                commandsManager.printToClient("Неверный тип аргумента");
            } finally {
                commandsManager.getLock().unlock();
            }
        }
    }
}
