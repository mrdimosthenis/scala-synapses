import org.junit.Test
import org.junit.Assert.*
import synapses.lib._

class StatisticsTest:

  val allowedErr = 0.0001

  @Test def `root mean square error`(): Unit =
    val expectedWithOutputValues =
      Iterator(
        (List(0.0, 0.0, 1.0), List(0.0, 0.0, 1.0)),
        (List(0.0, 0.0, 1.0), List(0.0, 1.0, 1.0))
      )
    assertEquals(
      0.7071067811865476,
      Stats.rmse(expectedWithOutputValues),
      allowedErr
    )

  @Test def `accuracy`(): Unit =
    val expectedWithOutputValues =
      Iterator(
        (List(0.0, 0.0, 1.0), List(0.0, 0.1, 0.9)),
        (List(0.0, 1.0, 0.0), List(0.8, 0.2, 0.0)),
        (List(1.0, 0.0, 0.0), List(0.7, 0.1, 0.2)),
        (List(1.0, 0.0, 0.0), List(0.3, 0.3, 0.4)),
        (List(0.0, 0.0, 1.0), List(0.2, 0.2, 0.6))
      )
    assertEquals(
      0.6,
      Stats.score(expectedWithOutputValues),
      allowedErr
    )
