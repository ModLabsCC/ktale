package cc.modlabs.ktale.ext

import com.hypixel.hytale.math.vector.Vector3d
import com.hypixel.hytale.math.vector.Vector3f
import com.hypixel.hytale.math.vector.Vector3i

// ── Vector3d operators ──────────────────────────────────────────────

operator fun Vector3d.plus(other: Vector3d): Vector3d =
    Vector3d(x + other.x, y + other.y, z + other.z)

operator fun Vector3d.minus(other: Vector3d): Vector3d =
    Vector3d(x - other.x, y - other.y, z - other.z)

operator fun Vector3d.times(scalar: Double): Vector3d =
    Vector3d(x * scalar, y * scalar, z * scalar)

operator fun Vector3d.div(scalar: Double): Vector3d =
    Vector3d(x / scalar, y / scalar, z / scalar)

operator fun Vector3d.unaryMinus(): Vector3d =
    Vector3d(-x, -y, -z)

fun Vector3d.lengthSquared(): Double = x * x + y * y + z * z

fun Vector3d.normalized(): Vector3d {
    val len = length()
    return if (len == 0.0) this else this / len
}

fun Vector3d.distanceTo(other: Vector3d): Double = (this - other).length()

fun Vector3d.distanceSquaredTo(other: Vector3d): Double = (this - other).lengthSquared()

fun Vector3d.dot(other: Vector3d): Double =
    x * other.x + y * other.y + z * other.z

fun Vector3d.cross(other: Vector3d): Vector3d = Vector3d(
    y * other.z - z * other.y,
    z * other.x - x * other.z,
    x * other.y - y * other.x
)

// Vector3d.toVector3f() and Vector3d.toVector3i() are provided by the Hytale API.

// ── Vector3f operators ──────────────────────────────────────────────

operator fun Vector3f.plus(other: Vector3f): Vector3f =
    Vector3f(x + other.x, y + other.y, z + other.z)

operator fun Vector3f.minus(other: Vector3f): Vector3f =
    Vector3f(x - other.x, y - other.y, z - other.z)

operator fun Vector3f.times(scalar: Float): Vector3f =
    Vector3f(x * scalar, y * scalar, z * scalar)

operator fun Vector3f.div(scalar: Float): Vector3f =
    Vector3f(x / scalar, y / scalar, z / scalar)

operator fun Vector3f.unaryMinus(): Vector3f =
    Vector3f(-x, -y, -z)

fun Vector3f.lengthSquared(): Float = x * x + y * y + z * z

fun Vector3f.normalized(): Vector3f {
    val len = length()
    return if (len == 0f) this else this / len
}

fun Vector3f.distanceTo(other: Vector3f): Float = (this - other).length()

fun Vector3f.distanceSquaredTo(other: Vector3f): Float = (this - other).lengthSquared()

fun Vector3f.dot(other: Vector3f): Float =
    x * other.x + y * other.y + z * other.z

fun Vector3f.cross(other: Vector3f): Vector3f = Vector3f(
    y * other.z - z * other.y,
    z * other.x - x * other.z,
    x * other.y - y * other.x
)

// Vector3f.toVector3d() is provided by the Hytale API.

fun Vector3f.toVector3i(): Vector3i = Vector3i(x.toInt(), y.toInt(), z.toInt())

// ── Vector3i operators ──────────────────────────────────────────────

operator fun Vector3i.plus(other: Vector3i): Vector3i =
    Vector3i(x + other.x, y + other.y, z + other.z)

operator fun Vector3i.minus(other: Vector3i): Vector3i =
    Vector3i(x - other.x, y - other.y, z - other.z)

operator fun Vector3i.times(scalar: Int): Vector3i =
    Vector3i(x * scalar, y * scalar, z * scalar)

operator fun Vector3i.unaryMinus(): Vector3i =
    Vector3i(-x, -y, -z)

// Vector3i.toVector3d() and Vector3i.toVector3f() are provided by the Hytale API.

// ── Factory helpers ─────────────────────────────────────────────────

/** Creates a [Vector3d] with all components set to [v]. */
fun vec3d(v: Double): Vector3d = Vector3d(v, v, v)

/** Creates a [Vector3d] from individual components. */
fun vec3d(x: Double, y: Double, z: Double): Vector3d = Vector3d(x, y, z)

/** Creates a [Vector3f] with all components set to [v]. */
fun vec3f(v: Float): Vector3f = Vector3f(v, v, v)

/** Creates a [Vector3f] from individual components. */
fun vec3f(x: Float, y: Float, z: Float): Vector3f = Vector3f(x, y, z)

/** Creates a [Vector3i] with all components set to [v]. */
fun vec3i(v: Int): Vector3i = Vector3i(v, v, v)

/** Creates a [Vector3i] from individual components. */
fun vec3i(x: Int, y: Int, z: Int): Vector3i = Vector3i(x, y, z)

/** Linearly interpolates between [this] and [target] by [t] (0.0 .. 1.0). */
fun Vector3d.lerp(target: Vector3d, t: Double): Vector3d =
    this + (target - this) * t

/** Linearly interpolates between [this] and [target] by [t] (0.0 .. 1.0). */
fun Vector3f.lerp(target: Vector3f, t: Float): Vector3f =
    this + (target - this) * t
