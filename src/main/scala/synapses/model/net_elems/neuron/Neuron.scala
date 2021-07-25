package synapses.model.net_elems.neuron

import synapses.model.Mathematics
import synapses.model.net_elems.activation.Activation

import scala.util.chaining._

case class Neuron(activationF: Activation, weights: LazyList[Double])

object Neuron:

  def realize(neuron: Neuron): Neuron =
    neuron.weights.force
    neuron

  def init(inputSize: Int,
           activationF: Activation,
           weightInitF: => Double): Neuron =
    LazyList
      .range(0, inputSize + 1)
      .map(_ => weightInitF)
      .pipe(Neuron(activationF, _))

  def output(input: LazyList[Double])
            (neuron: Neuron): Double =
    Mathematics
      .dotProduct(
        LazyList.cons(1.0, input),
        neuron.weights
      )
      .pipe(neuron.activationF.f)

  def backPropagated(learningRate: Double,
                     input: LazyList[Double],
                     outputWithError: (Double, Double))
                    (neuron: Neuron): (LazyList[Double], Neuron) =
    val (output, error) = outputWithError
    val commonFactor =
      neuron
        .activationF
        .inverse(output)
        .pipe(error * neuron.activationF.deriv(_))
    val inErrors = input.map(_ * commonFactor)
    val newNeuron =
      neuron
        .weights
        .zip(LazyList.cons(1.0, input))
        .map { case (w, x) =>
          w - learningRate * commonFactor * x
        }
        .pipe(Neuron(neuron.activationF, _))
    (inErrors, newNeuron)
