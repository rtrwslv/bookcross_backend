package ru.itport.bookcross.security

import io.jmix.security.model.EntityAttributePolicyAction
import io.jmix.security.model.EntityPolicyAction
import io.jmix.security.role.annotation.EntityAttributePolicy
import io.jmix.security.role.annotation.EntityPolicy
import io.jmix.security.role.annotation.ResourceRole
import io.jmix.security.role.annotation.SpecificPolicy


@ResourceRole(name = "AnonymousAccessRole", code = AnonymousAccessRole.CODE, scope = ["API"])
interface AnonymousAccessRole {


    companion object {
        const val CODE = "anonymous-access-role"
    }

    @EntityAttributePolicy(entityName = "*", attributes = ["*"], action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityName = "*", actions = [EntityPolicyAction.READ])
    @EntityPolicy(entityName = "User", actions = [EntityPolicyAction.CREATE])
    @EntityPolicy(entityName = "UserProfile", actions = [EntityPolicyAction.CREATE])
    @SpecificPolicy(resources = ["rest.enabled", "rest.fileDownload.enabled", "rest.fileUpload.enabled"])
    fun anonymous()
}