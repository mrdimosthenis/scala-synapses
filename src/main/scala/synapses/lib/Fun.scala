package synapses.lib

import synapses.model.net_elems.activation.Activation

type Fun = Activation

object Fun:
  val sigmoid: Fun = Activation.Sigmoid
  val identity: Fun = Activation.Identity
  val tanh: Fun = Activation.Tanh
  val leakyReLU: Fun = Activation.LeakyReLU
