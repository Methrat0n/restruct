package io.github.methrat0n.restruct.handlers.bson

import io.github.methrat0n.restruct.core.data.schema.ComplexSchemaAlgebra
import io.github.methrat0n.restruct.readers.bson.bsonReader
import io.github.methrat0n.restruct.writers.bson.bsonWriter

trait ComplexBsonFormaterInterpreter extends ComplexSchemaAlgebra[BsonHandler] {

  private[this] val reader = bsonReader
  private[this] val writer = bsonWriter

  override def many[T](schema: BsonHandler[T]): BsonHandler[List[T]] =
    BsonHandler(
      reader.many(schema).read,
      writer.many(schema).write
    )

}
