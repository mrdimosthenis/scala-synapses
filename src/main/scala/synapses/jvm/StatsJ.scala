package synapses.jvm

import scala.jdk.StreamConverters._
import scala.jdk.FunctionConverters._
import scala.jdk.CollectionConverters._
import scala.util.chaining._
import synapses.lib.Stats

object StatsJ:

  private def toScalaOutputPairs(outputPairs: java.util.stream.Stream[Array[Array[Double]]])
  : Iterator[(List[Double], List[Double])] =
    outputPairs
      .toScala(Iterator)
      .map { arr =>
        (arr(0).toList, arr(1).toList)
      }

  def rmse(outputPairs: java.util.stream.Stream[Array[Array[Double]]]): Double =
    outputPairs
      .pipe(toScalaOutputPairs)
      .pipe(Stats.rmse)

  def score(outputPairs: java.util.stream.Stream[Array[Array[Double]]]): Double =
    outputPairs
      .pipe(toScalaOutputPairs)
      .pipe(Stats.score)
