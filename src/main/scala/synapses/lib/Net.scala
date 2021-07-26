package synapses.lib

import scala.util.chaining._
import scala.util.Random
import synapses.lib.Fun
import synapses.model.Draw
import synapses.model.net_elems.layer.Layer
import synapses.model.net_elems.network._

/** The methods of a neural network.
 *
 * Get the prediction for an input:
 * {{{
 *  net.predict(List(0.4, 0.05, 0.2))
 * }}}
 *
 * Fit network to a single observation:
 * {{{
 *  net.fit(0.1, List(0.4, 0.05, 0.2), List(0.03. 0.8))
 * }}}
 *
 * Get the JSON representation of the network:
 * {{{
 *  net.json()
 * }}}
 */
case class Net(layers: LazyList[Layer]):

  private def throwIfInputNotMatch(network: Network, inputValues: List[Double]): Unit =
    val numOfInputVals = inputValues.length
    val inputLayerSize = network.head.head.weights.length - 1
    if (numOfInputVals != inputLayerSize)
      throw new Exception(
        s"the number of input values ($numOfInputVals) " +
          s"does not match the size of the input layer ($inputLayerSize)"
      )

  private def throwIfExpectedNotMatch(network: Network, expectedOutput: List[Double]): Unit =
    val numOfExpectedVals = expectedOutput.length
    val outputLayerSize = network.last.length
    if (numOfExpectedVals != outputLayerSize)
      throw new Exception(
        s"the number of expected values ($numOfExpectedVals)" +
          s" does not match the size of the output layer ($outputLayerSize)"
      )

  /** Makes a prediction for the provided input.
   *
   * @param inputValues The values of the features. Their size should be equal to the size of the input layer.
   * @return The prediction. It's size should be equal to the size of the output layer.
   */
  def predict(inputValues: List[Double]): List[Double] =
    throwIfInputNotMatch(layers, inputValues)
    inputValues
      .to(LazyList)
      .pipe(Network.output(_, false)(layers))
      .toList

  /** Makes a prediction for the provided input in parallel.
   * When the neural network has huge layers, the parallel calculation boosts the perforamnce.
   *
   * @param inputValues The values of the features. Their size should be equal to the size of the input layer.
   * @return The prediction. It's size should be equal to the size of the output layer.
   */
  def parPredict(inputValues: List[Double]): List[Double] =
    throwIfInputNotMatch(layers, inputValues)
    inputValues
      .to(LazyList)
      .pipe(Network.output(_, true)(layers))
      .toList

  def errors(inputValues: List[Double],
             expectedOutput: List[Double],
             inParallel: Boolean = false): List[Double] =
    throwIfInputNotMatch(layers, inputValues)
    throwIfExpectedNotMatch(layers, expectedOutput)
    val input = inputValues.to(LazyList)
    val expected = expectedOutput.to(LazyList)
    Network
      .errors(input, expected, inParallel)(layers)
      .toList

  /** Returns the neural network with its weights adjusted to the provided observation.
   * In order for it to be trained, it should fit with multiple observations,
   * usually by folding over an iterator.
   *
   * @param learningRate   A number that controls how much the weights are adjusted to the observation.
   * @param inputValues    The features values of the observation.
   * @param expectedOutput The expected output of the observation.
   *                       It's size should be equal to the size of the output layer.
   * @param inParallel     When the neural network has huge layers,
   *                       setting this parameter to true, boosts the perforamnce of the calculation.
   * @return A new neural network that has the same shape of the original,
   *         but it has learned from a single observation.
   */
  def fit(learningRate: Double,
          inputValues: List[Double],
          expectedOutput: List[Double]): Net =
    throwIfInputNotMatch(layers, inputValues)
    throwIfExpectedNotMatch(layers, expectedOutput)
    val input = inputValues.to(LazyList)
    val expected = expectedOutput.to(LazyList)
    Network
      .fit(learningRate, input, expected, false)(layers)
      .pipe(Network.realize)
      .pipe(Net.apply)

  /** Returns the neural network with its weights adjusted to the provided observation.
   * When the neural network has huge layers, the parallel calculation boosts the perforamnce.
   *
   * @param learningRate   A number that controls how much the weights are adjusted to the observation.
   * @param inputValues    The features values of the observation.
   * @param expectedOutput The expected output of the observation.
   *                       It's size should be equal to the size of the output layer.
   * @return A new neural network that has the same shape of the original,
   *         but it has learned from a single observation.
   */
  def parFit(learningRate: Double,
             inputValues: List[Double],
             expectedOutput: List[Double]): Net =
    throwIfInputNotMatch(layers, inputValues)
    throwIfExpectedNotMatch(layers, expectedOutput)
    val input = inputValues.to(LazyList)
    val expected = expectedOutput.to(LazyList)
    Network
      .fit(learningRate, input, expected, true)(layers)
      .pipe(Network.realize)
      .pipe(Net.apply)

  /** The JSON representation of the neural network.
   *
   * @return The JSON representation of the neural network.
   */
  def json(): String =
    NetworkSerialized.toJson(layers)

  /** An SVG representation of the neural network.
   *
   * @return The SVG representation of the neural network.
   *         The color of each neuron depends on its activation function
   *         while the transparency of the synapses depends on their weight.
   */
  def svg(): String =
    Draw.networkSVG(layers)

