package synapses.lib

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.scalajs.js.JSConverters._
import scala.util.chaining.*
import synapses.model.encoding.attribute.Attribute
import synapses.model.encoding.Preprocessor

/** The methods of a codec.
 *
 * Encode a data point:
 * {{{
 *  codec.encode(Map("petal_length" -> "1.5","species" -> "setosa"))
 * }}}
 *
 * Decode a data point:
 * {{{
 *  codec.decode(List(0.0, 1.0, 0.0))
 * }}}
 *
 * Get the JSON representation of the codec:
 * {{{
 *  codec.json()
 * }}}
 */
@JSExportTopLevel("CodecJs")
case class Codec(attributes: LazyList[Attribute]):

  /** Encodes a data point.
   *
   * @param datapoint A data point as a map of strings.
   * @return The encoded data point as a list of numbers between 0.0 and 1.0.
   */
  @JSExport
  def encode(datapoint: js.Dictionary[String]): js.Array[Double] =
    Preprocessor
      .encode(datapoint.toMap)(attributes)
      .toJSArray

  /** Decodes a data point.
   *
   * @param encodedValues An encoded data point as a list of numbers between 0.0 and 1.0.
   * @return The decoded data point as a map of strings.
   */
  @JSExport
  def decode(encodedValues: js.Array[Double]): js.Dictionary[String] =
    encodedValues
      .to(LazyList)
      .pipe(Preprocessor.decode(_)(attributes))
      .toJSDictionary

  /** The JSON representation of the codec.
   *
   * @return The JSON representation of the codec.
   */
  @JSExport
  def json(): String =
    Preprocessor.toJson(attributes)

/** The constructors of a codec.
 *
 * One hot encoding is a process that turns discrete attributes into a list of 0.0 and 1.0.
 * Minmax normalization scales continuous attributes into values between 0.0 and 1.0.
 *
 * A codec can encode and decode every data point.
 *
 * There are two ways to create a codec:
 *
 * 1. By providing a list of pairs that define the name and the type of each attribute:
 * {{{
 *  val codec = Codec(
 *    List( ("petal_length", false),
 *          ("species", true) ),
 *    Iterator(Map("petal_length" -> "1.5",
 *                 "species" -> "setosa"),
 *             Map("petal_length" -> "3.8",
 *                 "species" -> "versicolor"))
 *  )
 * }}}
 *
 * 2. By providing its JSON representation.
 * {{{
 *  val codec = Codec(
 *    """[{"Case":"SerializableContinuous",
 *         "Fields":[{"key":"petal_length","min":1.5,"max":3.8}]},
 *        {"Case":"SerializableDiscrete",
 *         "Fields":[{"key":"species","values":["setosa","versicolor"]}]}]"""
 *  )
 * }}}
 */
@JSExportTopLevel("CodecJsObj")
object Codec:

  /** Creates a codec by consuming an iterator of data points.
   *
   * @param attributesWithFlag A list of pairs that define the name and the type (discrete or not) of each attribute.
   * @param datapoints         An iterator that contains the data points.
   * @return A codec that can encode and decode every data point.
   */
  @JSExport("apply")
  def apply[A](attributesWithFlag: js.Array[js.Array[A]],
               datapoints: js.Iterable[js.Dictionary[String]]): Codec =
    attributesWithFlag
      .to(LazyList)
      .map { arr =>
        (arr(0).asInstanceOf[String], arr(1).asInstanceOf[Boolean])
      }
      .pipe(Preprocessor.init(_, datapoints.map(_.toMap).to(Iterator)))
      .pipe(Codec.apply)

  /** Parses a codec.
   *
   * @param json The JSON representation of a codec.
   * @return A codec that can encode and decode every data point.
   */
  @JSExport("applyJson")
  def apply(json: String): Codec =
    Preprocessor.ofJson(json)
      .pipe(Codec.apply)
