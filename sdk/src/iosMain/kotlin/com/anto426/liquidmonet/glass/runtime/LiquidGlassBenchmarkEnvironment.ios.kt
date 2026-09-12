package com.anto426.liquidmonet.glass.runtime

import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationDidEnterBackgroundNotification
import platform.UIKit.UIApplicationDidReceiveMemoryWarningNotification
import platform.UIKit.UIApplicationState.UIApplicationStateActive
import platform.darwin.NSObjectProtocol

/** Register/remove on Main; callbacks only invalidate the worker's current run. */
@OptIn(ExperimentalAtomicApi::class)
internal class LiquidGlassIosBenchmarkEnvironment private constructor() : AutoCloseable {
    private val valid = AtomicInt(if (UIApplication.sharedApplication.applicationState == UIApplicationStateActive) 1 else 0)
    private val notifications = NSNotificationCenter.defaultCenter
    private val observers: List<NSObjectProtocol> = listOfNotNull(
        UIApplicationDidEnterBackgroundNotification, UIApplicationDidReceiveMemoryWarningNotification
    ).map { name ->
        notifications.addObserverForName(name, null, NSOperationQueue.mainQueue) { valid.store(0) }
    }

    fun checkSafe() = check(valid.load() == 1 && liquidGlassIosCanBenchmark()) { "Device is no longer ready for GPU work" }

    override fun close() { observers.forEach { notifications.removeObserver(it) } }

    companion object { fun start() = LiquidGlassIosBenchmarkEnvironment() }
}
