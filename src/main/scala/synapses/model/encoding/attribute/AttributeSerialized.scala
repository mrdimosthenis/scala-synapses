package synapses.model.encoding.attribute

import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import scala.util.chaining._
import synapses.model.encoding.attribute.Attribute
import synapses.model.encoding.attribute.Attribute.Discrete
import synapses.model.encoding.attribute.Attribute.Continuous

object AttributeSerialized:

  def serialized(attribute: Attribute): Json =
    attribute match

      case Discrete(s: String, values: LazyList[String]) =>
        Map(
          "Case" -> "SerializableDiscrete".asJson,
          "Fields" -> List(
            Map(
              "key" -> s.asJson,
              "values" -> values.toList.asJson
            )
          ).asJson
        ).asJson

      case Continuous(s: String, min: Double, max: Double) =>
        Map(
          "Case" -> "SerializableContinuous".asJson,
          "Fields" -> List(
            Map(
              "key" -> s.asJson,
              "min" -> min.asJson,
              "max" -> max.asJson
            )
          ).asJson
        ).asJson

  def deserialized(json: Json): Attribute =
    val cursor = json.hcursor
    val attrCase = cursor.get[String]("Case").toOption.get
    val field = cursor.downField("Fields").downArray
    val s = field.get[String]("key").toOption.get
    attrCase match
      case "SerializableDiscrete" =>
        field
          .get[List[String]]("values")
          .toOption
          .get
          .to(LazyList)
          .pipe(Discrete(s, _))
      case "SerializableContinuous" =>
        val min = field.get[Double]("min").toOption.get
        val max = field.get[Double]("max").toOption.get
        Continuous(s, min, max)
