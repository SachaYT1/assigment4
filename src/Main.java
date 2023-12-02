
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;


/**
 * Main class contains functions for reading and outputting data.
 */
public class Main {
    /**
     * The playing field on which the entities are located.
     */
    private static Board gameBoard;
    /**
     * Constant that specifies the minimum board size.
     */
    private static final int LOWER_BOUND_OF_BOARD_SIZE = 4;
    /**
     * Constant that specifies the maximum board size.
     */
    private static final int UPPER_BOUND_OF_BOARD_SIZE = 1000;
    /**
     * Constant that specifies the minimum number of insects.
     */
    private static final int LOWER_BOUND_OF_NUMBER_OF_INSECTS = 1;
    /**
     * Constant that specifies the maximum number of insects.
     */
    private static final int UPPER_BOUND_OF_NUMBER_OF_INSECTS = 16;
    /**
     * Constant that specifies the minimum number of food points.
     */
    private static final int LOWER_BOUND_OF_NUMBER_OF_FOOD_POINTS = 1;
    /**
     * Constant that specifies the maximum number of food points.
     */
    private static final int UPPER_BOUND_OF_NUMBER_OF_FOOD_POINTS = 200;
    /**
     * List that contains all insects.
     */
    private static List<Insect> listOfInsects;

    /**
     * This function creates two files for reading and outputting data.
     * The corresponding functions are called. Starts the program.
     *
     * @param args standard parameter for main function.
     */
    public static void main(String[] args) {
        try {
            PrintStream fileOut = new PrintStream(new FileOutputStream("output.txt"));
            System.setOut(fileOut);
            FileInputStream fileIn = new FileInputStream("input.txt");
            System.setIn(fileIn);
            Scanner sc = new Scanner(System.in);
            listOfInsects = new ArrayList<>();
            readData(sc);
            printData();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * In this function the data is read from the file and checked for correctness.
     * If all data are correct, the entity is added to the board.
     *
     * @param sc Scanner which used to read data from input file.
     */
    private static void readData(Scanner sc) {
        int boardSize = Integer.parseInt(sc.next());
        if (boardSize < LOWER_BOUND_OF_BOARD_SIZE || boardSize > UPPER_BOUND_OF_BOARD_SIZE) {
            InvalidBoardSizeException error = new InvalidBoardSizeException();
            System.out.println(error.getMessage());
            System.exit(0);
        }

        gameBoard = new Board(boardSize);

        int numberOfInsects = Integer.parseInt(sc.next());

        if (numberOfInsects < LOWER_BOUND_OF_NUMBER_OF_INSECTS
                || numberOfInsects > UPPER_BOUND_OF_NUMBER_OF_INSECTS) {
            InvalidNumberOfInsectsException error = new InvalidNumberOfInsectsException();
            System.out.println(error.getMessage());
            System.exit(0);
        }

        int numberOfFoodPoints = Integer.parseInt(sc.next());
        if (numberOfFoodPoints < LOWER_BOUND_OF_NUMBER_OF_FOOD_POINTS
                || numberOfFoodPoints > UPPER_BOUND_OF_NUMBER_OF_FOOD_POINTS) {
            InvalidNumberOFoodPointsException error = new InvalidNumberOFoodPointsException();
            System.out.println(error.getMessage());
            System.exit(0);
        }


        for (int i = 0; i < numberOfInsects; i++) {
            InsectColor color = InsectColor.toColor(sc.next());
            if (color == null) {
                InvalidInsectColorException error = new InvalidInsectColorException();
                System.out.println(error.getMessage());
                System.exit(0);
            }

            String insectTypeString = sc.next();

            EntityPosition entityPosition = createEntityPosition(sc);

            Insect insectType = null;
            switch (insectTypeString) {
                case "Grasshopper":
                    insectType = new Grasshopper(entityPosition, color);
                    break;
                case "Butterfly":
                    insectType = new Butterfly(entityPosition, color);
                    break;
                case "Ant":
                    insectType = new Ant(entityPosition, color);
                    break;
                case "Spider":
                    insectType = new Spider(entityPosition, color);
                    break;
                default:
                    InvalidInsectTypeException error = new InvalidInsectTypeException();
                    System.out.println(error.getMessage());
                    System.exit(0);
            }

            if (gameBoard.getEntity(entityPosition) != null) {
                TwoEntitiesOnSamePositionException error = new TwoEntitiesOnSamePositionException();
                System.out.println(error.getMessage());
                System.exit(0);
            }
            if (listOfInsects.contains(insectType)) {
                DuplicateInsectException error = new DuplicateInsectException();
                System.out.println(error.getMessage());
                System.exit(0);
            }

            listOfInsects.add(insectType);
            gameBoard.addEntity(insectType);

        }

        for (int i = 0; i < numberOfFoodPoints; i++) {

            int foodAmount = Integer.parseInt(sc.next());

            EntityPosition entityPosition = createEntityPosition(sc);
            if (gameBoard.getEntity(entityPosition) != null) {
                TwoEntitiesOnSamePositionException error = new TwoEntitiesOnSamePositionException();
                System.out.println(error.getMessage());
                System.exit(0);
            }
            gameBoard.addEntity(new FoodPoint(entityPosition, foodAmount));
        }
    }

    /**
     * This function goes through the list, where all insects are stored,
     * and outputs data depending on the type of insect.
     */
    private static void printData() {
        for (Insect insect : listOfInsects) {
            if (insect instanceof Spider) {
                Spider spider = (Spider) insect;
                System.out.print(spider.color.toString() + " ");
                System.out.print("Spider" + " ");
                Direction bestDirection = spider.getBestDirection(gameBoard.getBoardData(), gameBoard.getSize());
                System.out.print(bestDirection.toString() + " ");
                System.out.print(spider.travelDiagonally(bestDirection, spider.entityPosition, spider.color,
                        gameBoard.getBoardData(), gameBoard.getSize()));
            } else if (insect instanceof Butterfly) {
                Butterfly butterfly = (Butterfly) insect;
                System.out.print(butterfly.color.toString() + " ");
                System.out.print("Butterfly" + " ");
                Direction bestDirection = butterfly.getBestDirection(gameBoard.getBoardData(), gameBoard.getSize());
                System.out.print(bestDirection.toString() + " ");
                System.out.print(butterfly.travelOrthogonally(bestDirection, butterfly.entityPosition, butterfly.color,
                        gameBoard.getBoardData(), gameBoard.getSize()));
            } else if (insect instanceof Ant) {
                Ant ant = (Ant) insect;
                System.out.print(ant.color.toString() + " ");
                System.out.print("Ant" + " ");
                Direction bestDirection = ant.getBestDirection(gameBoard.getBoardData(), gameBoard.getSize());
                System.out.print(bestDirection.toString() + " ");
                if (bestDirection.equals(Direction.N) || bestDirection.equals(Direction.E)
                        || bestDirection.equals(Direction.S) || bestDirection.equals(Direction.W)) {
                    System.out.print(ant.travelOrthogonally(bestDirection, ant.entityPosition, ant.color,
                            gameBoard.getBoardData(), gameBoard.getSize()));
                } else {
                    System.out.print(ant.travelDiagonally(bestDirection, ant.entityPosition, ant.color,
                            gameBoard.getBoardData(), gameBoard.getSize()));
                }
            } else if (insect instanceof Grasshopper) {
                Grasshopper grasshopper = (Grasshopper) insect;
                System.out.print(grasshopper.color.toString() + " ");
                System.out.print("Grasshopper" + " ");
                Direction bestDirection = grasshopper.getBestDirection(gameBoard.getBoardData(), gameBoard.getSize());
                System.out.print(bestDirection.toString() + " ");
                System.out.print(grasshopper.travelOrthogonally(bestDirection, grasshopper.entityPosition,
                        grasshopper.color, gameBoard.getBoardData(), gameBoard.getSize()));
            }
            System.out.println();
        }
    }

    /**
     * This function reads the coordinates and checks that they are within the boundaries of the game board.
     *
     * @param sc Scanner which used to read data from input file.
     * @return EntityPosition class object.
     */
    private static EntityPosition createEntityPosition(Scanner sc) {
        int x = Integer.parseInt(sc.next());
        if (x < 1 || x > gameBoard.getSize()) {
            InvalidEntityPositionException error = new InvalidEntityPositionException();
            System.out.println(error.getMessage());
            System.exit(0);
        }

        int y = Integer.parseInt(sc.next());
        if (y < 1 || y > gameBoard.getSize()) {
            InvalidEntityPositionException error = new InvalidEntityPositionException();
            System.out.println(error.getMessage());
            System.exit(0);
        }
        return new EntityPosition(x, y);
    }
}

/**
 * This class contains data about the game board.
 */
class Board {
    /**
     * Object of the Map type.
     * The key is a string that is made by adding the x and y coordinates separated by a space.
     * The value is an object on the board.
     */
    private static Map<String, BoardEntity> boardData = new HashMap<>();
    /**
     * Board size.
     */
    private final int size;

    /**
     * Function that return board size.
     *
     * @return board size.
     */
    public int getSize() {
        return size;
    }

    public Map<String, BoardEntity> getBoardData() {
        return boardData;
    }

    public static void setBoardData(Map<String, BoardEntity> boardData) {
        Board.boardData = boardData;
    }

    /**
     * Constructor of an object of the Board class.
     *
     * @param boardSize board size.
     */
    public Board(int boardSize) {
        this.size = boardSize;
    }

    /**
     * Function that adds an entity to {@link Board#boardData} by coordinates.
     *
     * @param entity board entity.
     */
    public void addEntity(BoardEntity entity) {
        boardData.put(toString(entity.entityPosition), entity);
    }

    /**
     * Function that which translates the coordinates from {@link EntityPosition} to a string.
     *
     * @param entityPosition coordinates of entity.
     * @return string that contains coordinates separated by space.
     */
    public static String toString(EntityPosition entityPosition) {
        return entityPosition.getX() + " " + entityPosition.getY();
    }

    public BoardEntity getEntity(EntityPosition position) {
        return boardData.get(toString(position));
    }

}

/**
 * Class that represents any entity on the board.
 */
abstract class BoardEntity {
    /**
     * Entity position.
     */
    protected EntityPosition entityPosition;
}

/**
 * Class that is responsible for arranging the entity on the board.
 */
class EntityPosition {
    /**
     * x coordinate of entity
     */
    private final int x;
    /**
     * y coordinate of entity
     */
    private final int y;

    /**
     * Contractor of object of Entity position.
     *
     * @param x x coordinate of entity.
     * @param y u coordinate of entity.
     */
    public EntityPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EntityPosition that = (EntityPosition) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}

/**
 * Class that contains all directions of insect movement.
 */
enum Direction {
    N("North"),
    E("East"),
    S("South"),
    W("West"),
    NE("North-East"),
    SE("South-East"),
    SW("South-West"),
    NW("North-West");

    /**
     * String representation of Direction class field.
     */
    private final String textRepresentation;

    Direction(String text) {
        textRepresentation = text;
    }

    @Override
    public String toString() {
        return this.textRepresentation;
    }

}

/**
 * Class that contains all colors of insect.
 */
enum InsectColor {
    RED,
    GREEN,
    BLUE,
    YELLOW;

    public static InsectColor toColor(String s) {
        InsectColor color;
        switch (s) {
            case "Red":
                color = RED;
                break;
            case "Green":
                color = GREEN;
                break;
            case "Blue":
                color = BLUE;
                break;
            case "Yellow":
                color = YELLOW;
                break;
            default:
                color = null;
                break;
        }
        return color;
    }


    @Override
    public String toString() {
        return super.toString().charAt(0) + super.toString().toLowerCase().substring(1);
    }
}

/**
 * Class that represents food point.
 */
class FoodPoint extends BoardEntity {
    /**
     * Value of food point
     */
    protected int value;

    /**
     * Constructor of food point.
     *
     * @param position position of food point.
     * @param value    value of food point.
     */

    public FoodPoint(EntityPosition position, int value) {
        this.value = value;
        this.entityPosition = position;
    }
}

/**
 * Class that represents insect.
 */
abstract class Insect extends BoardEntity {
    /**
     * Insect color.
     */
    protected InsectColor color;

    /**
     * Constructor of insect.
     *
     * @param position position of insect.
     * @param color    insect color.
     */
    public Insect(EntityPosition position, InsectColor color) {
        this.color = color;
        this.entityPosition = position;
    }

    /**
     * Сounts the amount of food for all possible directions and returns the one with the maximum amount of food.
     *
     * @param boardData {@link Board#getBoardData()}
     * @param boardSize {@link Board#getSize()}
     * @return best direction.
     */
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        return null;
    }

    public InsectColor getColor() {
        return color;
    }

    /**
     * A function that counts the amount of food a certain animal can collect on the path without getting killed.
     *
     * @param dir       {@link Direction}
     * @param boardData {@link Board#getBoardData()}
     * @param boardSize {@link Board#getSize()}
     * @return amount of food.
     */
    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Insect insect = (Insect) o;
        return color == insect.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }
}

