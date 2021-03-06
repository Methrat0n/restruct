package io.github.methrat0n.restruct.schema

import io.github.methrat0n.restruct.constraints.Constraint

import scala.annotation.implicitNotFound
import scala.collection.Iterable

sealed trait Interpreter[+Format[_], +Type]

object Interpreter {

  @implicitNotFound("""
  Cannot find an interpreter for ${Type}
""")
  trait SimpleInterpreter[Format[_], Type] extends Interpreter[Format, Type] {
    def schema: Format[Type]
  }

  @implicitNotFound("""
  Cannot find an interpreter for collection ${Collection} of ${Type}
""")
  trait ManyInterpreter[Format[_], Type, Collection[A] <: Iterable[A], UnderlyingInterpreter <: Interpreter[Format, Type]] extends Interpreter[Format, Collection[Type]] {
    def originalInterpreter: UnderlyingInterpreter
    def many(schema: Format[Type]): Format[Collection[Type]]
  }

  @implicitNotFound("""
  Cannot find an interpreter for ${Type}.
  Maybe the format you ask for does not support the path you used (see documentation for this format).
""")
  trait RequiredInterpreter[Format[_], P <: Path, Type, UnderlyingInterpreter <: Interpreter[Format, Type]] extends Interpreter[Format, Type] {
    def originalInterpreter: UnderlyingInterpreter
    def required(path: P, schema: Format[Type], default: Option[Type]): Format[Type]
  }

  @implicitNotFound("""
  Cannot find an interpreter for ${Type}.
  Maybe the format you ask for does not support the path you used (see documentation for this format).
""")
  trait OptionalInterpreter[Format[_], P <: Path, Type, UnderlyingInterpreter <: Interpreter[Format, Type]] extends Interpreter[Format, Option[Type]] {
    def originalInterpreter: UnderlyingInterpreter
    def optional(path: P, schema: Format[Type], default: Option[Option[Type]]): Format[Option[Type]]
  }

  trait ConstrainedInterpreter[Format[_], Type, UnderlyingInterpreter <: Interpreter[Format, Type]] extends Interpreter[Format, Type] {
    def originalInterpreter: UnderlyingInterpreter
    def verifying(schema: Format[Type], constraint: Constraint[Type]): Format[Type]

    def verifying(schema: Format[Type], constraint: List[Constraint[Type]]): Format[Type] =
      constraint.foldLeft(schema)((schema, constraint) => verifying(schema, constraint))
  }

  @implicitNotFound("""
  Cannot find an interpreter for Either[${A}, ${B}].
  It needs the interpreters for ${A} and ${B}, maybe their missing ?
  Or maybe your path isn't supported by the format you want (see documentation for this format).
""")
  trait OneOfInterpreter[Format[_], A, B, AInterpreter <: Interpreter[Format, A], BInterpreter <: Interpreter[Format, B]] extends Interpreter[Format, Either[A, B]] {
    def originalInterpreterA: AInterpreter
    def originalInterpreterB: BInterpreter
    /**
     * Should return a success, if any, or concatenate errors.
     *
     * fa == sucess => fa result in Left
     * fa == error && fb == sucess => fb result in Right
     * fa == error && fb == error => concatenate fa and fb errors into F error handling
     *
     * If two successes are found, fa will be choosen.
     *
     * @return F in error (depends on the implementing F) or successful F with one of the two value
     */
    def or(fa: Format[A], fb: Format[B]): Format[Either[A, B]]
  }

  @implicitNotFound("""
  Cannot find an interpreter for ${A} with ${B}.
  It needs the interpreters for ${A} and ${B}, maybe their missing ?
  Or maybe your path isn't supported by the format you want (see documentation for this format).
""")
  trait InvariantInterpreter[Format[_], A, B, UnderlyingInterpreter <: Interpreter[Format, A]] extends Interpreter[Format, B] {
    def underlyingInterpreter: UnderlyingInterpreter
    def imap(fa: Format[A])(f: A => B)(g: B => A): Format[B]
  }

  @implicitNotFound("""
  Cannot find an interpreter for (${A}, ${B}).
  It needs the interpreters for ${A} and ${B}, maybe their missing ?
  Or maybe your path isn't supported by the format you want (see documentation for this format).
""")
  trait SemiGroupalInterpreter[Format[_], A, B, AInterpreter <: Interpreter[Format, A], BInterpreter <: Interpreter[Format, B]] extends Interpreter[Format, (A, B)] {
    def originalInterpreterA: AInterpreter
    def originalInterpreterB: BInterpreter
    def product(fa: Format[A], fb: Format[B]): Format[(A, B)]
  }
}
