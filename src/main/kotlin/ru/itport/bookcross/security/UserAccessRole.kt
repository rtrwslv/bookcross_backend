package ru.itport.bookcross.security

import io.jmix.security.model.EntityAttributePolicyAction
import io.jmix.security.model.EntityPolicyAction
import io.jmix.security.role.annotation.EntityAttributePolicy
import io.jmix.security.role.annotation.EntityPolicy
import io.jmix.security.role.annotation.ResourceRole
import io.jmix.security.role.annotation.SpecificPolicy


@ResourceRole(name = "UserAccessRole", code = UserAccessRole.CODE, scope = ["API"])
interface UserAccessRole {

    companion object {
        const val CODE = "user-access-role"
    }

    @EntityAttributePolicy(entityName = "*", attributes = ["*"], action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityName = "*", actions = [EntityPolicyAction.READ])
    @EntityPolicy(entityName = "TelegramChat", actions = [EntityPolicyAction.READ])
    @EntityPolicy(entityName = "BookingJournal", actions = [EntityPolicyAction.READ])
    @EntityPolicy(entityName = "User", actions = [EntityPolicyAction.READ, EntityPolicyAction.CREATE, EntityPolicyAction.UPDATE, EntityPolicyAction.DELETE])
    @EntityPolicy(entityName = "Book", actions = [EntityPolicyAction.READ, EntityPolicyAction.CREATE, EntityPolicyAction.UPDATE, EntityPolicyAction.DELETE])
    @EntityPolicy(entityName = "BookingJournal", actions = [EntityPolicyAction.CREATE, EntityPolicyAction.UPDATE, EntityPolicyAction.DELETE])
    @EntityPolicy(entityName = "Category", actions = [EntityPolicyAction.READ, EntityPolicyAction.CREATE, EntityPolicyAction.UPDATE, EntityPolicyAction.DELETE])
    @SpecificPolicy(resources = ["rest.enabled", "rest.fileDownload.enabled", "rest.fileUpload.enabled"])
    fun user()
}