/**
 * Class that represents butterfly.
 */
class Butterfly extends Insect implements OrthogonalMoving {

    public Butterfly(EntityPosition position, InsectColor color) {
        super(position, color);
    }

    @Override
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        return Travel.getBestOrthogonalDirection(entityPosition, boardData, boardSize, 1);
    }

    @Override
    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        return super.travelDirection(dir, boardData, boardSize);
    }

    @Override
    public int getOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                  Map<String, BoardEntity> boardData, int boardSize) {
        return Travel.getOrthogonalDirectionVisibleValue(dir, entityPosition, boardData, boardSize, 1);
    }

    @Override
    public int travelOrthogonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                  Map<String, BoardEntity> boardData, int boardSize) {
        return Travel.travelOrthogonally(dir, entityPosition, color, boardData, boardSize, 1);
    }
}

/**
 * Class that represents ant
 */
class Ant extends Insect implements OrthogonalMoving, DiagonalMoving {

    public Ant(EntityPosition position, InsectColor color) {
        super(position, color);
    }

    @Override
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        Direction bestOrthogonalDirection = Travel.getBestOrthogonalDirection(entityPosition,
                boardData, boardSize, 1);
        Direction bestDiagonalDirection = Travel.getBestDiagonalDirection(entityPosition, boardData, boardSize);
        int sumOfFoodInOrthogonalDirection = getOrthogonalDirectionVisibleValue(bestOrthogonalDirection, entityPosition,
                boardData, boardSize);
        int sumOfFoodInDiagonalDirection = getDiagonalDirectionVisibleValue(bestDiagonalDirection, entityPosition,
                boardData, boardSize);
        if (sumOfFoodInOrthogonalDirection >= sumOfFoodInDiagonalDirection) {
            return bestOrthogonalDirection;
        } else {
            return bestDiagonalDirection;
        }
    }

    @Override
    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        return 0;
    }

    @Override
    public int getOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                  Map<String, BoardEntity> boardData, int boardSize) {
        return Travel.getOrthogonalDirectionVisibleValue(dir, entityPosition, boardData, boardSize, 1);
    }

    @Override
    public int travelOrthogonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                  Map<String, BoardEntity> boardData, int boardSize) {
        return Travel.travelOrthogonally(dir, entityPosition, color, boardData, boardSize, 1);
    }

    @Override
    public int getDiagonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                Map<String, BoardEntity> boardData, int boardSize) {
        return Travel.getDiagonalDirectionVisibleValue(dir, entityPosition, boardData, boardSize);
    }

    @Override
    public int travelDiagonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                Map<String, BoardEntity> boardData, int boardSize) {
        return Travel.travelDiagonally(dir, entityPosition, color, boardData, boardSize);
    }


}

