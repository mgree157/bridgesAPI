import bridges.base.NamedColor;
import bridges.base.NamedSymbol;
import bridges.games.NonBlockingGame;

class Snake extends NonBlockingGame {
  // keep an element to represent something the snake would consume to grow,
  // for instance, an apple and keep track of the snake head (both will be elements of
  // type Block

  // keep track of snake direction (can move in all 4 directions, its last direction
  // and current direction

  java.util.Random random = new java.util.Random();
  static int gridColumns = 30;
  static int gridRows = 30;

  final long FRAMERATE = 1000000000 / 15;
  long frameTime;
  long nextFrameTime;

  NamedColor bg = NamedColor.forestgreen;
  NamedColor bc = NamedColor.green;
  NamedColor fg = NamedColor.silver;
  NamedColor hc = NamedColor.white;
  NamedColor ac = NamedColor.red;

  // constructor - set bridges credentials, grid size
  public Snake(int assid, String login, String apiKey, int c, int r) {
    super(assid, login, apiKey, c, r);
  }

  // user input related
  public void handleInput() {
    // Use the 4 arrow keys to move the snake in a particular direction
  }

  // update snake position
  public void updatePosition() {
    // Move the snake one position, based on its direction and update
    // the linked list


    // handle edge cases - check to make sure the snake
    // doesnt go off the edge of the board; can do a wrap around
    // in X or Y to handle it. Must handle all 4 directions the snake
    // might be traveling..
  }

  // create a new apple (position)
  public void plantApple() {
    // randomly position the apple, taking care to ensure that it doesnt
    // intersect with the snake position.
  }

  // check if snake has found the apple
  public void detectApple() {
    // if apple is found, snake consumes it and update the board and plant
    // a new apple on the board.
  }

  // check if snake ate itself! Yuk!
  public void detectDeath() {

  }

  // redraw
  public void paint() {
    // draw the board, the apple and the snake
    // make sure to choose colors so that snake and apple are clearly visible.
  }

  // Set up the first state of the game grid
  public void initialize() {
    // create the snake of some number of elements,
    // perform all initializations, place the apple
  }

  // Game loop will run many times per second.
  // handle input, check if apple was detected, update position, redraw,
  // detect if snake ate itself
  public void gameLoop() {

  }

  public static void main(String args[]) {
    Snake game = new Snake(22, "BRIDGES_USER_ID", "BRIDGES_API_KEY",
                           gridColumns, gridRows);
    game.setTitle("Snake");
    game.setDescription("Snake: Eat the food, not yourself! Use the arrow keys to move.");
    game.start();
  }
}

enum Direction {
  NORTH,
  SOUTH,
  EAST,
  WEST
}

// helper class to hold snake and apple objects; not snake grows as it
// eats and hence a linked list of blocks
class Block {
  public Block next;
  public int x;
  public int y;

  public Block() {
    this(-1, -1, null);
  }

  public Block(int x, int y) {
    this(x, y, null);
  }

  public Block(int x, int y, Block next) {
    this.x = x;
    this.y = y;
    this.next = next;
  }
}
