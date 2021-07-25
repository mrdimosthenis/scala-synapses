package synapses.lib

import scala.util.chaining._
import synapses.model.encoding.attribute.Attribute
import synapses.model.encoding.Preprocessor

case class Codec(attributes: LazyList[Attribute]):

  def encode(datapoint: Map[String, String]): List[Double] =
    Preprocessor
      .encode(datapoint)(attributes)
      .toList

  def decode(encodedValues: List[Double]): Map[String, String] =
    encodedValues
      .to(LazyList)
      .pipe(Preprocessor.decode(_)(attributes))

  def json(): String =
    Preprocessor.toJson(attributes)

object Codec:

  def apply(keysWithFlags: List[(String, Boolean)],
            datapoints: Iterator[Map[String, String]]): Codec =
    keysWithFlags
      .to(LazyList)
      .pipe(Preprocessor.init(_, datapoints))
      .pipe(Codec.apply)

  def apply(json: String): Codec =
    Preprocessor.ofJson(json)
      .pipe(Codec.apply)
