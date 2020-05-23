package lab7.Commands;

import lab7.Collections.SpaceMarine;
import lab7.DataBaseManager;
import lab7.Exceptions.InvalidCountOfArgumentsException;

import java.util.PriorityQueue;

/**
 * Класс команды update
 */
public class Update extends AbstractCommand {
    public Update() {
        name = "update";
        help = "обновляет значение элемента коллекции, id которого равен заданному";
        needObjectToExecute = true;
    }

    /**
     * Обновляет элемент коллекции по id
     *
     * @param priorityQueue   коллекция, элемент которой нужно обновить
     * @param commandsManager объект класса CommandsManager
     * @param dataBaseManager объект класса DataBaseManager
     */
    @Override
    public void execute(PriorityQueue<SpaceMarine> priorityQueue, CommandsManager commandsManager, DataBaseManager dataBaseManager) {
        commandsManager.getLock().lock();
        try {
            if (args.length != 1) {
                commandsManager.printToClient("Команда принимает лишь один аргумент");
                logger.warn("Команда принимает лишь один аргумент");
                if (commandsManager.isScript()) {
                    commandsManager.commandRewider();
                }
            } else {
                long oldIdSetter = SpaceMarine.idSetter;
                SpaceMarine.idSetter = Long.parseLong(args[0]);
                System.out.println("OLD: " + oldIdSetter);
                System.out.println("ARGS" + args[0]);
                if (priorityQueue.removeIf(spaceMarine -> {
                    if (spaceMarine.getId() == Integer.parseInt(args[0]))
                        return (dataBaseManager.removeFromDataBase(spaceMarine));
                    return false;
                })) {
                    spaceMarine.setId(SpaceMarine.idSetter);
                    if (dataBaseManager.updateElementInDataBase(spaceMarine))
                        commandsManager.printToClient("Элемент с id = " + (SpaceMarine.idSetter) + " обновлён");
                } else {
                    commandsManager.printToClient("Элемент с id = " + (SpaceMarine.idSetter) + " не существует или вы не имеете прав на его модификацию");
                    logger.info("Обновляем колекцию в памяти, так как таблица в БД была изменена");
                    dataBaseManager.updateCollectionFromDataBase(priorityQueue);
                }
                SpaceMarine.idSetter = oldIdSetter;
            }
        } catch (NullPointerException ignored) {
        } catch (Exception e) {
            commandsManager.printToClient("Неверный тип аргумента");
            if (commandsManager.isScript()) {
                commandsManager.commandRewider();
            }
        } finally {
            commandsManager.getLock().unlock();
        }
    }

    /**
     * @param args аргументы команды
     * @throws InvalidCountOfArgumentsException если ввели количество аргументов не равное 1
     */
    @Override
    public void setArgs(String[] args) throws InvalidCountOfArgumentsException {
        if (args.length == 1) this.args = args;
        else throw new InvalidCountOfArgumentsException("Команда принимает лишь один аргумент");
    }
}
