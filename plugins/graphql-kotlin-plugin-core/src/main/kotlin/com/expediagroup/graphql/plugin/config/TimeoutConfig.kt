package com.expediagroup.graphql.plugin.config

import java.io.Serializable

/**
 * Timeout configuration for executing introspection query and downloading schema SDL.
 */
data class TimeoutConfig(
    /** Timeout in milliseconds to establish new connection. */
    var connect: Long = 5_000,
    /** Read timeout in milliseconds */
    var read: Long = 15_000
) : Serializable
