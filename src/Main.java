
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.LongStream;

public class Main {
    private static Board gameBoard;

    public static void main(String[] args) {
        try {
            PrintStream fout = new PrintStream(new FileOutputStream("output.txt"));
            System.setOut(fout);
            FileInputStream fin = new FileInputStream("input.txt");
            System.setIn(fin);
            Scanner sc = new Scanner(System.in);

            // board size
            int boardSize = Integer.parseInt(sc.next());
            LongStream rangeBoardSize = LongStream.range(4, 1000);
            if (boardSize < 4 || boardSize > 1000) {
                InvalidBoardSizeException error = new InvalidBoardSizeException();
                System.out.println(error.getMessage());
                System.exit(0);
            }

            gameBoard = new Board(boardSize);

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

            List<Insect> listOfInsects = new ArrayList<>();

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
                if (x < 1 || x > boardSize) {
                    InvalidEntityPositionException error = new InvalidEntityPositionException();
                    System.out.println(error.getMessage());
                    System.exit(0);
                }

                int y = Integer.parseInt(sc.next());
                if (y < 1 || y > boardSize) {
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

            for (int i = 0; i < M; i++) {

                int foodAmount = Integer.parseInt(sc.next());

                int x = Integer.parseInt(sc.next());
                if (x < 1 || x > boardSize) {
                    InvalidEntityPositionException error = new InvalidEntityPositionException();
                    System.out.println(error.getMessage());
                    System.exit(0);
                }

                int y = Integer.parseInt(sc.next());
                if (y < 1 || y > boardSize) {
                    InvalidEntityPositionException error = new InvalidEntityPositionException();
                    System.out.println(error.getMessage());
                    System.exit(0);
                }
                EntityPosition entityPosition = new EntityPosition(x, y);
                if (gameBoard.getEntity(entityPosition) != null) {
                    TwoEntitiesOnSamePositionException error = new TwoEntitiesOnSamePositionException();
                    System.out.println(error.getMessage());
                    System.exit(0);
                }
                gameBoard.addEntity(new FoodPoint(entityPosition, foodAmount));
            }

            printData(listOfInsects);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void printData(List<Insect> insectList) {
        for (Insect insect: insectList) {
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
                if (bestDirection.equals(Direction.N) || bestDirection.equals(Direction.E) ||
                        bestDirection.equals(Direction.S) || bestDirection.equals(Direction.W)) {
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
                System.out.print(grasshopper.travelOrthogonally(bestDirection, grasshopper.entityPosition, grasshopper.color,
                        gameBoard.getBoardData(), gameBoard.getSize()));
            }
            System.out.println();
        }
     }
}

class Board {
    private static Map<String, BoardEntity> boardData = new HashMap<>();
    private int size;

    public int getSize() {
        return size;
    }

    public Map<String, BoardEntity> getBoardData() {
        return boardData;
    }

    public static void setBoardData(Map<String, BoardEntity> boardData) {
        Board.boardData = boardData;
    }

    public Board(int boardSize) {
        this.size = boardSize;
    }

    // TODO: добавить сущность
    public void addEntity(BoardEntity entity) {
        boardData.put(toString(entity.entityPosition), entity);
    }

    public static String toString(EntityPosition entityPosition) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityPosition that = (EntityPosition) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
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


    @Override
    public String toString() {
        return super.toString().charAt(0) + super.toString().toLowerCase().substring(1);
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

    public InsectColor getColor() {
        return color;
    }

    public void setColor(InsectColor color) {
        this.color = color;
    }

    // TODO: сколько
    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        int travel = 0;
        return travel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Insect insect = (Insect) o;
        return color == insect.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
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
    public int travelOrthogonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                  Map<String, BoardEntity> boardData, int boardSize) {
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
                        boardData.remove(x + " " + y);
                    } else if (entity instanceof Insect) {
                        if (((Insect) entity).getColor() != color) {
                            break;
                        }
                    }
                }
                break;
            case S:
                while (x < boardSize) {
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
            case E:
                while (y < boardSize) {
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
            case W:
                while (y > 1) {
                    y -= 1;
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
        }
        boardData.remove(Board.toString(entityPosition));
        Board.setBoardData(boardData);
        return amountOfFood;
    }
}

class Ant extends Insect implements OrthogonalMoving, DiagonalMoving {

    public Ant(EntityPosition position, InsectColor color) {
        super(position, color);
    }

    @Override
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
        int north = getOrthogonalDirectionVisibleValue(Direction.N, entityPosition, boardData, boardSize);
        int east = getOrthogonalDirectionVisibleValue(Direction.E, entityPosition, boardData, boardSize);
        int south = getOrthogonalDirectionVisibleValue(Direction.S, entityPosition, boardData, boardSize);
        int west = getOrthogonalDirectionVisibleValue(Direction.W, entityPosition, boardData, boardSize);
        int northEast = getDiagonalDirectionVisibleValue(Direction.NE, entityPosition, boardData, boardSize);
        int southEast = getDiagonalDirectionVisibleValue(Direction.SE, entityPosition, boardData, boardSize);
        int southWest = getDiagonalDirectionVisibleValue(Direction.SW, entityPosition, boardData, boardSize);
        int northWest = getDiagonalDirectionVisibleValue(Direction.NW, entityPosition, boardData, boardSize);
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

    @Override
    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        return 0;
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
    public int travelOrthogonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                  Map<String, BoardEntity> boardData, int boardSize) {
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
                        boardData.remove(x + " " + y);
                    } else if (entity instanceof Insect) {
                        if (((Insect) entity).getColor() != color) {
                            break;
                        }
                    }
                }
                break;
            case S:
                while (x < boardSize) {
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
            case E:
                while (y < boardSize) {
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
            case W:
                while (y > 1) {
                    y -= 1;
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
        }
        boardData.remove(Board.toString(entityPosition));
        Board.setBoardData(boardData);
        return amountOfFood;
    }

    @Override
    public int getDiagonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition, Map<String, BoardEntity> boardData, int boardSize) {
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
        }
        return amountOfFood;
    }

