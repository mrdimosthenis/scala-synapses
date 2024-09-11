package synapses.model.net_elems.network

import scala.util.chaining._
import synapses.model.net_elems.activation.Activation
import synapses.model.net_elems.layer.Layer

type Network = LazyList[Layer]

object Network:

  def realize(network: Network): Network =
    network.map(Layer.realize)
    network.force
    network

  def init(layerSizes: LazyList[Int],
           activationF: Int => Activation,
           weightInitF: Int => Double): Network =
    layerSizes
      .zip(layerSizes.tail)
      .zip(LazyList.from(0, 1))
      .map { case ((lrSz, nextLrSz), index) =>
        Layer.init(
          lrSz,
          nextLrSz,
          activationF(index),
          weightInitF(index))
      }

  def output(input: LazyList[Double], inParallel: Boolean)
            (network: Network): LazyList[Double] =
    network
      .foldLeft(input) { case (acc, x) =>
        Layer
          .output(acc, inParallel)(x)
          .to(LazyList)
      }

  private def fedForwardAccF(inParallel: Boolean)
                            (alreadyFed: LazyList[(LazyList[Double], Layer)],
                             nextLayer: Layer): LazyList[(LazyList[Double], Layer)] =
    val (errors, layer) = alreadyFed.head
    val nextInput = Layer
      .output(errors, inParallel)(layer)
      .to(LazyList)
    LazyList.cons((nextInput, nextLayer), alreadyFed)

  private def fedForward(input: LazyList[Double], inParallel: Boolean)
                        (network: Network): LazyList[(LazyList[Double], Layer)] =
    val initFeed = LazyList((input, network.head))
    network
      .tail
      .foldLeft(initFeed)(fedForwardAccF(inParallel))

  private def backPropagatedAccF(learningRate: Double, inParallel: Boolean)
                                (errorsWithAlreadyPropagated: (LazyList[Double], LazyList[Layer]),
                                 inputWithLayer: (LazyList[Double], Layer))
  : (LazyList[Double], LazyList[Layer]) =
    val (errors, alreadyPropagated) = errorsWithAlreadyPropagated
    val (lastInput, lastLayer) = inputWithLayer
    val lastOutputWithErrors =
      Layer
        .output(lastInput, inParallel)(lastLayer)
        .zip(errors)
    val (nextErrors, propagatedLayer) =
      Layer.backPropagated(
        learningRate,
        lastInput,
        lastOutputWithErrors,
        inParallel
      )(lastLayer)
    val nextAlreadyPropagated =
      LazyList.cons(propagatedLayer, alreadyPropagated)
    (nextErrors, nextAlreadyPropagated)

  private def backPropagated(learningRate: Double,
                             expectedOutput: LazyList[Double],
                             reversedInputsWithLayers: LazyList[(LazyList[Double], Layer)],
                             inParallel: Boolean,
                             dLossF: (Double,Double) => Double = (x,y) => x-y )
  : (LazyList[Double], Network) =
    val (lastInput, lastLayer) = reversedInputsWithLayers.head
    val output = Layer.output(lastInput, inParallel)(lastLayer)
    val outputWithErrors =
      output
        .zip(expectedOutput)
        .map(dLossF.tupled)
        .pipe(output.zip)
    val (initErrors, firstPropagated) =
      Layer.backPropagated(
        learningRate,
        lastInput,
        outputWithErrors,
        inParallel
      )(lastLayer)
    val initAcc = (initErrors, LazyList(firstPropagated))
    reversedInputsWithLayers
      .tail
      .foldLeft(initAcc)(backPropagatedAccF(learningRate, inParallel))

  def errors(input: LazyList[Double],
             expectedOutput: LazyList[Double],
             inParallel: Boolean,
             dLossF: (Double,Double) => Double = (x,y) => x-y )
            (network: Network): LazyList[Double] =
    backPropagated(
      0.0, // errors should not depend on the learning rate
      expectedOutput,
      fedForward(input, inParallel)(network),
      inParallel,
      dLossF
    )._1

  def fit(learningRate: Double,
          input: LazyList[Double],
          expectedOutput: LazyList[Double],
          inParallel: Boolean,
          dLossF: (Double,Double) => Double = (x,y) => x-y )
         (network: Network): Network =
    backPropagated(
      learningRate,
      expectedOutput,
      fedForward(input, inParallel)(network),
      inParallel,
      dLossF
    )._2
