package synapses.model.net_elems.activation

type ActivationSerialized = String

object ActivationSerialized:

  def serialized(activation: Activation): ActivationSerialized =
    activation match
      case Activation.Sigmoid => "sigmoid"
      case Activation.Identity => "identity"
      case Activation.Tanh => "tanh"
      case Activation.LeakyReLU => "leakyReLU"
  
  def deserialized(activationSerialized: ActivationSerialized): Activation =
    activationSerialized match
      case "sigmoid" => Activation.Sigmoid
      case "identity" => Activation.Identity
      case "tanh" => Activation.Tanh
      case "leakyReLU" => Activation.LeakyReLU
