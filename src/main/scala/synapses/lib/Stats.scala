package synapses.lib

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.scalajs.js.JSConverters._
import scala.util.chaining._
import synapses.model.Mathematics

/** Measure the difference between the values predicted by a neural network and the observed values.
 *
 * Calculate the root mean square error:
 * {{{
 *  Stats.rmse(
 *    Iterator(
 *      (List(0.0, 0.0, 1.0), List(0.0, 0.0, 1.0)),
 *      (List(0.0, 0.0, 1.0), List(0.0, 1.0, 1.0))
 *    )
 *  )
 * }}}
 *
 * Calculate the score of the classification accuracy:
 * {{{
 *  Stats.score(
 *    Iterator(
 *      (List(0.0, 0.0, 1.0), List(0.0, 0.1, 0.9)),
 *      (List(0.0, 1.0, 0.0), List(0.8, 0.2, 0.0)),
 *      (List(1.0, 0.0, 0.0), List(0.7, 0.1, 0.2)),
 *      (List(1.0, 0.0, 0.0), List(0.3, 0.3, 0.4)),
 *      (List(0.0, 0.0, 1.0), List(0.2, 0.2, 0.6))
 *      (List(0.0, 0.0, 1.0), List(0.0, 0.1, 0.9)),
 *      (List(0.0, 1.0, 0.0), List(0.8, 0.2, 0.0)),
 *      (List(1.0, 0.0, 0.0), List(0.7, 0.1, 0.2)),
 *      (List(1.0, 0.0, 0.0), List(0.3, 0.3, 0.4)),
 *      (List(0.0, 0.0, 1.0), List(0.2, 0.2, 0.6))
 *    )
 *  )
 * }}}
 */
@JSExportTopLevel("StatsJs")
object Stats:

  /** Root Mean Square Error.
   *
   * RMSE is the standard deviation of the prediction errors.
   *
   * @param outputPairs An iterator of pairs that contain the expected and predicted values.
   * @return The value of the RMSE metric.
   */
  @JSExport
  def rmse(outputPairs: js.Iterable[js.Array[js.Array[Double]]]): Double =
    outputPairs
      .to(Iterator)
      .map{ arr =>
        (arr(0).to(LazyList), arr(1).to(LazyList))
      }
      .pipe(Mathematics.rootMeanSquareError)

  /** Classification Accuracy.
   *
   * The ratio of number of correct predictions to the total number of provided pairs.
   * For a prediction to be considered as correct, the index of its maximum expected value
   * needs to be the same with the index of its maximum predicted value.
   *
   * @param outputPairs An iterator of pairs that contain the expected and predicted values.
   * @return The score of the classification accuracy.
   */
  @JSExport
  def score(outputPairs: js.Iterable[js.Array[js.Array[Double]]]): Double =
    outputPairs
      .to(Iterator)
      .map{ arr =>
        (arr(0).to(LazyList), arr(1).to(LazyList))
      }
      .pipe(Mathematics.accuracy)
