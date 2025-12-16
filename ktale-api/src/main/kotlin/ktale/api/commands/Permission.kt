package ktale.api.commands

/**
 * Abstract permission identifier.
 *
 * KTale does not assume any specific permission engine. A platform adapter decides how
 * permissions are checked and represented.
 */
public class Permission public constructor(public val value: String) {
    override fun toString(): String = value

    override fun equals(other: Any?): Boolean = other is Permission && other.value == value

    override fun hashCode(): Int = value.hashCode()

    public companion object {
        /** Java-friendly factory. */
        @JvmStatic
        public fun of(value: String): Permission = Permission(value)
    }
}


