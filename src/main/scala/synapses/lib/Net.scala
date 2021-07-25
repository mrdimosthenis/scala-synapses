package synapses.lib

import scala.util.chaining._
import scala.util.Random
import synapses.lib.Fun
import synapses.model.Draw
import synapses.model.net_elems.layer.Layer
import synapses.model.net_elems.network._

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
   * @param inParallel When the neural network has huge layers,
   *                   setting this parameter to true, boosts the perforamnce of the calculation.
   * @return The prediction. It's size should be equal to the size of the output layer.
   */
  def predict(inputValues: List[Double], inParallel: Boolean = false): List[Double] =
    throwIfInputNotMatch(layers, inputValues)
    inputValues
      .to(LazyList)
      .pipe(Network.output(_, inParallel)(layers))
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
   * @param learningRate A number that controls how much the weights are adjusted to the observation.
   * @param inputValues The features values of the observation.
   * @param expectedOutput The expected output of the observation.
   *                       It's size should be equal to the size of the output layer.
   * @param inParallel When the neural network has huge layers,
   *                   setting this parameter to true, boosts the perforamnce of the calculation.
   * @return A new neural network that has the same shape of the original,
   *         but it has learned from a single observation.
   */
  def fit(learningRate: Double,
          inputValues: List[Double],
          expectedOutput: List[Double],
          inParallel: Boolean = false): Net =
    throwIfInputNotMatch(layers, inputValues)
    throwIfExpectedNotMatch(layers, expectedOutput)
    val input = inputValues.to(LazyList)
    val expected = expectedOutput.to(LazyList)
    Network
      .fit(learningRate, input, expected, inParallel)(layers)
      .pipe(Network.realize)
      .pipe(Net.apply)

  /** A JSON representation of a neural network.
   *
   * @return The JSON representation of the neural network.
   */
  def json(): String =
    NetworkSerialized.toJson(layers)

  /** An SVG representation of a neural network.
   *
   * @return The SVG representation of the neural network.
   *         The color of each neuron depends on its activation function
   *         while the transparency of the synapses depends on their weight.
   */
  def svg(): String =
    Draw.networkSVG(layers)

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


  /** Creates a neural network.
   *
   * @param json The JSON representation of a neural network.
   * @return A neural network.
   */
  def apply(json: String): Net =
    json
      .pipe(NetworkSerialized.ofJson)
      .pipe(Net.apply)
