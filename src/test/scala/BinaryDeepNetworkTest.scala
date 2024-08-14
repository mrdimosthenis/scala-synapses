import org.junit.Test
import org.junit.Assert.*
import scala.util.Random
import synapses.lib._

class BinaryDeepNetworkTest:

  // a "somewhat deep" network with 37 hidden layers and a single output
  val layerSizesLarge = List(1024) ++ List.fill(37)(6) ++ List(1)

  // same input and output layers but with only one hidden layer
  val layerSizesSmall= List(1024) ++ List(6) ++ List(1)

  val rand = new Random(1000)
  private def randBits(n: Int) = List.range(0,n).map(_ => rand.nextBoolean())
  private def bool2Double(b: Boolean) = if(b) 1.0 else 0.0

  // our inputs are bitvectors, so all input values are ether 0 or 1
  // and these are mapped using above helpers accordingly to 0.0 and 1.0
  val input1 = randBits(1024).map(bool2Double)
  val input2 = randBits(1024).map(bool2Double)

  def activationF(layerIndex: Int): Fun =
    layerIndex match
      case _ => Fun.sigmoid

  def weightInitF(_layerIndex: Int): Double =
    1.0 - 2.0 * rand.nextDouble()

  val largeNet = Net(layerSizesLarge, activationF, weightInitF)
  val smallNet = Net(layerSizesSmall, activationF, weightInitF)

  @Test def `inputs not equal`(): Unit = 
    assertNotEquals(input1, input2)

  @Test def `same exact output for different inputs`(): Unit =
    assertEquals(
      largeNet.parPredict(input1),
      largeNet.parPredict(input2)
    )
  
  @Test def `but smaller network properly gives different outputs`(): Unit =
    assertNotEquals(
      smallNet.parPredict(input1),
      smallNet.parPredict(input2)
    )