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

  def predict(inputValues: List[Double], inParallel: Boolean = false): List[Double] =
    throwIfInputNotMatch(layers, inputValues)
    inputValues
      .to(LazyList)
      .pipe(Network.output(_, inParallel)(layers))
      .toList

  // not documented
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

  def json(): String =
    NetworkSerialized.toJson(layers)

  def svg(): String =
    Draw.networkSVG(layers)

object Net:

  def apply(layerSizes: List[Int], activationF: Int => Fun, weightInitF: Int => Double): Net =
    layerSizes
      .to(LazyList)
      .pipe(Network.init(_, activationF, weightInitF))
      .pipe(Network.realize)
      .pipe(Net.apply)

  def apply(layerSizes: List[Int]): Net =
    val activationF = (_: Int) => Fun.sigmoid
    val weightInitF = (_: Int) => 1.0 - 2.0 * Random().nextDouble()
    apply(layerSizes, activationF, weightInitF)

  def apply(layerSizes: List[Int], seed: Long): Net =
    val activationF = (_: Int) => Fun.sigmoid
    val rnd = new Random(seed)
    val weightInitF =
      (_: Int) => 1.0 - 2.0 * rnd.nextDouble()
    apply(layerSizes, activationF, weightInitF)

  def apply(json: String): Net =
    json
      .pipe(NetworkSerialized.ofJson)
      .pipe(Net.apply)
