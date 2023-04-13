package snake.model

import java.util.*

data class Game(
   val config: GameConfig,
   val snake: Snake,
   val apple: Point,
   val score: Int = 0,
   val isLost: Boolean = false,
) {

   data class GameConfig(
      val size: Point = Point(32, 32),
      val hasWalls: Boolean = true,
   )

   companion object {
      fun newGame(gameConfig: GameConfig = GameConfig()): Game {
         return Game(
            config = gameConfig,
            apple = gameConfig.size.randomApple(),
            snake = Snake(
               direction = Snake.Direction.Up,
               body = (0..4).map { Point(10, 10 + it) }
            )
         )
      }

      private fun Point.randomApple() =
         Point(Random().nextInt(x), Random().nextInt(y))
   }

   fun tick(direction: Snake.Direction?): Game {
      var newSnake = snake.move(direction)

      if (isCrashed(newSnake)) {
         return copy(isLost = true)
      }

      var newApple = apple
      var newScore = score

      if (newSnake.head == apple) {
         newScore++
         newSnake = newSnake.grow()

         do {
            newApple = config.size.randomApple()
         } while (
            !(newApple != apple && newApple !in snake.body)
         )
      }

      return copy(
         snake = newSnake,
         score = newScore,
         apple = newApple
      )
   }

   private fun isCrashed(newSnake: Snake) =
      newSnake.head in snake.body
            || newSnake.head.x < 0 || newSnake.head.y < 0
            || newSnake.head.x >= config.size.x || newSnake.head.y >= config.size.y
}