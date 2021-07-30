# scala-synapses

A lightweight library for **neural networks** written in **Scala**!

## Basic usage

* Add this line to your `build.sbt` file

```scala
libraryDependencies += "com.github.mrdimosthenis" %% "synapses" % "8.0.0-RC3"
```

* Import the `Net` object

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
//         LazyList(-0.5992706028339825, 0.8539319792607571, 0.22531536843483813)
//       ),
//       Neuron(
//         Sigmoid,
//         LazyList(0.2823930892827673, -0.14616485389053446, -0.5255789342250927)
//       ),
//       Neuron(
//         Sigmoid,
//         LazyList(0.43834833148387076, -0.0961605440612181, -0.6965642461777182)
//       )
//     ),
//     LazyList(
//       Neuron(
//         Sigmoid,
//         LazyList(
//           0.09431404104679508,
//           0.6095080337477099,
//           -0.6872254795147608,
//           -0.15132082995925944
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
//         -0.5992706028339825,
//         0.8539319792607571,
//         0.22531536843483813
//       ]
//     },
//     {
//       "activationF" : "sigmoid",
//       "weights" : [
//         0.2823930892827673,
//         -0.14616485389053446,
//         -0.5255789342250927
//       ]
//     },
//     {
//       "activationF" : "sigmoid",
//       "weights" : [
//         0.43834833148387076,
//         -0.0961605440612181,
//         -0.6965642461777182
//       ]
//     }
//   ],
//   [
//     {
//       "activationF" : "sigmoid",
//       "weights" : [
//         0.09431404104679508,
//         0.6095080337477099,
//         -0.6872254795147608,
//         -0.15132082995925944
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
