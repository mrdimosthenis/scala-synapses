import org.junit.Test
import org.junit.Assert.*
import scala.io.Source
import scala.util.Random
import synapses.lib._

class CustomizedNetworkTest:

  val inputValues = List(1.0, 0.5625, 0.511111, 0.47619)

  val expectedOutput = List(0.4, 0.05, 0.2)

  val layers = List(4, 6, 8, 5, 3)

  def activationF(layerIndex: Int): Fun =
    layerIndex match
      case 0 => Fun.sigmoid
      case 1 => Fun.identity
      case 2 => Fun.leakyReLU
      case 3 => Fun.tanh

  val rnd = new Random(1000)

  def weightInitF(_layerIndex: Int): Double =
    1.0 - 2.0 * rnd.nextDouble()

  val justCreatedNet: Net =
    Net(layers, activationF, weightInitF)

  val justCreatedNetJson: String =
    justCreatedNet.json()

  @Test def `neural network of to json`(): Unit =
    val netJson = Net(justCreatedNetJson)
    assertEquals(
      netJson.json(),
      justCreatedNetJson
    )

  val jsonSource: Source = Source.fromFile("test-resources/network.json")

  val svgSource: Source = Source.fromFile("test-resources/drawing.svg")

  val neuralNetworkJson: String = jsonSource.getLines.mkString

  val neuralNetworkSvg: String = svgSource.getLines.mkString

  val neuralNetwork: Net =
    Net(neuralNetworkJson)

  val prediction: List[Double] =
    neuralNetwork.parPredict(inputValues)

  @Test def `neural network prediction`(): Unit =
    assertEquals(
      List(-0.013959435951885571, -0.16770539176070537, 0.6127887629040738),
      prediction
    )

  val learningRate = 0.01

  @Test def `neural network normal errors`(): Unit =
    assertEquals(
      neuralNetwork.errors(inputValues, expectedOutput, true),
      List(-0.18229373795952453, -0.10254022760223255, -0.09317233470223055, -0.086806455078946)
    )

  @Test def `neural network zero errors`(): Unit =
    assertEquals(
      List(0, 0, 0, 0),
      neuralNetwork.errors(inputValues, prediction, true)
    )

  val fitNet: Net =
    neuralNetwork.parFit(learningRate, inputValues, expectedOutput)

  @Test def `fit neural network prediction`(): Unit =
    assertEquals(
      List(-0.006109464554743645, -0.1770428172237149, 0.6087944183600162),
      fitNet.parPredict(inputValues)
    )

  @Test def `neural network svg`(): Unit =
    assertEquals(
      neuralNetworkSvg,
      neuralNetwork.svg()
    )

  jsonSource.close()

  svgSource.close()
