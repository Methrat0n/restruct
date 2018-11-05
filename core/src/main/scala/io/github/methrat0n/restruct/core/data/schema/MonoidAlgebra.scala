package io.github.methrat0n.restruct.core.data.schema

import scala.language.higherKinds

trait MonoidAlgebra[F[_]] extends SemiGroupalAlgebra[F] with IdentityAlgebra[F]