    @Override
    public int travelDiagonally(Direction dir, EntityPosition entityPosition, InsectColor color, Map<String, BoardEntity> boardData, int boardSize) {
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
        }
        boardData.remove(Board.toString(entityPosition));
        Board.setBoardData(boardData);
        return amountOfFood;
    }


}

class Spider extends Insect implements DiagonalMoving {
    public Spider(EntityPosition position, InsectColor color) {
        super(position, color);
    }

    @Override
    public Direction getBestDirection(Map<String, BoardEntity> boardData, int boardSize) {
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

    @Override
    public int travelDirection(Direction dir, Map<String, BoardEntity> boardData, int boardSize) {
        return super.travelDirection(dir, boardData, boardSize);
    }

    @Override
    public int getDiagonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition, Map<String, BoardEntity> boardData, int boardSize) {
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
        }
        return amountOfFood;
    }

    @Override
    public int travelDiagonally(Direction dir, EntityPosition entityPosition, InsectColor color, Map<String, BoardEntity> boardData, int boardSize) {
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
        }
        boardData.remove(Board.toString(entityPosition));
        Board.setBoardData(boardData);
        return amountOfFood;
    }

}

class Grasshopper extends Insect implements OrthogonalMoving{
    public Grasshopper(EntityPosition position, InsectColor color) {
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
                while (x > 2) {
                    x -= 2;
                    BoardEntity entity = boardData.get(x + " " + y);
                    if (entity instanceof FoodPoint) {
                        amountOfFood += ((FoodPoint) entity).value;

                    }
                }
                break;
            case S:
                while (x < boardSize - 1) {
                    x += 2;
                    BoardEntity entity = boardData.get(x + " " + y);
                    if (entity instanceof FoodPoint) {
                        amountOfFood += ((FoodPoint) entity).value;
                    }
                }
                break;
            case E:
                while (y < boardSize - 1) {
                    y += 2;
                    BoardEntity entity = boardData.get(x + " " + y);
                    if (entity instanceof FoodPoint) {
                        amountOfFood += ((FoodPoint) entity).value;
                    }
                }
                break;
            case W:
                while (y > 2) {
                    y -= 2;
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
    public int travelOrthogonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                                  Map<String, BoardEntity> boardData, int boardSize) {
        int amountOfFood = 0;
        int x = entityPosition.getX();
        int y = entityPosition.getY();
        switch (dir) {
            case N:
                while (x > 2) {
                    x -= 2;
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
                while (x < boardSize - 1) {
                    x += 2;
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
                while (y < boardSize - 1) {
                    y += 2;
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
                while (y > 2) {
                    y -= 2;
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
        }
        boardData.remove(Board.toString(entityPosition));
        Board.setBoardData(boardData);
        return amountOfFood;
    }

}


interface OrthogonalMoving {
    int getOrthogonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                           Map<String, BoardEntity> boardData, int boardSize);

    int travelOrthogonally(Direction dir, EntityPosition entityPosition, InsectColor color,
                           Map<String, BoardEntity> boardData, int boardSize);
}

interface DiagonalMoving {
    int getDiagonalDirectionVisibleValue(Direction dir, EntityPosition entityPosition,
                                         Map<String, BoardEntity> boardData, int boardSize);

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