/** The constructors of a neural network.
 *
 * There are four ways to create a neural network:
 *
 * 1. By providing its layer sizes. This constructor creates a random sigmoid neural network.
 * {{{
 *  Net(List(2, 3, 1))
 * }}}
 *
 * 2. By providing its layer sizes and a seed. This constructor creates a non-random sigmoid neural network.
 * {{{
 *  Net(List(4, 6, 8, 5, 3), 1000)
 * }}}
 *
 * 3. By providing its JSON representation.
 * {{{
 *  Net("""[[{"activationF":"sigmoid","weights":[-0.4,-0.1,-0.8]}]]""")
 * }}}
 *
 * 4. By providing the size, the activation function and the weights for each layer.
 * {{{
 *  Net(List(4, 8, 3), _ => Fun.tanh, _ => Random().nextDouble())
 * }}}
 */
object Net:

  /** Creates a neural network.
   *
   * @param layerSizes  The size of each layer.
   * @param activationF A function that accepts the index of a layer and returns an activation function for its neurons.
   * @param weightInitF A function that accepts the index of a layer and returns a weight for the synapses of its neurons.
   * @return A new neural network.
   */
  def apply(layerSizes: List[Int], activationF: Int => Fun, weightInitF: Int => Double): Net =
    layerSizes
      .to(LazyList)
      .pipe(Network.init(_, activationF, weightInitF))
      .pipe(Network.realize)
      .pipe(Net.apply)

  /** Creates a random neural network.
   * The activation function of the nodes is sigmoid.
   * The weight distribution of the synapses is normal between -1.0 and 1.0.
   *
   * @param layerSizes The size of each layer.
   *                   The first number in the list defines the size of the input layer.
   *                   The last number in the list defines the size of the output layer.
   *                   In order for a neural network to be deep, the list should contain more than two numbers.
   * @return A new neural network.
   */
  def apply(layerSizes: List[Int]): Net =
    val activationF = (_: Int) => Fun.sigmoid
    val weightInitF = (_: Int) => 1.0 - 2.0 * Random().nextDouble()
    apply(layerSizes, activationF, weightInitF)

  /** Creates a non-random neural network.
   * Calling this function with the same parameters multiple times, should always return the same neural network.
   * The activation function of the nodes is sigmoid.
   * The weight distribution of the synapses is normal between -1.0 and 1.0.
   *
   * @param layerSizes The size of each layer.
   * @param seed       A number used to initialize the internal pseudorandom number generator.
   * @return A new neural network.
   */
  def apply(layerSizes: List[Int], seed: Long): Net =
    val activationF = (_: Int) => Fun.sigmoid
    val rnd = new Random(seed)
    val weightInitF =
      (_: Int) => 1.0 - 2.0 * rnd.nextDouble()
    apply(layerSizes, activationF, weightInitF)


  /** Parses a neural network.
   *
   * @param json The JSON representation of a neural network.
   * @return A neural network.
   */
  def apply(json: String): Net =
    json
      .pipe(NetworkSerialized.ofJson)
      .pipe(Net.apply)
