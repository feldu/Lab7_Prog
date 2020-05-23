package lab7.Commands;

import lab7.Collections.SpaceMarine;
import lab7.DataBaseManager;
import lab7.Exceptions.InvalidCountOfArgumentsException;

import java.util.PriorityQueue;

/**
 * Класс команды remove_first
 *
 * @author Остряков Егор, P3112
 */
public class RemoveFirst extends AbstractCommand {

    public RemoveFirst() {
        name = "remove_first";
        help = "удаляет первый элемент из коллекции";
    }

    /**
     * Удаляет первый элемент коллекции
     *
     * @param priorityQueue   коллекция, из которой удаляется элемент
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
                if (dataBaseManager.removeFromDataBase(priorityQueue.element())) {
                    commandsManager.printToClient("Элемент с id = " + priorityQueue.poll().getId() + " удалён");
                    logger.info("Обновляем колекцию в памяти, так как таблица в БД была изменена");
                    dataBaseManager.updateCollectionFromDataBase(priorityQueue);
                }
                else commandsManager.printToClient("Не удалось удалить элемент, возможно, у вас нет прав");
            } catch (Exception e) {
                commandsManager.printToClient("Список пуст");
            } finally {
                commandsManager.getLock().unlock();
            }
        }
    }

    @Override
    public void setArgs(String[] args) throws InvalidCountOfArgumentsException {
        if (args.length == 0) this.args = args;
        else throw new InvalidCountOfArgumentsException("На данном этапе команда не принимает аргументы");

    }
}
