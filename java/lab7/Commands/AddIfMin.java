package lab7.Commands;

import lab7.Collections.SpaceMarine;
import lab7.DataBaseManager;
import lab7.InputHandler;
import lab7.Exceptions.InvalidCountOfArgumentsException;

import java.util.PriorityQueue;

/**
 * Класс команды add_if_min
 *
 * @author Остряков Егор, P3112
 */
public class AddIfMin extends AbstractCommand {
    public AddIfMin() {
        name = "add_if_min";
        help = "добавляет новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции";
        needObjectToExecute = true;
        args = new String[0];
    }

    /**
     * Добавляет новый элемент в коллекцию, если значение его здоровья минимально
     *  @param priorityQueue   коллекция, в которую нужно добавить элемент
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
                PriorityQueue<SpaceMarine> priorityQueueWithMin = new PriorityQueue<>(CommandsManager.GetIdComparator());
                priorityQueueWithMin.addAll(priorityQueue);
                if (commandsManager.isScript()) priorityQueueWithMin.add(InputHandler.ArgumentsReader(commandsManager));
                else priorityQueueWithMin.add(spaceMarine);
                SpaceMarine minMarine = priorityQueueWithMin.stream().min(CommandsManager.GetIdComparator()).get();
                if (priorityQueue.peek().getHealth() <= minMarine.getHealth()) {
                    logger.warn("Элемент не добавлен, так как не является минимальным");
                    commandsManager.printToClient("Элемент не добавлен, так как не является минимальным (значение Health >= " + priorityQueue.peek().getHealth() + ")");
                } else if (dataBaseManager.addToDataBase(minMarine)){
                    commandsManager.printToClient("Элемент добавлен в коллекцию");
                    logger.info("Элемент добавлен в коллекцию");
                    logger.info("Обновляем колекцию в памяти, так как таблица в БД была изменена");
                    dataBaseManager.updateCollectionFromDataBase(priorityQueue);
                }
            } catch (NullPointerException ignored) {
            } catch (Exception e) {
                commandsManager.printToClient("Неверный тип аргумента");
                logger.error("Неверный тип аргумента: {}", e.getMessage());
                if (commandsManager.isScript()) {
                    commandsManager.commandRewider();
                }
            }
            finally {
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