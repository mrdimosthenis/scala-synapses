package synapses.model.net_elems.layer

import scala.collection.parallel.immutable.ParVector
import synapses.model.net_elems.activation.Activation
import synapses.model.net_elems.neuron.Neuron

type Layer = LazyList[Neuron]

object Layer:

  def realize(layer: Layer): Layer =
    layer.map(Neuron.realize)
    layer.force
    layer

  extension[A] (lazyList: LazyList[A])
    def pmap[B](f: A => B): LazyList[B] =
      lazyList
        .to(ParVector)
        .map(f)
        .to(LazyList)

  def init(inputSize: Int,
           outputSize: Int,
           activation: Activation,
           weightInitF: => Double): Layer =
    LazyList
      .range(0, outputSize)
      .map { _ =>
        Neuron.init(inputSize, activation, weightInitF)
      }

  def output(input: LazyList[Double], inParallel: Boolean)
            (layer: Layer): LazyList[Double] =
    if inParallel then
      layer.pmap(Neuron.output(input))
    else
      layer.map(Neuron.output(input))

  def backPropagated(learningRate: Double,
                     input: LazyList[Double],
                     outputWithErrors: LazyList[(Double, Double)],
                     inParallel: Boolean)
                    (layer: Layer): (LazyList[Double], Layer) =
    def f(outWithError: (Double, Double), neuron: Neuron) =
      Neuron.backPropagated(
        learningRate,
        input,
        outWithError
      )(neuron)

    val (errorsMulti, newNeurons) =
      if inParallel then
        outputWithErrors
          .zip(layer)
          .pmap(f)
          .unzip
      else
        outputWithErrors
          .zip(layer)
          .map(f)
          .unzip
    val inErrors =
      errorsMulti.foldLeft(LazyList.continually(0.0)) {
        case (acc, x) => acc
          .zip(x)
          .map(t => t._1 + t._2)
      }
    (inErrors, newNeurons)
