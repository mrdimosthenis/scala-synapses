# scala-synapses

A lightweight library for **neural networks** written in **Scala**!

## Basic usage

* Add this line to your `build.sbt` file

```scala
libraryDependencies += "com.github.mrdimosthenis" %% "synapses" % "8.0.0-RC3"
```

* Import the 'Net' object

```scala
import synapses.lib.Net
```

* Create a random neural network by providing its layer sizes

```scala
val randNet = Net(List(2, 3, 1))
// randNet: Net = Net(
//   LazyList(
//     LazyList(
//       Neuron(
//         Sigmoid,
//         LazyList(-0.3639204723480316, -0.7024477376385172, 0.7346484622397325)
//       ),
//       Neuron(
//         Sigmoid,
//         LazyList(-0.6788939084279098, 0.19323083498812688, 0.015719604222974803)
//       ),
//       Neuron(
//         Sigmoid,
//         LazyList(-0.16311906882083993, 0.7729624115687699, -0.45176100652397233)
//       )
//     ),
//     LazyList(
//       Neuron(
//         Sigmoid,
//         LazyList(
//           -0.5648689796756128,
//           -0.16336827187391112,
//           0.11075765899730028,
//           0.9294639295190656
//         )
//       )
//     )
//   )
// )
```

The first (input) layer of the network has 2 nodes,
the second (hidden) layer has 3 neurons,
and the third (output) layer has 1 neurons.

* Get the JSON representation of the random neural network

```scala
randNet.json()
// res0: String = """[
//   [
//     {
//       "activationF" : "sigmoid",
//       "weights" : [
//         -0.3639204723480316,
//         -0.7024477376385172,
//         0.7346484622397325
//       ]
//     },
//     {
//       "activationF" : "sigmoid",
//       "weights" : [
//         -0.6788939084279098,
//         0.19323083498812688,
//         0.015719604222974803
//       ]
//     },
//     {
//       "activationF" : "sigmoid",
//       "weights" : [
//         -0.16311906882083993,
//         0.7729624115687699,
//         -0.45176100652397233
//       ]
//     }
//   ],
//   [
//     {
//       "activationF" : "sigmoid",
//       "weights" : [
//         -0.5648689796756128,
//         -0.16336827187391112,
//         0.11075765899730028,
//         0.9294639295190656
//       ]
//     }
//   ]
// ]"""
```

* Create a neural network by providing its JSON representation

```scala
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
// net: Net = Net(
//   LazyList(
//     LazyList(
//       Neuron(Sigmoid, LazyList(-0.5, 0.1, 0.8)),
//       Neuron(Sigmoid, LazyList(0.7, 0.6, -0.1)),
//       Neuron(Sigmoid, LazyList(-0.8, -0.1, -0.7))
//     ),
//     LazyList(Neuron(Sigmoid, LazyList(0.5, -0.3, -0.4, -0.5)))
//   )
// )
```

`net` has the same layer sizes as `randNet`, but different weights.

* Make a prediction

```scala
net.predict(List(0.2, 0.6))
// res1: List[Double] = List(0.49131100324012494)
```

* Train a neural network

```scala
val fitNet = net.fit(
    learningRate = 0.1,
    inputValues = List(0.2, 0.6),
    expectedOutput = List(0.9)
)
// fitNet: Net = Net(
//   LazyList(
//     LazyList(
//       Neuron(
//         Sigmoid,
//         LazyList(-0.4987232325785445, 0.1002553534842911, 0.8007660604528734)
//       ),
//       Neuron(
//         Sigmoid,
//         LazyList(0.7015109701815497, 0.6003021940363099, -0.09909341789107015)
//       ),
//       Neuron(
//         Sigmoid,
//         LazyList(-0.7996009710421346, -0.09992019420842692, -0.6997605826252806)
//       )
//     ),
//     LazyList(
//       Neuron(
//         Sigmoid,
//         LazyList(
//           0.5102141393716438,
//           -0.29489293031417807,
//           -0.39304055800168897,
//           -0.49770757956173206
//         )
//       )
//     )
//   )
// )
```

`fitNet` is the updated `net`, fitted with a single observation.

For a neural network to be trained, it should be fitted with multiple observations,
usually by folding over an iterator.

```scala
Iterator(
    (List(0.2, 0.6), List(0.9)),
    (List(0.1, 0.8), List(0.2)),
    (List(0.5, 0.4), List(0.6))
).foldLeft(net){ case (acc, (xs, ys)) =>
    acc.fit(learningRate = 0.1, xs, ys)
}
// res2: Net = Net(
//   LazyList(
//     LazyList(
//       Neuron(
//         Sigmoid,
//         LazyList(-0.49939244729968957, 0.10031837690581497, 0.8001007437823189)
//       ),
//       Neuron(
//         Sigmoid,
//         LazyList(0.7008135150375799, 0.6003940375743398, -0.09981297105867289)
//       ),
//       Neuron(
//         Sigmoid,
//         LazyList(-0.799718797676025, -0.09988166956726867, -0.6999051512369404)
//       )
//     ),
//     LazyList(
//       Neuron(
//         Sigmoid,
//         LazyList(
//           0.5055696838216472,
//           -0.297587306484989,
//           -0.39596102720562515,
//           -0.49853333418344103
//         )
//       )
//     )
//   )
// )
```
