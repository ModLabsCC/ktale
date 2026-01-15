package cc.modlabs.ktale.ext

import cc.modlabs.ktale.entities.InternalHytaleEntities
import com.hypixel.hytale.component.AddReason
import com.hypixel.hytale.math.vector.Vector3d
import com.hypixel.hytale.math.vector.Vector3f
import com.hypixel.hytale.protocol.InteractionType
import com.hypixel.hytale.server.core.asset.type.model.config.Model
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset
import com.hypixel.hytale.server.core.entity.UUIDComponent
import com.hypixel.hytale.server.core.entity.entities.ProjectileComponent
import com.hypixel.hytale.server.core.entity.nameplate.Nameplate
import com.hypixel.hytale.server.core.modules.entity.component.*
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId
import com.hypixel.hytale.server.core.modules.interaction.Interactions
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore


/**
 * Spawns an entity in the specified world with the given parameters.
 *
 * @param world The world in which the entity will be spawned.
 * @param entity The type of entity to spawn, represented as an instance of `InternalHytaleEntities`.
 * @param scale The scale factor for the entity's model.
 * @param location The spawn location of the entity, specified as a `Vector3d`.
 * @param interactions A map of interaction types associated with their corresponding action identifiers.
 */
fun spawnEntity(world: World, entity: InternalHytaleEntities, scale: Float, location: Vector3d, interactions: Map<InteractionType, String>) {
    val worldStore = world.entityStore.store

    world.execute {
        val holder = EntityStore.REGISTRY.newHolder()

        val modelAsset = ModelAsset.getAssetMap().getAsset(entity.assetName) ?: return@execute
        val model = Model.createScaledModel(modelAsset, scale)

        holder.addComponent(TransformComponent.getComponentType(), TransformComponent(location, Vector3f(0f, 0f, 0f)))
        holder.addComponent(PersistentModel.getComponentType(), PersistentModel(model.toReference()))
        holder.addComponent(ModelComponent.getComponentType(), ModelComponent(model))
        holder.addComponent(BoundingBox.getComponentType(), BoundingBox(model.boundingBox!!))
        holder.addComponent(NetworkId.getComponentType(), NetworkId(worldStore.externalData.takeNextNetworkId()))

        if (interactions.isNotEmpty()) {
            holder.addComponent(Interactions.getComponentType(), Interactions(interactions))
        }

        holder.ensureComponent(UUIDComponent.getComponentType())
        holder.ensureComponent(Interactable.getComponentType())

        worldStore.addEntity(holder, AddReason.SPAWN)
    }
}

/**
 * Spawns a hologram entity in the specified world at the given position with the provided text and rotation.
 *
 * @param world The world in which the hologram will be spawned.
 * @param text The text content to display on the hologram.
 * @param position The 3D position where the hologram will be spawned.
 * @param rotation The rotational orientation of the hologram.
 */
fun spawnHologram(world: World, text: String, position: Vector3d, rotation: Vector3f) {
    world.execute {
        val holder = EntityStore.REGISTRY.newHolder()
        val projectileComponent = ProjectileComponent("Projectile")
        holder.putComponent(ProjectileComponent.getComponentType(), projectileComponent)
        holder.putComponent(
            TransformComponent.getComponentType(),
            TransformComponent(position, rotation)
        )
        holder.ensureComponent(UUIDComponent.getComponentType())
        holder.ensureComponent(Intangible.getComponentType())
        if (projectileComponent.projectile == null) {
            projectileComponent.initialize()
            if (projectileComponent.projectile == null) {
                return@execute
            }
        }

        holder.addComponent(
            NetworkId.getComponentType(),
            NetworkId(world.entityStore.store.getExternalData().takeNextNetworkId())
        )
        holder.addComponent(Nameplate.getComponentType(), Nameplate(text))
        world.entityStore.store.addEntity(holder, AddReason.SPAWN)
    }
}