import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private Board gameBoard;

    public static void main(String[] args) {
        try {
            PrintStream fout = new PrintStream(new FileOutputStream("output.txt"));
            System.setOut(fout);
            FileInputStream fin = new FileInputStream("input.txt");
            System.setIn(fin);
            Scanner sc = new Scanner(System.in);

            // board size
            int D = Integer.parseInt(sc.next());
            if (D < 4 || D > 1000) {
                InvalidBoardSizeException error = new InvalidBoardSizeException();
                System.out.println(error.getMessage());
                System.exit(0);
            }

            Board board = new Board(D);

            // number of insects
            int N = Integer.parseInt(sc.next());
            if (N < 1 || N > 16) {
                InvalidNumberOfInsectsException error = new InvalidNumberOfInsectsException();
                System.out.println(error.getMessage());
                System.exit(0);
            }

            // number of foods
            int M = Integer.parseInt(sc.next());
            if (M < 1 || M > 200) {
                InvalidNumberOFoodPointsException error = new InvalidNumberOFoodPointsException();
                System.out.println(error.getMessage());
                System.exit(0);
            }

            for (int i = 0; i < N; i++) {
                InsectColor color = InsectColor.toColor(sc.next());
                if (color == null) {
                    InvalidInsectColorException error = new InvalidInsectColorException();
                    System.out.println(error.getMessage());
                    System.exit(0);
                }

                // TODO: как сделать чтобы сначала ошибка выводилась на неприавльно название а потом на местоположение
                String insectTypeString = sc.next();


                int x = Integer.parseInt(sc.next());
                if (x < 0 || x > D) {
                    InvalidEntityPositionException error = new InvalidEntityPositionException();
                    System.out.println(error.getMessage());
                    System.exit(0);
                }

                int y = Integer.parseInt(sc.next());
                if (y < 0 || y > D) {
                    InvalidEntityPositionException error = new InvalidEntityPositionException();
                    System.out.println(error.getMessage());
                    System.exit(0);
                }

                EntityPosition entityPosition = new EntityPosition(x, y);

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


                board.addEntity(insectType);

            }

            for (int i = 0; i < M; i++) {

                int foodAmount = Integer.parseInt(sc.next());

                int x = Integer.parseInt(sc.next());
                if (x < 0 || x > D) {
                    InvalidEntityPositionException error = new InvalidEntityPositionException();
                    System.out.println(error.getMessage());
                    System.exit(0);
                }

                int y = Integer.parseInt(sc.next());
                if (y < 0 || y > D) {
                    InvalidEntityPositionException error = new InvalidEntityPositionException();
                    System.out.println(error.getMessage());
                    System.exit(0);
                }

                board.addEntity(new FoodPoint(new EntityPosition(x, y), foodAmount));
            }


            for (int x = 1; x <= D; x ++) {
                for (int y = 1; y <= D; y ++) {
                    if (board.getEntity(new EntityPosition(x, y)) != null) {
                        System.out.println(board.getEntity(new EntityPosition(x, y)).getClass().getSimpleName());
                    }
                }
            }

        } catch (Exception e) {
        }

    }
}

class Board {
    private Map<String, BoardEntity> boardData = new HashMap<>();
    private int size;

    public Map<String, BoardEntity> getBoardData() {
        return boardData;
    }

    public void setBoardData(Map<String, BoardEntity> boardData) {
        this.boardData = boardData;
    }

    public Board(int boardSize) {
        this.size = boardSize;
    }

    // TODO: добавить сущность
    public void addEntity(BoardEntity entity) {
        boardData.put(toString(entity.entityPosition), entity);
    }

    private String toString(EntityPosition entityPosition) {
        return entityPosition.getX() + " " + entityPosition.getY();
    }

    // TODO: получить сущность по позиции
    public BoardEntity getEntity(EntityPosition position) {
        BoardEntity entity = boardData.get(toString(position));
        return entity;
    }

    // TODO: получить направление в зависимости от типа сущности
    public Direction getDirection(Insect insect) {
        return null;
    }

    // TODO: ?? возможно посчитать сумму еды на пути
    public int getDirectionSum(Insect insect) {
        return 0;
    }
}

abstract class BoardEntity {
    protected EntityPosition entityPosition;
}

class EntityPosition {
    private int x;
    private int y;

    public EntityPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}

enum Direction {
    N("North"),
    E("East"),
    S("South"),
    W("West"),
    NE("North-East"),
    SE("South-East"),
    SW("South-West"),
    NW("North-West");

    private final String textRepresentation;

    Direction(String text) {
        textRepresentation = text;
    }

    @Override
    public String toString() {
        return this.textRepresentation;
    }
}

enum InsectColor {
    RED,
    GREEN,
    BLUE,
    YELLOW;

    public static InsectColor toColor(String s) {
        InsectColor color = null;
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
                break;

            // TODO: throw error
        }
        return color;
    }
}

class FoodPoint extends BoardEntity {
    protected int value;

    public FoodPoint(EntityPosition position, int value) {
        this.value = value;
        this.entityPosition = position;
    }
}

abstract class Insect extends BoardEntity {
    protected InsectColor color;

    public Insect(EntityPosition position, InsectColor color) {
        this.color = color;
        this.entityPosition = position;
    }

    // TODO: получить лучшее направление
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        Direction bestDirection = null;
        return bestDirection;
    }

    // TODO: сколько
    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        int travel = 0;
        return travel;
    }
}

class Butterfly extends Insect implements OrthogonalMoving {

    public Butterfly(EntityPosition position, InsectColor color) {
        super(position, color);
    }

