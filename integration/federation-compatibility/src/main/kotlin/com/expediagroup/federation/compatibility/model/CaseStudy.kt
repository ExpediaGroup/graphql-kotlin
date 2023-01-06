package com.expediagroup.federation.compatibility.model

import com.expediagroup.graphql.generator.scalars.ID

/*
type CaseStudy {
  caseNumber: ID!
  description: String
}
 */
data class CaseStudy(
    val caseNumber: ID,
    val description: String?
)
