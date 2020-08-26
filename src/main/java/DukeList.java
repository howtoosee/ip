import java.util.ArrayList;
import java.lang.StringBuilder;
import java.util.Arrays;


public class DukeList {

    final ArrayList<Task> list;
    private static final int CAPACITY = 100;
    
    private final Storage store = new Storage("data", "duke.txt");


    public DukeList() {
        this.list = new ArrayList<>(CAPACITY);
    }


    /**
     * Removes the type keyword at the start of the string.
     *
     * @param strArr Array of strings (originally split by spaces).
     * @return Substring with the keyword removed.
     * @throws DukeInvalidDescriptionException Invalid description text.
     */
    private static String getItemSubstring(String[] strArr) throws DukeInvalidDescriptionException {
        if (strArr.length <= 1) {
            throw new DukeNoDescriptionException("String array of unexpected length: expected length > 1");
        } else {
            return String.join(" ", Arrays.copyOfRange(strArr, 1, strArr.length));
        }
    }


    /**
     * Helper function for adding item.
     *
     * @param itemString String to be added.
     * @return Task added.
     * @throws DukeException Duke exception.
     */
    private Task addHelper(String itemString) throws DukeException {
        Task newTask;

        String[] strArr = itemString.split(" ");
        String keyword = strArr[0];

        String formattedItemString;

        switch (keyword) {
        case ("todo"):
            try {
                formattedItemString = DukeList.getItemSubstring(strArr);
                newTask = new Todo(formattedItemString);
                break;
            } catch (DukeInvalidDescriptionException e) {
                throw new DukeInvalidDescriptionException(String.format("OOPS!!! The description of a `%s` cannot be empty.", keyword));
            }

        case ("deadline"):
            try {
                formattedItemString = DukeList.getItemSubstring(strArr);
                newTask = new Deadline(formattedItemString);
                break;
            } catch (DukeNoDescriptionException e) {
                throw new DukeInvalidDescriptionException(String.format("OOPS!!! The description of a `%s` cannot be empty.", keyword));
            } catch (DukeNoDateException e) {
                throw new DukeInvalidDescriptionException(String.format("OOPS!!! The description of `%s` is invalid.", keyword));
            }

        case ("event"):
            try {
                formattedItemString = DukeList.getItemSubstring(strArr);
                newTask = new Event(formattedItemString);
                break;
            } catch (DukeNoDescriptionException e) {
                throw new DukeInvalidDescriptionException(String.format("OOPS!!! The description of a `%s` cannot be empty.", keyword));
            } catch (DukeNoDateException e) {
                throw new DukeInvalidDescriptionException(String.format("OOPS!!! The description of `%s` is invalid.", keyword));
            }

        default:
            throw new DukeInvalidCommandException(String.format("OOPS!!! I'm sorry, but I don't know what `%s` means :-(", keyword));
        }

        this.list.add(newTask);

        return newTask;
    }


    /**
     * Adds a new item to the list.
     *
     * @param itemString String to be added.
     * @return Status string to be printed.
     * @throws DukeException Duke exception.
     */
    public String add(String itemString) throws DukeException {
        Task newTask = addHelper(itemString);
        this.store.addToFileBuffer(newTask);

        return "Got it. I've added this task:\n" +
                String.format("\t%s\n", newTask.toString()) +
                String.format("%s", this.getListStats());
    }


    /**
     * Adds a new item to the list.
     * This method is only accessed privately.
     *
     * @param itemString String to be added.
     * @param isDone     If task is done already.
     * @throws DukeException Duke exception.
     */
    private void add(String itemString, boolean isDone) throws DukeException {
        Task newTask = addHelper(itemString);

        if (isDone) {
            newTask.markAsDone();
        }
    }


    /**
     * Marks an item as done.
     *
     * @param index Index of item to be marked as done.
     *              !This index is the printed index, not the actual index in the list.
     * @return Status string to be printed.
     * @throws NullPointerException invalid index.
     */
    public String markAsDone(int index) throws NullPointerException {
        Task targetTask = this.list.get(index - 1);
        targetTask.markAsDone();
        return String.format("Nice! I've marked this task as done:\n\t%s", targetTask.toString());

    }


    /**
     * Deletes an item from the list.
     *
     * @param index Index of ite to be deleted.
     *              ! This index is the printed index, not the actual index in the list.
     * @return Status string to be printed.
     * @throws NullPointerException invalid index.
     */
    public String delete(int index) throws NullPointerException {
        Task removedTask = this.list.remove(index - 1);
        return "Noted. I've removed this task:\n" +
                String.format("\t%s\n", removedTask.toString())
                + String.format("%s", this.getListStats());
    }
    
    public void writeToFile() {
        this.store.writeToFile();
    }


    /**
     * Adds items that are read from file.
     */
    public void loadFromFile() {
        String[][] parsedLines = this.store.readFromFile();
        for (String[] parsedLine : parsedLines) {
            String itemString = parsedLine[0];
            String isDoneString = parsedLine[1];

            this.add(itemString, isDoneString.equals("1"));
        }
    }


    /**
     * Gets the string to display the stats of the list.
     *
     * @return stats string.
     */
    private String getListStats() {
        return String.format("Now you have %d tasks in the list.", this.list.size());
    }


    @Override
    public String toString() {
        if (this.list.size() == 0) {
            return "List is currently empty!";
        } else {
            StringBuilder outputString = new StringBuilder();
            outputString.append("Here are the tasks in your list:\n");

            for (int i = 0; i < this.list.size(); i++) {
                String currTaskStr = String.format("%d: %s", i + 1, this.list.get(i).toString());
                outputString.append(currTaskStr);

                // add new line and tab only if not at the end of the list
                if (i < list.size() - 1) {
                    outputString.append("\n");
                }
            }

            return outputString.toString();
        }
    }

}
