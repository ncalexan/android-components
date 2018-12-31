/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.service.glean

import kotlinx.coroutines.launch
import mozilla.components.service.glean.storages.CountersStorageEngine
import mozilla.components.support.base.log.logger.Logger

/**
 * This implements the developer facing API for recording counter metrics.
 *
 * Instances of this class type are automatically generated by the parsers at build time,
 * allowing developers to record values that were previously registered in the metrics.yaml file.
 *
 * The counter API only exposes the [add] method, which takes care of validating the input
 * data and making sure that limits are enforced.
 */
data class CounterMetricType(
    override val disabled: Boolean,
    override val category: String,
    override val lifetime: Lifetime,
    override val name: String,
    override val sendInPings: List<String>
) : CommonMetricData {

    override val defaultStorageDestinations: List<String> = listOf("metrics")

    private val logger = Logger("glean/CounterMetricType")

    /**
     * Add to counter value.
     *
     * @param amount This is the amount to increment the counter by, defaulting to 1 if called
     * without parameters.
     */
    fun add(amount: Int = 1) {
        // TODO report errors through other special metrics handled by the SDK. See bug 1499761.

        if (!shouldRecord(logger)) {
            return
        }

        if (amount <= 0) {
            logger.warn("Attempt to add a negative value to counter: $category.$name")
            return
        }

        Dispatchers.API.launch {
            // Delegate storing the new counter value to the storage engine.
            CountersStorageEngine.record(
                    this@CounterMetricType,
                    amount = amount
            )
        }
    }
}