/**
 * CLass that represents spider.
 */
class Spider extends Insect implements DiagonalMoving {
    public Spider(EntityPosition position, InsectColor color) {
        super(position, color);
    }

    @Override
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        return Travel.getBestDiagonalDirection(entityPosition, boardData, boardSize);
    }

    @Override
    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        return super.travelDirection(dir, boardData, boardSize);
    }

    @Override
    public int getDiagonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                Map<String, BoardEntity> boardData, int boardSize) {
        return Travel.getDiagonalDirectionVisibleValue(dir, entityPosition, boardData, boardSize);
    }

    @Override
    public int travelDiagonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                Map<String, BoardEntity> boardData, int boardSize) {
        return Travel.travelDiagonally(dir, entityPosition, color,
                boardData, boardSize);
    }
}

/**
 * Class that represents grasshopper.
 */
class Grasshopper extends Insect implements OrthogonalMoving {
    public Grasshopper(EntityPosition position, InsectColor color) {
        super(position, color);
    }

    @Override
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        return Travel.getBestOrthogonalDirection(entityPosition, boardData, boardSize, 2);
    }

    @Override
    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        return super.travelDirection(dir, boardData, boardSize);
    }

    @Override
    public int getOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                  Map<String, BoardEntity> boardData, int boardSize) {
        return Travel.getOrthogonalDirectionVisibleValue(dir, entityPosition, boardData, boardSize, 2);
    }

    @Override
    public int travelOrthogonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                  Map<String, BoardEntity> boardData, int boardSize) {
        return Travel.travelOrthogonally(dir, entityPosition, color, boardData, boardSize, 2);
    }

}

