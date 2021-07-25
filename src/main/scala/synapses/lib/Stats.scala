package synapses.lib

import scala.util.chaining._
import synapses.model.Mathematics

object Stats:

  def rmse(outputPairs: Iterator[(List[Double], List[Double])]): Double =
    outputPairs
      .map { case (yHat, y) =>
        (yHat.to(LazyList), y.to(LazyList))
      }
      .pipe(Mathematics.rootMeanSquareError)

  def score(outputPairs: Iterator[(List[Double], List[Double])]): Double =
    outputPairs
      .map { case (yHat, y) =>
        (yHat.to(LazyList), y.to(LazyList))
      }
      .pipe(Mathematics.accuracy)
