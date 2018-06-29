package restruct.enumeratum.data.constraints

import enumeratum.Enum
import lib.core.data.constraints.Constraint

object EnumeratumConstraints {
  final case class EnumConstraint[E <: Enum[_]](enum: E) extends Constraint[String] {
    override def name: String = "enum"

    override def args: Seq[Any] = Seq(enum.namesToValuesMap.keys.toList)

    override def validate(value: String): Boolean = enum.withNameOption(value).isDefined
  }
}