/**
 * Class that contains all the functions for moving and getting the best path for an insect.
 */
class Travel {
    public static int travelDiagonally(Direction dir, EntityPosition entityPosition,
                                       InsectColor color, Map<String, BoardEntity> boardData, int boardSize) {
        int amountOfFood = 0;
        int x = entityPosition.getX();
        int y = entityPosition.getY();
        switch (dir) {
            case NE:
                while (x > 1 && y < boardSize) {
                    x -= 1;
                    y += 1;
                    BoardEntity entity = boardData.get(x + " " + y);
                    if (entity instanceof FoodPoint) {
                        amountOfFood += ((FoodPoint) entity).value;
                        boardData.remove(x + " " + y);
                    } else if (entity instanceof Insect) {
                        if (((Insect) entity).getColor() != color) {
                            break;
                        }
                    }
                }
                break;
            case SE:
                while (x < boardSize && y < boardSize) {
                    x += 1;
                    y += 1;
                    BoardEntity entity = boardData.get(x + " " + y);
                    if (entity instanceof FoodPoint) {
                        amountOfFood += ((FoodPoint) entity).value;
                        boardData.remove(x + " " + y);
                    } else if (entity instanceof Insect) {
                        if (((Insect) entity).getColor() != color) {
                            break;
                        }
                    }
                }
                break;
            case SW:
                while (y > 1 && x < boardSize) {
                    y -= 1;
                    x += 1;
                    BoardEntity entity = boardData.get(x + " " + y);
                    if (entity instanceof FoodPoint) {
                        amountOfFood += ((FoodPoint) entity).value;
                        boardData.remove(x + " " + y);
                    } else if (entity instanceof Insect) {
                        if (((Insect) entity).getColor() != color) {
                            break;
                        }
                    }
                }
                break;
            case NW:
                while (y > 1 && x > 1) {
                    y -= 1;
                    x -= 1;
                    BoardEntity entity = boardData.get(x + " " + y);
                    if (entity instanceof FoodPoint) {
                        amountOfFood += ((FoodPoint) entity).value;
                        boardData.remove(x + " " + y);
                    } else if (entity instanceof Insect) {
                        if (((Insect) entity).getColor() != color) {
                            break;
                        }
                    }
                }
                break;
            default:
                break;
        }
        boardData.remove(Board.toString(entityPosition));
        Board.setBoardData(boardData);
        return amountOfFood;
    }

