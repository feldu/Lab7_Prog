package lab7.Commands;

import lab7.Collections.SpaceMarine;
import lab7.DataBaseManager;
import lab7.InputHandler;
import lab7.Exceptions.InvalidCountOfArgumentsException;

import java.util.PriorityQueue;

/**
 * Класс команды remove_greater
 *
 * @author Остряков Егор, P3112
 */
public class RemoveGreater extends AbstractCommand {
    public RemoveGreater() {
        name = "remove_greater";
        help = "удаляет из коллекции все элементы, превышающие заданный";
        needObjectToExecute = true;
        args = new String[0];
    }

    /**
     * Удаляет из коллекции элементы, здоровье которых больше указанного
     *
     * @param priorityQueue   коллекция, из которой удаляются элементы
     * @param commandsManager объект класса CommandsManager
     * @param dataBaseManager объект класса DataBaseManager
     */
    @Override
    public void execute(PriorityQueue<SpaceMarine> priorityQueue, CommandsManager commandsManager, DataBaseManager dataBaseManager) {
        if (args.length > 0) {
            commandsManager.printToClient("На данном этапе команда не принимает аргументы");
            logger.warn("На данном этапе команда не принимает аргументы");
            commandsManager.commandRewider();
        } else {
            commandsManager.getLock().lock();
            try {
                if (priorityQueue.size() > 0) {
                    PriorityQueue<SpaceMarine> priorityQueueWithComp = new PriorityQueue<>(CommandsManager.GetIdComparator());
                    priorityQueueWithComp.addAll(priorityQueue);
                    if (commandsManager.isScript())
                        priorityQueueWithComp.add(InputHandler.ArgumentsReader(commandsManager));
                    else priorityQueueWithComp.add(spaceMarine);
                    if (priorityQueue.removeIf(spaceMarine -> {
                        if (spaceMarine.getHealth() > priorityQueueWithComp.stream().max((o1, o2) -> (int) (o1.getId() - o2.getId())).get().getHealth())
                            return (dataBaseManager.removeFromDataBase(spaceMarine));
                        return false;
                    })) {
                        commandsManager.printToClient("Все элементы, которые доступны вам для модификации и превышающие заданный удалены из коллекции");
                        logger.info("Обновляем колекцию в памяти, так как таблица в БД была изменена");
                        dataBaseManager.updateCollectionFromDataBase(priorityQueue);
                    } else
                        commandsManager.printToClient("Элементов (к которым вы имеете доступ) превышающих заданный нет");
                } else commandsManager.printToClient("Список пуст");
                SpaceMarine.idSetter--;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                commandsManager.getLock().unlock();
            }
        }
    }

    /**
     * @param args аргументы команды
     * @throws InvalidCountOfArgumentsException если ввели количество аргументов не равное 0
     */
    @Override
    public void setArgs(String[] args) throws InvalidCountOfArgumentsException {
        if (args.length == 0) this.args = args;
        else throw new InvalidCountOfArgumentsException("На данном этапе команда не принимает аргументы");
    }
}
