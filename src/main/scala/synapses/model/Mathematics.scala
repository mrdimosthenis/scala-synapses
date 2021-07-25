package synapses.model

import scala.util.chaining._

object Mathematics:

  def sigmoid(x: Double): Double =
    1.0 / (1.0 + Math.exp(-x))

  def dotProduct(xs: LazyList[Double], ys: LazyList[Double]): Double =
    xs.zip(ys)
      .map { case (x, y) => x * y }
      .sum

  def euclideanDistance(xs: LazyList[Double], ys: LazyList[Double]): Double =
    xs.zip(ys)
      .map { case (x, y) => (x - y) * (x - y) }
      .sum
      .pipe(Math.sqrt)

  def rootMeanSquareError(yHatsWithYs: Iterator[(LazyList[Double], LazyList[Double])])
  : Double =
    val (n, s) =
      yHatsWithYs
        .map { case (y_hat, y) =>
          val d = euclideanDistance(y_hat, y)
          d * d
        }
        .foldLeft((0, 0.0)) { case ((accN, accS), sd) =>
          (accN + 1, accS + sd)
        }
    Math.sqrt(s / n)

  def accuracy(yHatsWithYs: Iterator[(LazyList[Double], LazyList[Double])])
  : Double =
    def indexOfMaxVal(ys: LazyList[Double]) =
      ys.zipWithIndex.maxBy(_._1)._2
    val (n, s) =
      yHatsWithYs
        .map { case (y_hat, y) =>
          indexOfMaxVal(y_hat) == indexOfMaxVal(y)
        }
        .foldLeft((0, 0)) { case ((accN, accS), s) =>
          val newS =
            if s then
              accS + 1
            else
              accS
          (accN + 1, newS)
        }
    s.toDouble / n.toDouble