    public static int getDiagonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                       Map<String, BoardEntity> boardData, int boardSize) {
        int amountOfFood = 0;
        int x = entityPosition.getX();
        int y = entityPosition.getY();
        switch (dir) {
            case NE:
                while (x > 1 && y < boardSize) {
                    x -= 1;
                    y += 1;
                    BoardEntity entity = boardData.get(x + " " + y);
                    if (entity instanceof FoodPoint) {
                        amountOfFood += ((FoodPoint) entity).value;
                    }
                }
                break;
            case SE:
                while (x < boardSize && y < boardSize) {
                    x += 1;
                    y += 1;
                    BoardEntity entity = boardData.get(x + " " + y);
                    if (entity instanceof FoodPoint) {
                        amountOfFood += ((FoodPoint) entity).value;
                    }
                }
                break;
            case SW:
                while (y > 1 && x < boardSize) {
                    y -= 1;
                    x += 1;
                    BoardEntity entity = boardData.get(x + " " + y);
                    if (entity instanceof FoodPoint) {
                        amountOfFood += ((FoodPoint) entity).value;
                    }
                }
                break;
            case NW:
                while (y > 1 && x > 1) {
                    y -= 1;
                    x -= 1;
                    BoardEntity entity = boardData.get(x + " " + y);
                    if (entity instanceof FoodPoint) {
                        amountOfFood += ((FoodPoint) entity).value;
                    }
                }
                break;
            default:
                break;
        }
        return amountOfFood;
    }

    public static int getOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                         Map<String, BoardEntity> boardData, int boardSize, int k) {
        int amountOfFood = 0;
        int x = entityPosition.getX();
        int y = entityPosition.getY();
        switch (dir) {
            case N:
                while (x > k) {
                    x -= k;
                    BoardEntity entity = boardData.get(x + " " + y);
                    if (entity instanceof FoodPoint) {
                        amountOfFood += ((FoodPoint) entity).value;

                    }
                }
                break;
            case S:
                while (x <= boardSize - k) {
                    x += k;
                    BoardEntity entity = boardData.get(x + " " + y);
                    if (entity instanceof FoodPoint) {
                        amountOfFood += ((FoodPoint) entity).value;
                    }
                }
                break;
            case E:
                while (y <= boardSize - k) {
                    y += k;
                    BoardEntity entity = boardData.get(x + " " + y);
                    if (entity instanceof FoodPoint) {
                        amountOfFood += ((FoodPoint) entity).value;
                    }
                }
                break;
            case W:
                while (y > k) {
                    y -= k;
                    BoardEntity entity = boardData.get(x + " " + y);
                    if (entity instanceof FoodPoint) {
                        amountOfFood += ((FoodPoint) entity).value;
                    }
                }
                break;
            default:
                break;
        }
        return amountOfFood;
    }

    public static int travelOrthogonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                         Map<String, BoardEntity> boardData, int boardSize, int k) {
        int amountOfFood = 0;
        int x = entityPosition.getX();
        int y = entityPosition.getY();
        switch (dir) {
            case N:
                while (x > k) {
                    x -= k;
                    BoardEntity entity = boardData.get(x + " " + y);
                    if (entity instanceof FoodPoint) {
                        amountOfFood += ((FoodPoint) entity).value;
                        boardData.remove(x + " " + y);
                    } else if (entity instanceof Insect) {
                        if (((Insect) entity).getColor() != color) {
                            break;
                        }
                    }
                }
                break;
            case S:
                while (x <= boardSize - k) {
                    x += k;
                    BoardEntity entity = boardData.get(x + " " + y);
                    if (entity instanceof FoodPoint) {
                        amountOfFood += ((FoodPoint) entity).value;
                        boardData.remove(x + " " + y);
                    } else if (entity instanceof Insect) {
                        if (((Insect) entity).getColor() != color) {
                            break;
                        }
                    }
                }
                break;
            case E:
                while (y <= boardSize - k) {
                    y += k;
                    BoardEntity entity = boardData.get(x + " " + y);
                    if (entity instanceof FoodPoint) {
                        amountOfFood += ((FoodPoint) entity).value;
                        boardData.remove(x + " " + y);
                    } else if (entity instanceof Insect) {
                        if (((Insect) entity).getColor() != color) {
                            break;
                        }
                    }
                }
                break;
            case W:
                while (y > k) {
                    y -= k;
                    BoardEntity entity = boardData.get(x + " " + y);
                    if (entity instanceof FoodPoint) {
                        amountOfFood += ((FoodPoint) entity).value;
                        boardData.remove(x + " " + y);
                    } else if (entity instanceof Insect) {
                        if (((Insect) entity).getColor() != color) {
                            break;
                        }
                    }
                }
                break;
            default:
                break;
        }
        boardData.remove(Board.toString(entityPosition));
        Board.setBoardData(boardData);
        return amountOfFood;
    }

    public static Direction getBestOrthogonalDirection(EntityPosition entityPosition,
                                                       Map<String, BoardEntity> boardData, int boardSize, int k) {

        int north = getOrthogonalDirectionVisibleValue(Direction.N, entityPosition, boardData, boardSize, k);
        int east = getOrthogonalDirectionVisibleValue(Direction.E, entityPosition, boardData, boardSize, k);
        int south = getOrthogonalDirectionVisibleValue(Direction.S, entityPosition, boardData, boardSize, k);
        int west = getOrthogonalDirectionVisibleValue(Direction.W, entityPosition, boardData, boardSize, k);
        int maxAmountOfFood = -1;
        Direction bestDirection = null;
        if (north > maxAmountOfFood) {
            maxAmountOfFood = north;
            bestDirection = Direction.N;
        }
        if (east > maxAmountOfFood) {
            maxAmountOfFood = east;
            bestDirection = Direction.E;
        }
        if (south > maxAmountOfFood) {
            maxAmountOfFood = south;
            bestDirection = Direction.S;
        }
        if (west > maxAmountOfFood) {
            maxAmountOfFood = west;
            bestDirection = Direction.W;
        }
        return bestDirection;
    }

    public static Direction getBestDiagonalDirection(EntityPosition entityPosition,
                                                     Map<String, BoardEntity> boardData, int boardSize) {
        int northEast = getDiagonalDirectionVisibleValue(Direction.NE, entityPosition, boardData, boardSize);
        int southEast = getDiagonalDirectionVisibleValue(Direction.SE, entityPosition, boardData, boardSize);
        int southWest = getDiagonalDirectionVisibleValue(Direction.SW, entityPosition, boardData, boardSize);
        int northWest = getDiagonalDirectionVisibleValue(Direction.NW, entityPosition, boardData, boardSize);
        int maxAmountOfFood = -1;
        Direction bestDirection = null;
        if (northEast > maxAmountOfFood) {
            maxAmountOfFood = northEast;
            bestDirection = Direction.NE;
        }
        if (southEast > maxAmountOfFood) {
            maxAmountOfFood = southEast;
            bestDirection = Direction.SE;
        }
        if (southWest > maxAmountOfFood) {
            maxAmountOfFood = southWest;
            bestDirection = Direction.SW;
        }
        if (northWest > maxAmountOfFood) {
            maxAmountOfFood = northWest;
            bestDirection = Direction.NW;
        }
        return bestDirection;
    }
}

