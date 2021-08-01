# scala-synapses

A lightweight library for **neural networks** written in **Scala 3**!

## Basic usage

### Install synapses

```scala
libraryDependencies += "com.github.mrdimosthenis" %% "synapses" % "8.0.0-RC3"
```

### Import the `Net` object

```scala mdoc:silent
import synapses.lib.Net
```

### Create a random neural network by providing its layer sizes

```scala mdoc:silent
val randNet = Net(List(2, 3, 1))
```

* Input layer: the first layer of the network has 2 nodes.
* Hidden layer: the second layer has 3 neurons.
* Output layer: the third layer has 1 neuron.

### Get the json of the random neural network

```scala mdoc:silent
randNet.json()
// res0: String = """[
//   [{"activationF" : "sigmoid", "weights" : [-0.5,0.1,0.8]},
//    {"activationF" : "sigmoid", "weights" : [0.7,0.6,-0.1]},
//    {"activationF" : "sigmoid", "weights" : [-0.8,-0.1,-0.7]}],
//   [{"activationF" : "sigmoid", "weights" : [0.5,-0.3,-0.4,-0.5]}]
// ]"""
```

### Create a neural network by providing its json

```scala mdoc:silent
val net = Net("""[
  [{"activationF" : "sigmoid", "weights" : [-0.5,0.1,0.8]},
   {"activationF" : "sigmoid", "weights" : [0.7,0.6,-0.1]},
   {"activationF" : "sigmoid", "weights" : [-0.8,-0.1,-0.7]}],
  [{"activationF" : "sigmoid", "weights" : [0.5,-0.3,-0.4,-0.5]}]
]""")
```

### Make a prediction

```scala mdoc
net.predict(List(0.2, 0.6))
```

### Train a neural network

```scala mdoc:silent
net.fit(
    learningRate = 0.1,
    inputValues = List(0.2, 0.6),
    expectedOutput = List(0.9)
)
```

The `fit` method returns the neural network with its weights adjusted to a single observation.

## Advanced usage

### Fully train a neural network

In practice, for a neural network to be fully trained, it should be fitted with multiple observations,
usually by folding over an iterator.

```scala mdoc:silent
Iterator(
    (List(0.2, 0.6), List(0.9)),
    (List(0.1, 0.8), List(0.2)),
    (List(0.5, 0.4), List(0.6))
).foldLeft(net){ case (acc, (xs, ys)) =>
    acc.fit(learningRate = 0.1, xs, ys)
}
```

### Boost the performance

Every function is efficient because its implementation is based on lazy list
and all information is obtained at a single pass.

For a neural network that has huge layers,
the performance of its `predict` and `fit` methods can be further improved
by using their parallel counterparts, `parPredict` and `parFit`.

### Create a neural network for testing

```scala mdoc:silent
Net(layerSizes = List(2, 3, 1), seed = 1000)
```

We can provide a `seed` to create a non-random neural network.
This way, we can use it for testing.

### Define the activation functions and the weights

```scala mdoc:silent
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

### Draw a neural network

```scala mdoc:silent
customNet.svg()
```

![Network Drawing](https://github.com/mrdimosthenis/scala-synapses/blob/master/neural_network.png?raw=true)

With its svg drawing, we can see what a neural network looks like .
The color of each neuron depends on its activation function
while the transparency of the synapses depends on their weight.

### Measure the difference between the expected and predicted values

```scala mdoc:silent
import synapses.lib.Stats
def expAndPredVals() =
    Iterator(
      (List(0.0, 0.0, 1.0), List(0.0, 0.1, 0.9)),
      (List(0.0, 1.0, 0.0), List(0.8, 0.2, 0.0)),
      (List(1.0, 0.0, 0.0), List(0.7, 0.1, 0.2)),
      (List(1.0, 0.0, 0.0), List(0.3, 0.3, 0.4)),
      (List(0.0, 0.0, 1.0), List(0.2, 0.2, 0.6))
    )
```

* Root-mean-square error

```scala mdoc
Stats.rmse(expAndPredVals())
```

* Classification accuracy score

```scala mdoc
Stats.score(expAndPredVals())
```

### Import the `Codec` object

```scala mdoc:silent
import synapses.lib.Codec
```

* One hot encoding is a process that turns discrete attributes into a list of 0.0 and 1.0.
* Minmax normalization scales continuous attributes into values between 0.0 and 1.0.

```scala mdoc:silent
val setosa = Map(
  "petal_length" -> "1.5",
  "petal_width" -> "0.1",
  "sepal_length" -> "4.9",
  "sepal_width" -> "3.1",
  "species" -> "setosa"
)

val versicolor = Map(
  "petal_length" -> "3.8",
  "petal_width" -> "1.1",
  "sepal_length" -> "5.5",
  "sepal_width" -> "2.4",
  "species" -> "versicolor"
)

val virginica = Map(
  "petal_length" -> "6.0",
  "petal_width" -> "2.2",
  "sepal_length" -> "5.0",
  "sepal_width" -> "1.5",
  "species" -> "virginica"
)

def dataset() = Iterator(setosa,versicolor,virginica)
```

You can use a `Codec` to encode and decode a data point.

### Create a `Codec` by providing attributes and the data points

```scala mdoc:silent
val codec = Codec(
    List(("petal_length", false),
         ("petal_width", false),
         ("sepal_length", false),
         ("sepal_width", false),
         ("species", true)),
    dataset()
)
```

* The first parameter is a list of pairs that define the name and the type (discrete or not) of each attribute.
* The second parameter is an iterator that contains the data points.

### Get the json of the codec

```scala mdoc:silent
val codecJson = codec.json()
// codecJson: String = """[
//   {"Case" : "SerializableContinuous",
//    "Fields" : [{"key" : "petal_length","min" : 1.5,"max" : 6.0}]},
//   {"Case" : "SerializableContinuous",
//    "Fields" : [{"key" : "petal_width","min" : 0.1,"max" : 2.2}]},
//   {"Case" : "SerializableContinuous",
//    "Fields" : [{"key" : "sepal_length","min" : 4.9,"max" : 5.5}]},
//   {"Case" : "SerializableContinuous",
//    "Fields" : [{"key" : "sepal_width","min" : 1.5,"max" : 3.1}]},
//   {"Case" : "SerializableDiscrete",
//    "Fields" : [{"key" : "species","values" : ["virginica","versicolor","setosa"]}]}
// ]"""
```

### Create a codec by providing its json

```scala mdoc:silent
Codec(codecJson)
```

### Encode a data point

```scala mdoc
val encodedSetosa = codec.encode(setosa)
```

### Decode a data point

```scala mdoc
codec.decode(encodedSetosa)
```