    @Override
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        int north = getOrthogonalDirectionVisibleValue(Direction.N, entityPosition, boardData, boardSize);
        int east = getOrthogonalDirectionVisibleValue(Direction.E, entityPosition, boardData, boardSize);
        int south = getOrthogonalDirectionVisibleValue(Direction.S, entityPosition, boardData, boardSize);
        int west = getOrthogonalDirectionVisibleValue(Direction.W, entityPosition, boardData, boardSize);
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

    @Override
    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        return super.travelDirection(dir, boardData, boardSize);
    }

    @Override
    public int getOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition, Map<String, BoardEntity> boardData, int boardSize) {
        int amountOfFood = 0;
        int x = entityPosition.getX();
        int y = entityPosition.getY();
        switch (dir) {
            case N:
                while (x > 1) {
                    x -= 1;
                    BoardEntity entity = boardData.get(x + " " + y);
                    if (entity instanceof FoodPoint) {
                        amountOfFood += ((FoodPoint) entity).value;
                    }
                }
                break;
            case S:
                while (x < boardSize) {
                    x += 1;
                    BoardEntity entity = boardData.get(x + " " + y);
                    if (entity instanceof FoodPoint) {
                        amountOfFood += ((FoodPoint) entity).value;
                    }
                }
                break;
            case E:
                while (y < boardSize) {
                    y += 1;
                    BoardEntity entity = boardData.get(x + " " + y);
                    if (entity instanceof FoodPoint) {
                        amountOfFood += ((FoodPoint) entity).value;
                    }
                }
                break;
            case W:
                while (y > 1) {
                    y -= 1;
                    BoardEntity entity = boardData.get(x + " " + y);
                    if (entity instanceof FoodPoint) {
                        amountOfFood += ((FoodPoint) entity).value;
                    }
                }
                break;
        }
        return amountOfFood;
    }

    @Override
    public int travelOrthogonally(Direction dir, EntityPosition entityPosition, Map<String, BoardEntity> boardData, int boardSize) {
        int amountOfFood = 0;
        int x = entityPosition.getX();
        int y = entityPosition.getY();
        switch (dir) {
            case N:
                while (x > 1) {
                    x -= 1;
                    BoardEntity entity = boardData.get(x + " " + y);
                    if (entity instanceof FoodPoint) {
                        amountOfFood += ((FoodPoint) entity).value;
                    }
                }
                break;
            case S:
                while (x < boardSize) {
                    x += 1;
                    BoardEntity entity = boardData.get(x + " " + y);
                    if (entity instanceof FoodPoint) {
                        amountOfFood += ((FoodPoint) entity).value;
                    }
                }
                break;
            case E:
                while (y < boardSize) {
                    y += 1;
                    BoardEntity entity = boardData.get(x + " " + y);
                    if (entity instanceof FoodPoint) {
                        amountOfFood += ((FoodPoint) entity).value;
                    }
                }
                break;
            case W:
                while (y > 1) {
                    y -= 1;
                    BoardEntity entity = boardData.get(x + " " + y);
                    if (entity instanceof FoodPoint) {
                        amountOfFood += ((FoodPoint) entity).value;
                    }
                }
                break;
        }
        return amountOfFood;
        return 0;
    }
}

class Ant extends Insect implements OrthogonalMoving, DiagonalMoving {

    public Ant(EntityPosition position, InsectColor color) {
        super(position, color);
    }

    @Override
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        return super.getBestDirection(boardData, boardSize);
    }

    @Override
    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        return 0;
    }

    @Override
    public int getOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                                  Map<String, BoardEntity> boardData, int boardSize) {
        int sumOfFoodAmounts = 0;
        if (dir.equals(Direction.N)) {

        }
        return 0;
    }

    @Override
    public int travelOrthogonally(Direction dir, EntityPosition entityPosition, Map<String, BoardEntity> boardData, int boardSize) {
        return 0;
    }

    @Override
    public int getDiagonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition, Map<String, BoardEntity> boardData, int boardSize) {
        return 0;
    }

    @Override
    public int travelDiagonally(Direction dir, EntityPosition entityPosition, Map<String, BoardEntity> boardData, int boardSize) {
        return 0;
    }
}

class Spider extends Insect implements DiagonalMoving {
    public Spider(EntityPosition position, InsectColor color) {
        super(position, color);
    }

    @Override
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        return super.getBestDirection(boardData, boardSize);
    }

    @Override
    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        return super.travelDirection(dir, boardData, boardSize);
    }

    @Override
    public int getDiagonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition, Map<String, BoardEntity> boardData, int boardSize) {
        return 0;
    }

    @Override
    public int travelDiagonally(Direction dir, EntityPosition entityPosition, Map<String, BoardEntity> boardData, int boardSize) {
        return 0;
    }
}

class Grasshopper extends Insect {
    public Grasshopper(EntityPosition position, InsectColor color) {
        super(position, color);
    }

    @Override
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        return super.getBestDirection(boardData, boardSize);
    }

    @Override
    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        return super.travelDirection(dir, boardData, boardSize);
    }
}


interface OrthogonalMoving {
    int getOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                           Map<String, BoardEntity> boardData, int boardSize);

    int travelOrthogonally(Direction dir, EntityPosition entityPosition,
                           Map<String, BoardEntity> boardData, int boardSize);
}

interface DiagonalMoving {
    int getDiagonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                         Map<String, BoardEntity> boardData, int boardSize);

    int travelDiagonally(Direction dir, EntityPosition entityPosition,
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