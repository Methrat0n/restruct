package io.github.methrat0n.restruct.constraints

trait Constraint[T] {
  def name: String = this.getClass.getSimpleName
  def args: Seq[Any]
  def validate(value: T): Boolean
}
