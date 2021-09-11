package synapses.lib

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import synapses.model.net_elems.activation.Activation

type Fun = Activation

/** The activation functions a neuron can have.
 *
 * They can be used in the arguments of neural network's constructor.
 *
 * {{{
 *  Net(List(2, 3, 1), _ => Fun.sigmoid, _ => Random().nextDouble())
 * }}}
 *
 * {{{
 *  Net(List(4, 6, 8, 5, 3), _ => Fun.identity, _ => Random().nextDouble())
 * }}}
 *
 * {{{
 *  Net(List(4, 8, 3), _ => Fun.tanh, _ => Random().nextDouble())
 * }}}
 *
 * {{{
 *  Net(List(2, 1), _ => Fun.leakyReLU, _ => Random().nextDouble())
 * }}}
 */
@JSExportTopLevel("FunJs")
object Fun:

  /** Sigmoid takes any real value as input and outputs values in the range of 0 to 1.
   *
   * {{{
   *  x => 1.0 / (1.0 + Math.exp(-x))
   * }}}
   */
  @JSExport
  val sigmoid: Fun = Activation.Sigmoid


  /** Identity is a linear function where the output is equal to the input.
   *
   * {{{
   *  x => x
   * }}}
   */
  @JSExport
  val identity: Fun = Activation.Identity

  /** Tanh is similar to Sigmoid, but outputs values in the range of -1 and 1.
   *
   * {{{
   *  x => Math.tanh(x)
   * }}}
   */
  @JSExport
  val tanh: Fun = Activation.Tanh


  /** LeakyReLU gives a small proportion of x if x is negative and x otherwise.
   *
   * {{{
   *  x => if x < 0.0 then 0.01 * x else x
   * }}}
   */
  @JSExport
  val leakyReLU: Fun = Activation.LeakyReLU
