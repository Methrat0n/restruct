package io.github.methrat0n.restruct.schema

import io.github.methrat0n.restruct.schema.Path.\

sealed trait Path

final case class PathCon[PreviousSteps <: Path, Step](previousSteps: PreviousSteps, step: Step) extends Path { self =>
  def \(step: String): PreviousSteps \ Step \ String =
    PathCon(self, step)
  def \(step: Int): PreviousSteps \ Step \ Int =
    PathCon(self, step)
  def as[Type]: SchemeInferer[Type, PreviousSteps, Step] =
    SchemeInferer(self)

  def asOption[Type, TypeInterpreter[Format[_]] <: Interpreter[Format, Type], Scheme <: Schema[Type, TypeInterpreter]](implicit schema: Scheme): OptionalField[PreviousSteps \ Step, Type, TypeInterpreter, Scheme] =
    OptionalField(self, schema, None)
}

final case class SchemeInferer[Type, PreviousSteps <: Path, Step](path: PathCon[PreviousSteps, Step]) {
  def apply[TypeInterpreter[Format[_]] <: Interpreter[Format, Type]]()(implicit schema: Schema[Type, TypeInterpreter]): RequiredField[PreviousSteps \ Step, Type, TypeInterpreter] =
    RequiredField(path, schema, None)
}

sealed trait PathNil extends Path
case object PathNil extends PathNil

object Path {
  def \(step: String): PathCon[PathNil, String] = PathCon(PathNil, step)
  def \(step: Int): PathCon[PathNil, Int] = PathCon(PathNil, step)

  type \[PreviousSteps <: Path, Step] = PathCon[PreviousSteps, Step]
  object \ {
    def apply[PreviousSteps <: Path, Step](previousSteps: PreviousSteps, step: Step): PreviousSteps \ Step = PathCon(previousSteps, step)
    def apply[Step](step: Step): PathNil \ Step = PathCon(PathNil, step)
  }

  /*implicit def forceInfer[
    Type,
    PreviousSteps <: Path,
    Step,
    TypeInterpreter[Format[_]] <: Interpreter[Format, Type],
    Scheme <: Schema[Type, TypeInterpreter]
  ](inferer: SchemeInferer[Type, PreviousSteps, Step]): RequiredField[PreviousSteps \ Step, Type, TypeInterpreter, Scheme] =
    inferer*/
}

// String / Int / String / PathNil
