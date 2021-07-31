# scala-synapses

A lightweight library for **neural networks** written in **Scala 3**!

## Basic usage

#### Install synapses

```scala
libraryDependencies += "com.github.mrdimosthenis" %% "synapses" % "8.0.0-RC3"
```

#### Import the `Net` object

```scala
import synapses.lib.Net
```

#### Create a random neural network by providing its layer sizes

```scala
val randNet = Net(List(2, 3, 1))
```

The first (input) layer of the network has 2 nodes,
the second (hidden) layer has 3 neurons,
and the third (output) layer has 1 neurons.

#### Get the json of the random neural network

```scala
randNet.json()
// res0: String = """[
//   [{"activationF" : "sigmoid", "weights" : [-0.5,0.1,0.8]},
//    {"activationF" : "sigmoid", "weights" : [0.7,0.6,-0.1]},
//    {"activationF" : "sigmoid", "weights" : [-0.8,-0.1,-0.7]}],
//   [{"activationF" : "sigmoid", "weights" : [0.5,-0.3,-0.4,-0.5]}]
// ]"""
```

#### Create a neural network by providing its json

```scala
val net = Net("""[
  [{"activationF" : "sigmoid", "weights" : [-0.5,0.1,0.8]},
   {"activationF" : "sigmoid", "weights" : [0.7,0.6,-0.1]},
   {"activationF" : "sigmoid", "weights" : [-0.8,-0.1,-0.7]}],
  [{"activationF" : "sigmoid", "weights" : [0.5,-0.3,-0.4,-0.5]}]
]""")
```

#### Make a prediction

```scala
net.predict(List(0.2, 0.6))
// res1: List[Double] = List(0.49131100324012494)
```

#### Train a neural network

```scala
net.fit(
    learningRate = 0.1,
    inputValues = List(0.2, 0.6),
    expectedOutput = List(0.9)
)
```

The `fit` method returns the neural network with its weights adjusted to a single observation.

## Advanced usage

### Neural Networks

#### Fully train a neural network

In practice, for a neural network to be fully trained, it should be fitted with multiple observations,
usually by folding over an iterator.

```scala
Iterator(
    (List(0.2, 0.6), List(0.9)),
    (List(0.1, 0.8), List(0.2)),
    (List(0.5, 0.4), List(0.6))
).foldLeft(net){ case (acc, (xs, ys)) =>
    acc.fit(learningRate = 0.1, xs, ys)
}
```

#### Boost the performance

Every function is efficient because its implementation is based on lazy list
and all information is obtained at a single pass.

For a neural network that has huge layers,
the performance of its `predict` and `fit` methods can be further improved
by using their parallel counterparts, `parPredict` and `parFit`.

#### Create a neural network for testing

```scala
Net(layerSizes = List(2, 3, 1), seed = 1000)
```

We can provide a `seed` to create a non-random neural network.
This way, we can use it for testing.

#### Define the activation functions and the weights

```scala
import scala.util.Random
import synapses.lib.Fun

def activationF(layerIndex: Int): Fun =
    layerIndex match
      case 0 => Fun.sigmoid
      case 1 => Fun.identity
      case 2 => Fun.leakyReLU
      case 3 => Fun.tanh

def weightInitF(layerIndex: Int): Double =
    (layerIndex + 1) * (1.0 - 2.0 * Random().nextDouble())

val customNet = Net(layerSizes = List(4, 6, 8, 5, 3), activationF, weightInitF)
```

* The `activationF` function accepts the index of a layer and returns an activation function for its neurons.
* The `weightInitF` function accepts the index of a layer and returns a weight for the synapses of its neurons.

If we don't provide these functions, the activation function of all neurons is sigmoid,
and the weight distribution of the synapses is normal between -1.0 and 1.0.

#### Draw a neural network

```scala
customNet.svg()
```

![Network Drawing](https://github.com/mrdimosthenis/scala-synapses/blob/master/neural_network.png?raw=true)

With its svg drawing, we can see what a neural network looks like .
The color of each neuron depends on its activation function
while the transparency of the synapses depends on their weight.
