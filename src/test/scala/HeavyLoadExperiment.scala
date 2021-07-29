import org.junit.{Test, Ignore}
import org.junit.Assert.*
import scala.util.chaining._
import scala.util.Random
import synapses.lib._

class HeavyLoadExperiment:

  private val rnd = new Random(1000)

  private val firstLayerSize = 1000

  private val lastLayerSize = 2

  private val layers = List(firstLayerSize, 1500, 3000, 10, lastLayerSize)

  private def activationF(layerIndex: Int) =
    layerIndex match
      case 0 => Fun.sigmoid
      case 1 => Fun.tanh
      case 2 => Fun.leakyReLU
      case 3 => Fun.identity

  private def weightInitF(_layerIndex: Int) =
    1.0 - 2.0 * rnd.nextDouble()

  private val initNeuralNetwork = Net(layers, activationF, weightInitF)

  private def randomInputValues() =
    List.fill(firstLayerSize)(rnd.nextDouble())

  private def randomExpectedValues() =
    List.fill(lastLayerSize)(rnd.nextDouble())

  @Ignore
  @Test def heavyLoadExperiment(): Unit =
    val startMillis = System.currentTimeMillis()
    val lastPredictionSize =
      LazyList
        .range(0, 3)
        .foldLeft(initNeuralNetwork) { case (acc, _) =>
          acc.fit(0.1, randomInputValues(), randomExpectedValues())
        }
        .predict(randomInputValues())
        .size
    val endMillis = System.currentTimeMillis()
    println(s"${endMillis - startMillis}: the duration of SERIAL experiment")
    assertEquals(lastLayerSize, lastPredictionSize)

  @Ignore
  @Test def heavyLoadExperimentInParallel(): Unit =
    val startMillis = System.currentTimeMillis()
    val lastPredictionSize =
      LazyList
        .range(0, 3)
        .foldLeft(initNeuralNetwork) { case (acc, _) =>
          acc.parFit(0.1, randomInputValues(), randomExpectedValues())
        }
        .parPredict(randomInputValues())
        .size
    val endMillis = System.currentTimeMillis()
    println(s"${endMillis - startMillis}: the duration of PARALLEL experiment")
    assertEquals(lastLayerSize, lastPredictionSize)
