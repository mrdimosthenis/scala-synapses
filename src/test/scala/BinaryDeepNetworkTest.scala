import org.junit.Test
import org.junit.Assert.*
import scala.util.Random
import synapses.lib._

class BinaryDeepNetworkTest:

  // a "somewhat deep" network with 37 hidden layers and a single output
  val layerSizesLarge = List(1024) ++ List.fill(67)(6) ++ List(1)

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
      case _ => Fun.leakyReLU

  def weightInitFlarge( layerIndex: Int): Double =
    // Uniform Xavier Initialization
    // https://365datascience.com/tutorials/machine-learning-tutorials/what-is-xavier-initialization/

    // get the number of inputs and outputs for the layer in question
    val (num_inputs, num_outputs) = layerIndex match 
      case 0 => 
        // input layer always has same number of inputs and outputs
        (layerSizesLarge(0),layerSizesLarge(0))
      case i if( i == layerSizesLarge.size - 1 ) => 
        // output layer has inputs, but no outputs
        (layerSizesLarge(layerIndex - 1), 0)
      case i => 
        // inner layers have num inputs from prev layer
        (layerSizesLarge(layerIndex - 1), layerSizesLarge(layerIndex))
    
    // return weight selected uniformly from [-x,x]
    val x = math.sqrt(6.0 / (num_inputs + num_outputs))
    rand.between(-x,x)


  def weightInitFsmall(_layerIndex: Int): Double =
    1.0 - 2.0 * rand.nextDouble()

  val largeNet = Net(layerSizesLarge, activationF, weightInitFlarge)
  val smallNet = Net(layerSizesSmall, activationF, weightInitFsmall)

  @Test def `inputs not equal`(): Unit = 
    assertNotEquals(input1, input2)

  @Test def `different inputs should give different outputs`(): Unit =
    assertNotEquals(
      largeNet.parPredict(input1),
      largeNet.parPredict(input2)
    )
  
  @Test def `but smaller network properly gives different outputs`(): Unit =
    assertNotEquals(
      smallNet.parPredict(input1),
      smallNet.parPredict(input2)
    )