/**
 * Interface that contains functions for moving orthogonally.
 */
interface OrthogonalMoving {
    /**
     * A function that counts the amount of food that can be collected in one of the orthogonal directions.
     *
     * @param dir            {@link Direction} orthogonal direction.
     * @param entityPosition {@link EntityPosition} the position on which the insect is standing.
     * @param boardData      {@link Board#getBoardData()}
     * @param boardSize      {@link Board#getSize()}
     * @return amount of food.
     */
    int getOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                           Map<String, BoardEntity> boardData, int boardSize);

    /**
     * A function that counts the amount of food a certain insect can collect on the
     * orthogonal direction without getting killed.
     *
     * @param dir       {@link Direction} orthogonal direction.
     * @param boardData {@link Board#getBoardData()}
     * @param boardSize {@link Board#getSize()}
     * @return amount of food.
     */
    int travelOrthogonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                           Map<String, BoardEntity> boardData, int boardSize);
}

/**
 * Interface that contains functions for moving diagonally.
 */
interface DiagonalMoving {
    /**
     * A function that counts the amount of food that can be collected in one of the diagonal directions.
     *
     * @param dir            {@link Direction} diagonal direction.
     * @param entityPosition {@link EntityPosition} the position on which the insect is standing.
     * @param boardData      {@link Board#getBoardData()}
     * @param boardSize      {@link Board#getSize()}
     * @return amount of food.
     */
    int getDiagonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                         Map<String, BoardEntity> boardData, int boardSize);

    /**
     * A function that counts the amount of food a certain insect can collect on the
     * diagonal direction without getting killed.
     *
     * @param dir       {@link Direction} diagonal direction.
     * @param boardData {@link Board#getBoardData()}
     * @param boardSize {@link Board#getSize()}
     * @return amount of food.
     */
    int travelDiagonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                         Map<String, BoardEntity> boardData, int boardSize);
}

class InvalidBoardSizeException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid board size";
    }
}

class InvalidNumberOfInsectsException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid number of insects";
    }
}

class InvalidNumberOFoodPointsException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid number of food points";
    }
}

class InvalidInsectColorException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid insect color";
    }
}

class InvalidInsectTypeException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid insect type";
    }
}

class InvalidEntityPositionException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid entity position";
    }
}

class DuplicateInsectException extends Exception {
    @Override
    public String getMessage() {
        return "Duplicate insects";
    }
}

class TwoEntitiesOnSamePositionException extends Exception {
    @Override
    public String getMessage() {
        return "Two entities in the same position";
    }
}
