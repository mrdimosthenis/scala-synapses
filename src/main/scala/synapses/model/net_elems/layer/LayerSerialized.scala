package synapses.model.net_elems.layer

import synapses.model.net_elems.neuron.NeuronSerialized

type LayerSerialized = List[NeuronSerialized]

object LayerSerialized:

  def serialized(layer: Layer): LayerSerialized =
    layer
      .map(NeuronSerialized.serialized)
      .toList

  def deserialized(layerSerialized: LayerSerialized): Layer =
    layerSerialized
      .to(LazyList)
      .map(NeuronSerialized.deserialized)
