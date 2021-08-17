package synapses.jvm

import scala.jdk.StreamConverters._
import scala.jdk.FunctionConverters._
import scala.jdk.CollectionConverters._
import scala.util.chaining._
import synapses.lib.Fun
import synapses.lib.Net

case class NetJ(net: Net):

  def predict(inputValues: Array[Double]): Array[Double] =
    inputValues
      .toList
      .pipe(net.predict)
      .toArray

  def parPredict(inputValues: Array[Double]): Array[Double] =
    inputValues
      .toList
      .pipe(net.parPredict)
      .toArray

  def errors(inputValues: Array[Double],
             expectedOutput: Array[Double],
             inParallel: Boolean = false): Array[Double] =
    net.errors(
      inputValues.toList,
      expectedOutput.toList,
      inParallel
    ).toArray

  def fit(learningRate: Double,
          inputValues: Array[Double],
          expectedOutput: Array[Double]): NetJ =
    net.fit(
      learningRate,
      inputValues.toList,
      expectedOutput.toList,
    ).pipe(NetJ.apply)

  def parFit(learningRate: Double,
             inputValues: Array[Double],
             expectedOutput: Array[Double]): NetJ =
    net.parFit(
      learningRate,
      inputValues.toList,
      expectedOutput.toList,
    ).pipe(NetJ.apply)

  def json(): String =
    net.json()

  def svg(): String =
    net.svg()

object NetJ:

  def apply(layerSizes: Array[Int],
            activationF: java.util.function.IntFunction[Fun],
            weightInitF: java.util.function.IntFunction[Double]): NetJ =
    Net(
      layerSizes.toList,
      activationF.asScala,
      weightInitF.asScala
    ).pipe(NetJ.apply)

  def apply(layerSizes: Array[Int]): NetJ =
    layerSizes
      .toList
      .pipe(Net.apply)
      .pipe(NetJ.apply)

  def apply(layerSizes: Array[Int], seed: Long): NetJ =
    Net(
      layerSizes.toList,
      seed
    ).pipe(NetJ.apply)

  def apply(json: String): NetJ =
    json
      .pipe(Net.apply)
      .pipe(NetJ.apply)
