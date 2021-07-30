# scala-synapses

A lightweight library for **neural networks** written in **Scala**!

## Basic usage

* Add this line to your `build.sbt` file

```scala
libraryDependencies += "com.github.mrdimosthenis" %% "synapses" % "8.0.0-RC3"
```

* Import the `Net` object

```scala mdoc
import synapses.lib.Net
```

* Create a random neural network by providing its layer sizes

```scala mdoc
val randNet = Net(List(2, 3, 1))
```

The first (input) layer of the network has 2 nodes,
the second (hidden) layer has 3 neurons,
and the third (output) layer has 1 neurons.

* Get the JSON representation of the random neural network

```scala mdoc
randNet.json()
```

* Create a neural network by providing its JSON representation

```scala mdoc
val net = Net("""[
   [
     {
       "activationF" : "sigmoid",
       "weights" : [-0.5,0.1,0.8]
     },
     {
       "activationF" : "sigmoid",
       "weights" : [0.7,0.6,-0.1]
     },
     {
       "activationF" : "sigmoid",
       "weights" : [-0.8,-0.1,-0.7]
     }
   ],
   [
     {
       "activationF" : "sigmoid",
       "weights" : [0.5,-0.3,-0.4,-0.5]
     }
   ]
 ]""")
```

`net` has the same layer sizes as `randNet`, but different weights.

* Make a prediction

```scala mdoc
net.predict(List(0.2, 0.6))
```

* Train a neural network

```scala mdoc
val fitNet = net.fit(
    learningRate = 0.1,
    inputValues = List(0.2, 0.6),
    expectedOutput = List(0.9)
)
```

`fitNet` is the updated `net`, fitted with a single observation.

For a neural network to be trained, it should be fitted with multiple observations,
usually by folding over an iterator.

```scala mdoc
Iterator(
    (List(0.2, 0.6), List(0.9)),
    (List(0.1, 0.8), List(0.2)),
    (List(0.5, 0.4), List(0.6))
).foldLeft(net){ case (acc, (xs, ys)) =>
    acc.fit(learningRate = 0.1, xs, ys)
}
```
