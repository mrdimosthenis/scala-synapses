package synapses.model.encoding

import io.circe._
import io.circe.generic.auto._
import io.circe.parser
import io.circe.syntax._
import scala.util.chaining._
import synapses.model.encoding.attribute.Attribute
import synapses.model.encoding.attribute.Attribute.Discrete
import synapses.model.encoding.attribute.Attribute.Continuous
import synapses.model.encoding.attribute.AttributeSerialized

type Preprocessor = LazyList[Attribute]

object Preprocessor:

  private def realize(preprocessor: Preprocessor): Preprocessor =
    preprocessor.map(Attribute.realize)
    preprocessor.force
    preprocessor

  def init(keysWithFlags: LazyList[(String, Boolean)],
           dataset: Iterator[Map[String, String]]): Preprocessor =
    val (heads, tail) = dataset.splitAt(1)
    val head = heads.next()
    val initPreprocessor = keysWithFlags.map {
      case (key, isDiscrete) =>
        if isDiscrete then
          Discrete(
            key,
            LazyList(head(key))
          )
        else {
          val v = Attribute.parse(head(key))
          Continuous(key, v, v)
        }
    }
    tail.foldLeft(initPreprocessor) { case (acc, x) =>
      acc
        .map(Attribute.updated(x))
        .pipe(realize)
    }

  def encode(datapoint: Map[String, String])
            (preprocessor: Preprocessor): LazyList[Double] =
    preprocessor
      .flatMap { attr =>
        Attribute.encode(datapoint(attr.key))(attr)
      }

  private def decodeAccF(acc: (LazyList[Double], LazyList[(String, String)]),
                         attr: Attribute)
  : (LazyList[Double], LazyList[(String, String)]) =
    val (unprocessedFloats, processedKsVs) = acc

    val (key, splitIndex) = attr match
      case attr: Discrete =>
        (attr.key, attr.values.length)
      case attr: Continuous =>
        (attr.key, 1)

    val (encodedValues, nextFloats) =
      unprocessedFloats.splitAt(splitIndex)
    val decodedValue = Attribute.decode(encodedValues)(attr)
    val nextKsVs = processedKsVs.prepended(key, decodedValue)
    (nextFloats, nextKsVs)

  def decode(encodedDatapoint: LazyList[Double])
            (preprocessor: Preprocessor): Map[String, String] =
    val initAcc = (encodedDatapoint, LazyList.empty[(String, String)])
    preprocessor
      .foldLeft(initAcc)(decodeAccF)
      ._2
      .toMap

  def toJson(preprocessor: Preprocessor): String =
    preprocessor
      .map(AttributeSerialized.serialized)
      .toList
      .asJson
      .spaces2

  def ofJson(json: String): Preprocessor =
    parser
      .parse(json)
      .toOption
      .get
      .as[List[Json]]
      .toOption
      .get
      .to(LazyList)
      .map(AttributeSerialized.deserialized)
