import org.junit.Test
import org.junit.Assert.*
import scala.util.Random
import synapses.lib._

class BinaryDeepNetworkTest:

  // for constructing networks of varying depth
  def layerSizesF( num_hidden_layers: Int, num_neurons_per_hidden_layer:Int = 6) = 
    List(1024) ++ List.fill(num_hidden_layers)(num_neurons_per_hidden_layer) ++ List(1)

  val rand = new Random(1070)
  private def randBits(n: Int) = List.range(0,n).map(_ => rand.nextBoolean())
  private def bool2Double(b: Boolean) = if(b) 1.0 else 0.0

  // our inputs are bitvectors, so all input values are ether 0 or 1
  // and these are mapped using above helpers accordingly to 0.0 and 1.0
  val input1 = randBits(1024).map(bool2Double)
  val input2 = randBits(1024).map(bool2Double)

  @Test def `inputs not equal`(): Unit = 
    assertNotEquals(input1, input2)

  @Test def `different inputs should give different outputs`(): Unit =
    // a "somewhat deep" network
    // note: if the hidden layer sizes are significantly smaller than the input layer there
    // may be too much information loss and this test may fail. The below parameters seem
    // to work.
    val layerSizes = layerSizesF(num_hidden_layers = 700, num_neurons_per_hidden_layer = 300)

    def activationF(layerIndex: Int): Fun =
      layerIndex match
        case _ => Fun.leakyReLU

    def weightInitF( layerIndex: Int): Double =
      // Uniform Xavier Initialization
      // https://365datascience.com/tutorials/machine-learning-tutorials/what-is-xavier-initialization/

      // get the number of inputs and outputs for the layer in question
      val (num_inputs, num_outputs) = layerIndex match 
        case 0 => 
          // input layer always has same number of inputs and outputs
          (layerSizes(0),layerSizes(0))
        case i if( i == layerSizes.length - 1 ) => 
          // output layer has inputs, but no outputs
          (layerSizes(layerIndex - 1), 0)
        case i => 
          // inner layers have num inputs from prev layer
          (layerSizes(layerIndex - 1), layerSizes(layerIndex))
      
      // return weight selected uniformly from [-x,x]
      val x = math.sqrt(6.0 / (num_inputs + num_outputs))
      rand.between(-x,x)
    
    val net = Net(layerSizes, activationF, weightInitF)

    assertNotEquals(
      "large net with different inputs should still give different outputs",
      net.parPredict(input1),
      net.parPredict(input2)
    )
  
  @Test def `but smaller network properly gives different outputs`(): Unit =
    val layerSizes = layerSizesF(num_hidden_layers = 1, num_neurons_per_hidden_layer = 6)

    def activationF( layerIndex: Int) = Fun.leakyReLU
  
    def weightInitF( layerIndex: Int): Double =
      1.0 - 2.0 * rand.nextDouble()

    val net = Net(layerSizes, activationF, weightInitF)
    assertNotEquals(
      "small net with different inputs should give different outputs",
      net.parPredict(input1),
      net.parPredict(input2)
    )