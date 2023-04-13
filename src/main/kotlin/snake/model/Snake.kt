package snake.model

data class Snake(
   val direction: Direction,
   val body: List<Point>,
) {
   enum class Direction(val code: Int) {
      Left(-1), Right(1), Up(-2), Down(2),
   }

   val head: Point
      get() = body.first()

   fun move(desiredDirection: Direction? = null): Snake {
      val isConflictingDirection = (desiredDirection?.code ?: 0) + direction.code == 0

      val newDirection = desiredDirection?.takeIf {
         !isConflictingDirection
      } ?: direction

      val newHeadPoint = when (newDirection) {
         Direction.Left -> Point(head.x + 1, head.y)
         Direction.Right -> Point(head.x - 1, head.y)
         Direction.Up -> Point(head.x, head.y - 1)
         Direction.Down -> Point(head.x, head.y + 1)
      }

      val newBody = listOf(newHeadPoint) + body.dropLast(1)
      return Snake(newDirection, newBody)
   }

   fun grow(): Snake {
      return copy(body = body + body.last())
   }
}