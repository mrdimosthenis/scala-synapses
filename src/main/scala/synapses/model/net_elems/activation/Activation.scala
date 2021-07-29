package synapses.model.net_elems.activation

import synapses.model.Mathematics

enum Activation(val f: Double => Double,
                val deriv: Double => Double,
                val inverse: Double => Double):

  case Sigmoid extends Activation(
    Mathematics.sigmoid,
    d => Mathematics.sigmoid(d) * (1.0 - Mathematics.sigmoid(d)),
    y => Math.log(y / (1.0 - y)))

  case Identity extends Activation(
    x => x,
    _ => 1.0,
    y => y)

  case Tanh extends Activation(
    Math.tanh,
    d => 1.0 - Math.tanh(d) * Math.tanh(d),
    y => 0.5 * Math.log((1.0 + y) / (1.0 - y)))

  case LeakyReLU extends Activation(
    x => if x < 0.0 then 0.01 * x else x,
    d => if d < 0.0 then 0.01 else 1.0,
    y => if y < 0.0 then y / 0.01 else y)
