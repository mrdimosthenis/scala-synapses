package synapses.model.net_elems.neuron

import scala.util.chaining._
import synapses.model.net_elems.activation.ActivationSerialized

case class NeuronSerialized(activationF: ActivationSerialized,
                            weights: List[Double])

object NeuronSerialized:

  def serialized(neuron: Neuron): NeuronSerialized =
    neuron
      .activationF
      .pipe(ActivationSerialized.serialized)
      .pipe(NeuronSerialized(_, neuron.weights.toList))

  def deserialized(neuronSerialized: NeuronSerialized): Neuron =
    neuronSerialized
      .activationF
      .pipe(ActivationSerialized.deserialized)
      .pipe(Neuron(_, neuronSerialized.weights.to(LazyList)))
